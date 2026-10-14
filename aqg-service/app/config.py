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
    # Off keeps what students write in discussions from ever leaving for the model provider
    discussion_suggestions: bool = True
    # Characters of course material per prompt; a topic with more text spreads it over the questions
    context_chars: int = 12000
    # Writes every prompt sent to the model, and its reply, to the log (for debugging)
    log_prompts: bool = False
    # Protection against overuse. The defaults here are "off" or generous so that tests are not
    # slowed down; from_env() sets the real ones (see example.env).
    # Questions waiting or being written, across all jobs, above which new requests are refused
    max_queued_questions: int = 100
    max_queued_jobs: int = 20
    # Jobs written at the same time (the others wait their turn)
    max_concurrent_jobs: int = 2
    # Questions a course may request per (UTC) day; 0 = no limit
    daily_questions_per_course: int = 0
    # Calls per minute to each cloud provider (NIM, OpenAI, Anthropic); 0 = no limit
    llm_requests_per_minute: int = 0
    # Seconds a job waits for a free slot of the provider's budget, and a discussion suggestion
    rate_limit_wait: float = 600
    interactive_rate_limit_wait: float = 15
    # The Tutor sends this in the X-API-Key header; empty = the service is open to whoever reaches it
    api_key: str = ""
    # Whether a request may choose its own model (api_base and api_key included); the Tutor never does
    allow_request_model: bool = False

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
            discussion_suggestions=os.getenv("DISCUSSION_SUGGESTIONS", "true").lower() != "false",
            context_chars=int(os.getenv("CONTEXT_CHARS", "12000")),
            log_prompts=os.getenv("LOG_PROMPTS", "false").lower() == "true",
            max_queued_questions=int(os.getenv("MAX_QUEUED_QUESTIONS", "100")),
            max_queued_jobs=int(os.getenv("MAX_QUEUED_JOBS", "20")),
            max_concurrent_jobs=int(os.getenv("MAX_CONCURRENT_JOBS", "2")),
            daily_questions_per_course=int(os.getenv("DAILY_QUESTIONS_PER_COURSE", "50")),
            llm_requests_per_minute=int(os.getenv("LLM_REQUESTS_PER_MINUTE", "30")),
            api_key=os.getenv("AQG_API_KEY", ""),
            allow_request_model=os.getenv("ALLOW_REQUEST_MODEL", "false").lower() == "true",
        )


def default_model_config(settings: Settings) -> ModelConfig:
    """The environment's model, before the administrator saves any settings."""
    from app.llm.settings import LlmSettings, model_config_for

    llm = LlmSettings.from_env(settings)
    return model_config_for(llm.primary, llm)
