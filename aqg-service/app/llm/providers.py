"""The model providers a teacher or the administrator can give an API key for: the ones LiteLLM routes to
that write text and need nothing more than a key (no cloud account, endpoint or project).

The list is read from LiteLLM, so it grows with it. Ollama is separate: it has no key, only an address.
"""
import functools
import os
import re

import litellm

# Providers whose models are listed by their own API, with the service's key in its environment
CORE = ("openai", "anthropic", "nvidia_nim")

KEY_VARIABLES = {
    "openai": ["OPENAI_API_KEY"],
    "anthropic": ["ANTHROPIC_API_KEY"],
    "nvidia_nim": ["NVIDIA_NIM_API_KEY"],
}

NAMES = {
    "ollama": "Ollama (local)",
    "openai": "OpenAI",
    "anthropic": "Anthropic",
    "nvidia_nim": "NVIDIA NIM",
    "gemini": "Google Gemini",
    "groq": "Groq",
    "mistral": "Mistral AI",
    "deepseek": "DeepSeek",
    "openrouter": "OpenRouter",
    "together_ai": "Together AI",
    "fireworks_ai": "Fireworks AI",
    "perplexity": "Perplexity",
    "xai": "xAI (Grok)",
    "cohere": "Cohere",
    "cerebras": "Cerebras",
    "sambanova": "SambaNova",
    "deepinfra": "DeepInfra",
    "nebius": "Nebius",
    "hyperbolic": "Hyperbolic",
    "moonshot": "Moonshot AI",
    "zai": "Z.AI",
    "ai21": "AI21",
}

# The model each is tried with when a key is given and no model is named, if LiteLLM lists it
PREFERRED_MODELS = {
    "openai": "gpt-4o-mini",
    "anthropic": "claude-sonnet-5",
    "nvidia_nim": "nvidia/nemotron-3-ultra-550b-a55b",
    "gemini": "gemini-2.5-flash",
    "groq": "llama-3.3-70b-versatile",
    "mistral": "mistral-small-latest",
    "deepseek": "deepseek-chat",
    "openrouter": "openai/gpt-4o-mini",
    "together_ai": "meta-llama/Llama-3.3-70B-Instruct-Turbo",
    "perplexity": "sonar",
    "xai": "grok-3-mini",
    "cerebras": "llama-3.3-70b",
}

# They need more than an API key (cloud credentials, an endpoint, a project or an account) or are not
# a service a key is given for, so a key alone would not work
NEEDS_MORE_THAN_A_KEY = {
    "bedrock", "bedrock_mantle", "sagemaker", "sagemaker_chat", "sagemaker_nova", "vertex_ai", "vertex_ai_beta",
    "azure", "azure_text", "azure_ai", "azure_anthropic", "watsonx", "watsonx_text", "oci", "sap", "snowflake",
    "databricks", "datarobot", "github_copilot", "chatgpt", "litellm_proxy", "custom", "custom_openai", "openai_like",
    "hosted_vllm", "vllm", "triton", "llamafile", "lm_studio", "xinference", "docker_model_runner", "lemonade",
    "cloudflare", "heroku", "gigachat", "oobabooga", "petals", "ollama", "ollama_chat", "amazon_nova", "a2a",
    "a2a_agent", "langgraph", "langflow", "litellm_agent", "auto_router", "dotprompt", "cursor", "gdc",
}

CHAT_MODES = {"chat", "completion"}
_NOT_FOR_TEXT = re.compile(
    r"embed|rerank|retriever|reward|guard|safety|parse|clip|deplot|whisper|tts|dall-e|moderation|transcribe|audio|"
    r"realtime|image|sora|davinci|babbage|veo|imagen|lyria|orpheus",
    re.IGNORECASE,
)


@functools.lru_cache(maxsize=1)
def _routable() -> frozenset[str]:
    return frozenset(str(p.value) for p in litellm.provider_list)


@functools.lru_cache(maxsize=1)
def cloud_providers() -> tuple[str, ...]:
    """The providers a key can be given for, by name."""
    routable = _routable()
    found = {"openrouter"} | set(CORE)
    for info in litellm.model_cost.values():
        provider, mode = info.get("litellm_provider"), info.get("mode")
        if mode in CHAT_MODES and provider in routable and provider not in NEEDS_MORE_THAN_A_KEY:
            found.add(provider)
    found = {p for p in found if not p.startswith("text-completion")}
    return tuple(sorted(found, key=lambda p: provider_name(p).lower()))


def supported_providers() -> list[str]:
    """Ollama first, then every provider a key can be given for."""
    return ["ollama", *cloud_providers()]


def is_supported(provider: str) -> bool:
    return provider == "ollama" or provider in cloud_providers()


def provider_name(provider: str) -> str:
    return NAMES.get(provider) or provider.replace("_ai", " AI").replace("_", " ").title()


@functools.lru_cache(maxsize=None)
def key_variables(provider: str) -> tuple[str, ...]:
    """The environment variables the service reads the key of a provider from (any of them will do)."""
    if provider in KEY_VARIABLES:
        return tuple(KEY_VARIABLES[provider])
    try:
        missing = litellm.validate_environment(model=f"{provider}/x").get("missing_keys") or []
    except Exception:  # an unknown provider has no variable to name
        missing = []
    return tuple(missing) or (f"{provider.upper()}_API_KEY",)


def service_key(provider: str) -> str | None:
    """The service's own key of a provider, from its environment."""
    return next((os.getenv(variable) for variable in key_variables(provider) if os.getenv(variable)), None)


def model_name(provider: str, model: str) -> str:
    """The name LiteLLM routes on."""
    if provider == "ollama":
        return f"ollama_chat/{model}"
    if provider == "openai":
        return model
    return f"{provider}/{model}"


@functools.lru_cache(maxsize=None)
def suggested_models(provider: str) -> tuple[str, ...]:
    """The text models LiteLLM knows of a provider (a static list: it may lag behind the provider)."""
    prefix = f"{provider}/"
    models = set()
    for name, info in litellm.model_cost.items():
        if info.get("litellm_provider") != provider or info.get("mode") not in CHAT_MODES:
            continue
        short = name[len(prefix):] if name.startswith(prefix) else name
        if not _NOT_FOR_TEXT.search(short):
            models.add(short)
    return tuple(sorted(models))


def default_model(provider: str) -> str | None:
    """A model to try a key with, or None when nothing is known of the provider."""
    listed = suggested_models(provider)
    preferred = PREFERRED_MODELS.get(provider)
    if preferred and (preferred in listed or provider in CORE or not listed):
        return preferred
    return listed[0] if listed else preferred
