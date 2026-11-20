import pytest
from fastapi.testclient import TestClient

from app.api.main import create_app
from app.config import Settings
from app.llm.catalog import ModelCatalog, NoApiKey


class Response:
    def __init__(self, body, status=200):
        self.body = body
        self.status = status

    def json(self):
        return self.body

    def raise_for_status(self):
        if self.status >= 400:
            raise RuntimeError(f"HTTP {self.status}")


class FakeHttp:
    """Answers GET requests from a list of scripted replies and remembers what was asked."""

    def __init__(self, *replies):
        self.replies = list(replies)
        self.calls = []

    def get(self, url, headers=None, params=None, timeout=None):
        self.calls.append({"url": url, "headers": headers, "params": params})
        return self.replies.pop(0)


class FakeOllama:
    def list_models(self, base_url):
        return ["llama3.1:8b", "qwen3:4b"]


NIM_MODELS = {"data": [
    {"id": "nvidia/nemotron-3-ultra-550b-a55b"},
    {"id": "meta/llama-3.3-70b-instruct"},
    {"id": "nvidia/nv-embedqa-mistral-7b-v2"},
    {"id": "nvidia/llama-3.1-nemoguard-8b-content-safety"},
    {"id": "nvidia/nemotron-parse"},
    {"id": "nvidia/rerank-qa-mistral-4b"},
    {"id": "nvidia/nemotron-4-340b-reward"},
]}


@pytest.fixture
def keys(monkeypatch):
    monkeypatch.setenv("NVIDIA_NIM_API_KEY", "nim-key")
    monkeypatch.setenv("OPENAI_API_KEY", "openai-key")
    monkeypatch.setenv("ANTHROPIC_API_KEY", "anthropic-key")
    for variable in ("NVIDIA_NIM_API_BASE", "OPENAI_API_BASE", "ANTHROPIC_API_BASE"):
        monkeypatch.delenv(variable, raising=False)


def test_nim_lists_only_the_models_that_write_text(keys):
    http = FakeHttp(Response(NIM_MODELS))

    models = ModelCatalog(FakeOllama(), http).models("nvidia_nim", "http://ollama:11434")

    assert models == ["meta/llama-3.3-70b-instruct", "nvidia/nemotron-3-ultra-550b-a55b"]
    assert http.calls[0]["url"] == "https://integrate.api.nvidia.com/v1/models"
    assert http.calls[0]["headers"] == {"Authorization": "Bearer nim-key"}


def test_openai_leaves_out_speech_image_and_embedding_models(keys):
    http = FakeHttp(Response({"data": [{"id": "gpt-4o-mini"}, {"id": "whisper-1"}, {"id": "dall-e-3"},
                                       {"id": "text-embedding-3-small"}, {"id": "tts-1"}, {"id": "o4-mini"}]}))

    assert ModelCatalog(FakeOllama(), http).models("openai", "") == ["gpt-4o-mini", "o4-mini"]
    assert http.calls[0]["url"] == "https://api.openai.com/v1/models"


def test_anthropic_reads_every_page_with_its_own_headers(keys):
    http = FakeHttp(
        Response({"data": [{"id": "claude-sonnet-5"}], "has_more": True, "last_id": "claude-sonnet-5"}),
        Response({"data": [{"id": "claude-haiku-4-5"}], "has_more": False, "last_id": "claude-haiku-4-5"}),
    )

    models = ModelCatalog(FakeOllama(), http).models("anthropic", "")

    assert models == ["claude-haiku-4-5", "claude-sonnet-5"]
    assert http.calls[0]["headers"] == {"x-api-key": "anthropic-key", "anthropic-version": "2023-06-01"}
    assert http.calls[1]["params"]["after_id"] == "claude-sonnet-5"


def test_ollama_lists_what_the_server_has_installed():
    assert ModelCatalog(FakeOllama(), FakeHttp()).models("ollama", "http://ollama:11434") == ["llama3.1:8b", "qwen3:4b"]


def test_lists_are_kept_for_a_while_unless_a_refresh_is_asked(keys):
    http = FakeHttp(Response(NIM_MODELS), Response({"data": [{"id": "new/model"}]}))
    catalog = ModelCatalog(FakeOllama(), http)

    catalog.models("nvidia_nim", "")
    catalog.models("nvidia_nim", "")
    assert len(http.calls) == 1

    assert catalog.models("nvidia_nim", "", refresh=True) == ["new/model"]


def test_a_provider_without_its_key_is_not_called(monkeypatch):
    monkeypatch.delenv("OPENAI_API_KEY", raising=False)
    http = FakeHttp()

    with pytest.raises(NoApiKey):
        ModelCatalog(FakeOllama(), http).models("openai", "")
    assert http.calls == []


