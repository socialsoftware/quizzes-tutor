import os
from dataclasses import dataclass

from app.schema import ModelConfig

PROVIDERS = ("ollama", "openai", "anthropic", "nvidia_nim")

DEFAULT_MODELS = {
    "ollama": "llama3.1:8b",
    "openai": "gpt-4o-mini",
    "anthropic": "claude-sonnet-5",
    "nvidia_nim": "meta/llama-3.1-70b-instruct",
}


@dataclass(frozen=True)
class Settings:
    provider: str
    model: str
    ollama_base_url: str
    max_retries: int

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
        )


def default_model_config(settings: Settings) -> ModelConfig:
    """Cloud API keys are left to LiteLLM, which reads OPENAI_API_KEY, ANTHROPIC_API_KEY
    and NVIDIA_NIM_API_KEY from the environment."""
    if settings.provider == "ollama":
        return ModelConfig(model=f"ollama/{settings.model}", api_base=settings.ollama_base_url)
    if settings.provider == "nvidia_nim":
        return ModelConfig(model=f"nvidia_nim/{settings.model}")
    if settings.provider == "anthropic":
        return ModelConfig(model=f"anthropic/{settings.model}")
    return ModelConfig(model=settings.model)
