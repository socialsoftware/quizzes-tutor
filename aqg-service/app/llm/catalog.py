"""The models each provider offers, so the administrator picks one instead of typing its name.

Every provider has a list call: NVIDIA NIM and OpenAI answer GET /v1/models, Anthropic GET
/v1/models (paginated, with its own headers) and Ollama /api/tags. The keys come from the
environment, like for the calls that write questions.
"""
import os
import re
import time

import httpx

from app.llm.ollama import OllamaClient
from app.llm import providers

# Models in these lists that cannot write a question: embeddings, rerankers, safety classifiers,
# document parsers, speech and image models
NOT_FOR_TEXT = re.compile(
    r"embed|rerank|retriever|reward|guard|safety|content-safety|parse|clip|deplot|whisper|tts|dall-e|"
    r"moderation|transcribe|audio|realtime|image|sora|davinci|babbage",
    re.IGNORECASE,
)

DEFAULT_BASES = {
    "nvidia_nim": ("NVIDIA_NIM_API_BASE", "https://integrate.api.nvidia.com/v1"),
    "openai": ("OPENAI_API_BASE", "https://api.openai.com/v1"),
    "anthropic": ("ANTHROPIC_API_BASE", "https://api.anthropic.com/v1"),
    "gemini": ("GEMINI_API_BASE", "https://generativelanguage.googleapis.com/v1beta"),
}

CACHE_SECONDS = 600
TIMEOUT_SECONDS = 15


class NoApiKey(Exception):
    pass


class ModelCatalog:
    def __init__(self, ollama: OllamaClient | None = None, http=httpx):
        self.ollama = ollama or OllamaClient()
        self.http = http
        # provider -> (when it was read, models); lists change rarely and the calls are slow
        self._cache: dict[str, tuple[float, list[str]]] = {}

    def models(self, provider: str, ollama_base_url: str, refresh: bool = False, key: str | None = None) -> list[str]:
        if provider == "ollama":
            # What is installed changes with every download: never cached
            return self.ollama.list_models(ollama_base_url)
        cached = self._cache.get(provider)
        if cached and not refresh and time.monotonic() - cached[0] < CACHE_SECONDS:
            return cached[1]
        models = sorted({model for model in self._read(provider, key) if not NOT_FOR_TEXT.search(model)})
        self._cache[provider] = (time.monotonic(), models)
        return models

    def _read(self, provider: str, given_key: str | None = None) -> list[str]:
        if provider not in DEFAULT_BASES:
            # The others have no list call this service knows: what LiteLLM knows of them
            return list(providers.suggested_models(provider))
        key = given_key or providers.service_key(provider)
        if not key and provider == "gemini":
            # Without a key there is no live list; LiteLLM's may name models that are gone
            return list(providers.suggested_models(provider))
        if not key:
            raise NoApiKey(f"no API key for {provider} in the service's environment")
        variable, default = DEFAULT_BASES[provider]
        base = (os.getenv(variable) or default).rstrip("/")
        if provider == "anthropic":
            return self._read_anthropic(base, key)
        if provider == "gemini":
            return self._read_gemini(base, key)
        response = self.http.get(f"{base}/models", headers={"Authorization": f"Bearer {key}"}, timeout=TIMEOUT_SECONDS)
        response.raise_for_status()
        return [model["id"] for model in response.json().get("data", [])]

    def live_models(self, provider: str, key: str | None = None) -> list[str]:
        """The models a provider lists now, for a key; never cached, since the key may be one being checked."""
        return sorted({model for model in self._read(provider, key) if not NOT_FOR_TEXT.search(model)})

    def _read_gemini(self, base: str, key: str) -> list[str]:
        """Gemini lists `models/<name>` with the methods each supports; only those that generate text count."""
        models: list[str] = []
        params: dict = {"pageSize": 1000}
        while True:
            response = self.http.get(f"{base}/models", headers={"x-goog-api-key": key}, params=params, timeout=TIMEOUT_SECONDS)
            response.raise_for_status()
            page = response.json()
            models.extend(
                model["name"].removeprefix("models/")
                for model in page.get("models", [])
                if "generateContent" in model.get("supportedGenerationMethods", ["generateContent"])
            )
            if not page.get("nextPageToken"):
                return models
            params = {"pageSize": 1000, "pageToken": page["nextPageToken"]}

    def _read_anthropic(self, base: str, key: str) -> list[str]:
        headers = {"x-api-key": key, "anthropic-version": "2023-06-01"}
        models: list[str] = []
        params: dict = {"limit": 1000}
        while True:
            response = self.http.get(f"{base}/models", headers=headers, params=params, timeout=TIMEOUT_SECONDS)
            response.raise_for_status()
            page = response.json()
            models.extend(model["id"] for model in page.get("data", []))
            if not page.get("has_more") or not page.get("last_id"):
                return models
            params = {"limit": 1000, "after_id": page["last_id"]}