def test_the_api_answers_with_the_list_or_with_why_there_is_none(keys, monkeypatch):
    settings = Settings(provider="ollama", model="m", ollama_base_url="http://ollama:11434", max_retries=1)
    http = FakeHttp(Response(NIM_MODELS), Response({}, status=401))
    client = TestClient(create_app(settings, catalog=ModelCatalog(FakeOllama(), http)))

    listed = client.get("/settings/llm/models", params={"provider": "nvidia_nim"}).json()
    assert listed == {"provider": "nvidia_nim", "models": ["meta/llama-3.3-70b-instruct", "nvidia/nemotron-3-ultra-550b-a55b"], "error": None}

    failed = client.get("/settings/llm/models", params={"provider": "openai"}).json()
    assert failed["models"] == [] and "401" in failed["error"]
    assert "openai-key" not in str(failed)

    assert client.get("/settings/llm/models", params={"provider": "carrier-pigeon"}).status_code == 422


GEMINI_LIST = {"models": [
    {"name": "models/gemini-2.5-flash", "supportedGenerationMethods": ["generateContent"]},
    {"name": "models/gemini-3.5-flash", "supportedGenerationMethods": ["generateContent", "countTokens"]},
    {"name": "models/gemini-3.5-flash-lite", "supportedGenerationMethods": ["generateContent"]},
    {"name": "models/gemini-3.6-flash", "supportedGenerationMethods": ["generateContent"]},
    {"name": "models/gemini-3.7-flash-preview", "supportedGenerationMethods": ["generateContent"]},
    {"name": "models/gemini-embedding-001", "supportedGenerationMethods": ["embedContent"]},
]}


def test_gemini_lists_what_the_key_can_use_now_and_only_models_that_generate_text(monkeypatch):
    monkeypatch.delenv("GEMINI_API_BASE", raising=False)
    http = FakeHttp(Response(GEMINI_LIST))

    models = ModelCatalog(FakeOllama(), http).models("gemini", "", key="g-key")

    assert "gemini-embedding-001" not in models
    assert "gemini-3.6-flash" in models
    assert http.calls[0]["url"] == "https://generativelanguage.googleapis.com/v1beta/models"
    assert http.calls[0]["headers"] == {"x-goog-api-key": "g-key"}


def test_the_model_a_gemini_key_is_tried_with_is_the_newest_stable_flash():
    from app.llm.catalog import usual_gemini

    assert usual_gemini(["gemini-2.5-flash", "gemini-3.6-flash", "gemini-3.7-flash-preview", "gemini-3.6-flash-lite"]) == "gemini-3.6-flash"
    assert usual_gemini(["gemini-3.6-flash-lite", "gemini-2.5-pro"]) == "gemini-3.6-flash-lite"
    assert usual_gemini(["gemini-x-preview"]) == "gemini-x-preview"
    assert usual_gemini([]) is None


def test_a_gemini_key_is_not_refused_because_the_default_model_was_withdrawn(monkeypatch):
    from tests.test_llm_settings import FakeLLM, Recorder, settings

    class Listing(ModelCatalog):
        def live_models(self, provider, key=None):
            return ["gemini-3.6-flash", "gemini-3.5-flash-lite"]

    factory = Recorder({"gemini/gemini-3.6-flash": FakeLLM([{"ok": True}])})
    client = TestClient(create_app(settings(database_url="sqlite://"), provider_factory=factory, catalog=Listing(FakeOllama(), FakeHttp())))

    result = client.post("/settings/llm/test", json={"provider": "gemini", "model": "gemini-2.5-flash", "apiKey": "g-key"}).json()

    assert result["ok"] is True
    assert factory.configs[-1].model == "gemini/gemini-3.6-flash"


def _gemini_client(factory):
    from tests.test_llm_settings import settings

    class Listing(ModelCatalog):
        def live_models(self, provider, key=None):
            return ["gemini-3.8-flash", "gemini-3.5-flash", "gemini-2.5-flash", "gemini-2.5-pro"]

    return TestClient(create_app(settings(database_url="sqlite://"), provider_factory=factory, catalog=Listing(FakeOllama(), FakeHttp())))


def test_a_gemini_key_is_accepted_when_the_first_model_is_overloaded_and_the_next_answers():
    from tests.test_llm_settings import FakeLLM, Recorder

    class Overloaded:
        def complete(self, system, user):
            raise RuntimeError("503 UNAVAILABLE: This model is currently experiencing high demand")

    factory = Recorder({"gemini/gemini-3.8-flash": Overloaded(), "gemini/gemini-3.5-flash": FakeLLM([{"ok": True}])})

    result = _gemini_client(factory).post("/settings/llm/test", json={"provider": "gemini", "model": "gemini-3.8-flash", "apiKey": "g"}).json()

    assert result["ok"] is True
    assert [c.model for c in factory.configs] == ["gemini/gemini-3.8-flash", "gemini/gemini-3.5-flash"]


def test_a_gemini_key_the_provider_rejects_stops_at_the_first_model():
    from tests.test_llm_settings import Recorder

    class Rejected:
        def complete(self, system, user):
            raise RuntimeError("400 API key not valid. Please pass a valid API key. API_KEY_INVALID")

    factory = Recorder({"gemini/gemini-3.8-flash": Rejected()})

    result = _gemini_client(factory).post("/settings/llm/test", json={"provider": "gemini", "model": "gemini-3.8-flash", "apiKey": "g"}).json()

    assert result["ok"] is False
    assert len(factory.configs) == 1
