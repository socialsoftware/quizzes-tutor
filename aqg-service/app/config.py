import os
from dataclasses import dataclass
from pathlib import Path

from app.schema import ModelConfig

PROVIDERS = ("ollama", "openai", "anthropic", "nvidia_nim")

DEFAULT_MODELS = {
    "ollama": "llama3.1:8b",
    "openai": "gpt-4o-mini",
    "anthropic": "claude-sonnet-5",
    "nvidia_nim": "nvidia/nemotron-3-ultra-550b-a55b",
}


def _choice(name: str, default: str, allowed: tuple[str, ...]) -> str:
    value = os.getenv(name, default)
    if value not in allowed:
        raise ValueError(f"{name} must be one of {allowed}, got {value!r}")
    return value


@dataclass(frozen=True)
class Settings:
    provider: str
    model: str
    ollama_base_url: str
    max_retries: int
    materials_dir: Path = Path("/data/materials")
    pdf_parser: str = "pymupdf"
    office_parser: str = "markitdown"
    keep_originals: bool = True
    database_url: str = "sqlite://"
    llm_timeout: float = 120
    llm_thinking: bool = False

    @staticmethod
    def from_env() -> "Settings":
        provider = os.getenv("LLM_PROVIDER", "ollama")
        if provider not in PROVIDERS:
            raise ValueError(f"LLM_PROVIDER must be one of {PROVIDERS}, got {provider!r}")
        return Settings(
            provider=provider,
            model=os.getenv("LLM_MODEL") or DEFAULT_MODELS[provider],
            ollama_base_url=os.getenv("OLLAMA_BASE_URL", "http://ollama:11434"),
            max_retries=int(os.getenv("VERIFICATION_MAX_RETRIES", "2")),
            materials_dir=Path(os.getenv("MATERIALS_DIR", "/data/materials")),
            pdf_parser=_choice("PDF_PARSER", "pymupdf", ("pymupdf", "marker")),
            office_parser=_choice("OFFICE_PARSER", "markitdown", ("markitdown", "marker")),
            keep_originals=os.getenv("KEEP_ORIGINALS", "true").lower() != "false",
            database_url=os.getenv("DATABASE_URL", "sqlite:///aqg.db"),
            llm_timeout=float(os.getenv("LLM_TIMEOUT", "120")),
            llm_thinking=os.getenv("LLM_THINKING", "false").lower() == "true",
        )


def default_model_config(settings: Settings) -> ModelConfig:
    """Cloud API keys are left to LiteLLM, which reads OPENAI_API_KEY, ANTHROPIC_API_KEY
    and NVIDIA_NIM_API_KEY from the environment."""
    timeout = settings.llm_timeout
    if settings.provider == "ollama":
        return ModelConfig(
            model=f"ollama/{settings.model}", api_base=settings.ollama_base_url, timeout=timeout
        )
    if settings.provider == "nvidia_nim":
        # Reasoning models on NIM (GLM, Nemotron...) think before answering by default, which
        # made each call take ~30-60 s instead of ~2 s; the JSON replies are just as valid
        # without it. Models without the switch ignore the template argument.
        extra_body = None if settings.llm_thinking else {"chat_template_kwargs": {"enable_thinking": False}}
        return ModelConfig(model=f"nvidia_nim/{settings.model}", timeout=timeout, extra_body=extra_body)
    if settings.provider == "anthropic":
        return ModelConfig(model=f"anthropic/{settings.model}", timeout=timeout)
    return ModelConfig(model=settings.model, timeout=timeout)
