"""Which models the service uses, editable at run time by the Tutor's administrator.

The environment gives the starting values; once the administrator saves settings they are kept
in the database and win over the environment. API keys are never part of them: they stay in the
environment (OPENAI_API_KEY, ANTHROPIC_API_KEY, NVIDIA_NIM_API_KEY), and only whether each one is
set is ever shown.
"""
import os
from typing import Literal
from urllib.parse import urlparse

from pydantic import BaseModel, ConfigDict, Field, field_validator
from pydantic.alias_generators import to_camel
from sqlalchemy.orm import sessionmaker

from app.config import DEFAULT_MODELS, PROVIDERS, Settings
from app.db import SettingRow
from app.schema import ModelConfig

Provider = Literal["ollama", "openai", "anthropic", "nvidia_nim"]

# The environment variable LiteLLM reads each provider's key from; Ollama needs none
API_KEY_VARIABLES = {
    "openai": "OPENAI_API_KEY",
    "anthropic": "ANTHROPIC_API_KEY",
    "nvidia_nim": "NVIDIA_NIM_API_KEY",
}

MAX_FALLBACKS = 5
SETTING_KEY = "llm"


class CamelModel(BaseModel):
    """Read and written in camelCase, like the rest of the Tutor's JSON."""

    model_config = ConfigDict(alias_generator=to_camel, populate_by_name=True)


class ModelChoice(CamelModel):
    provider: Provider
    model: str = Field(min_length=1, max_length=200)

    @field_validator("model")
    @classmethod
    def strip(cls, model: str) -> str:
        model = model.strip()
        if not model:
            raise ValueError("the model needs a name")
        return model


class LlmSettings(CamelModel):
    primary: ModelChoice
    # Tried in order when a call to the model before fails (outage, overload, timeout, model withdrawn)
    fallbacks: list[ModelChoice] = Field(default_factory=list, max_length=MAX_FALLBACKS)
    ollama_base_url: str = Field(max_length=500)
    # Seconds before a call is abandoned
    timeout: float = Field(ge=5, le=900)
    # Keep the chain of thought of reasoning models on NIM (much slower)
    thinking: bool = False
    # Rewrites of a draft that failed the checks before it goes to the teacher as it is
    max_retries: int = Field(ge=0, le=5)
    # Off keeps what students write in discussions from ever leaving for the model provider
    discussion_suggestions: bool = True
    # Characters of course material per prompt
    context_chars: int = Field(ge=2000, le=60000)

    @field_validator("ollama_base_url")
    @classmethod
    def http_url(cls, url: str) -> str:
        url = url.strip().rstrip("/")
        parsed = urlparse(url)
        if parsed.scheme not in ("http", "https") or not parsed.netloc:
            raise ValueError("the Ollama address must be an http(s) URL, e.g. http://ollama:11434")
        return url

    @staticmethod
    def from_env(settings: Settings) -> "LlmSettings":
        # The bounds above are for what the administrator types; the environment is trusted as it is
        return LlmSettings.model_construct(
            primary=ModelChoice(provider=settings.provider, model=settings.model),
            fallbacks=[],
            ollama_base_url=settings.ollama_base_url,
            timeout=settings.llm_timeout,
            thinking=settings.llm_thinking,
            max_retries=settings.max_retries,
            discussion_suggestions=settings.discussion_suggestions,
            context_chars=settings.context_chars,
        )

    def choices(self) -> list[ModelChoice]:
        return [self.primary, *self.fallbacks]

    def model_configs(self) -> list[ModelConfig]:
        return [model_config_for(choice, self) for choice in self.choices()]


class LlmSettingsView(CamelModel):
    settings: LlmSettings
    # Whether each provider can be used: its key is set in the service's environment (never the key)
    keys: dict[str, bool]
    providers: list[str]
    default_models: dict[str, str]


class ModelTestResult(CamelModel):
    ok: bool
    seconds: float
    reply: str | None = None
    error: str | None = None


class ProviderModels(CamelModel):
    """The models a provider offers for writing text; `error` says why the list could not be read."""

    provider: str
    models: list[str] = Field(default_factory=list)
    error: str | None = None


class OllamaModels(CamelModel):
    models: list[str] = Field(default_factory=list)
    # Model -> "downloading", "done" or the error of its last download
    pulls: dict[str, str] = Field(default_factory=dict)
    error: str | None = None


def keys_present() -> dict[str, bool]:
    return {provider: provider == "ollama" or bool(os.getenv(API_KEY_VARIABLES[provider])) for provider in PROVIDERS}


def model_config_for(choice: ModelChoice, llm: LlmSettings) -> ModelConfig:
    """LiteLLM routes on the model prefix; cloud API keys are left to it, read from the environment."""
    timeout = llm.timeout
    if choice.provider == "ollama":
        # ollama_chat/ uses Ollama's chat endpoint, so the system prompt stays a system message
        return ModelConfig(model=f"ollama_chat/{choice.model}", api_base=llm.ollama_base_url, timeout=timeout)
    if choice.provider == "nvidia_nim":
        # Reasoning models on NIM (GLM, Nemotron...) think before answering by default, which
        # made each call take ~30-60 s instead of ~2 s; the JSON replies are just as valid
        # without it. Models without the switch ignore the template argument.
        extra_body = None if llm.thinking else {"chat_template_kwargs": {"enable_thinking": False}}
        return ModelConfig(model=f"nvidia_nim/{choice.model}", timeout=timeout, extra_body=extra_body)
    if choice.provider == "anthropic":
        return ModelConfig(model=f"anthropic/{choice.model}", timeout=timeout)
    return ModelConfig(model=choice.model, timeout=timeout)


class LlmSettingsStore:
    """The administrator's settings, or the environment's until there are some."""

    def __init__(self, session_factory: sessionmaker, defaults: LlmSettings) -> None:
        self._session = session_factory
        self.defaults = defaults

    def get(self) -> LlmSettings:
        with self._session() as session:
            row = session.get(SettingRow, SETTING_KEY)
            if row is None:
                return self.defaults
            try:
                return LlmSettings.model_validate(row.value)
            except ValueError:
                # Settings saved by an older version that no longer validate fall back to the environment
                return self.defaults

    def save(self, settings: LlmSettings) -> None:
        value = settings.model_dump(mode="json")
        with self._session.begin() as session:
            row = session.get(SettingRow, SETTING_KEY)
            if row is None:
                session.add(SettingRow(key=SETTING_KEY, value=value))
            else:
                row.value = value

    def view(self) -> LlmSettingsView:
        return LlmSettingsView(
            settings=self.get(), keys=keys_present(), providers=list(PROVIDERS), default_models=dict(DEFAULT_MODELS)
        )


def missing_keys(settings: LlmSettings) -> list[str]:
    """Providers chosen whose key is not in the environment: calls to them could only fail."""
    present = keys_present()
    return list(dict.fromkeys(choice.provider for choice in settings.choices() if not present[choice.provider]))
