import pytest
from fastapi.testclient import TestClient

from app.api.main import create_app
from app.config import Settings
from app.llm.provider import FallbackProvider
from tests.conftest import CLEAN_DISTRACTORS, GROUNDED, FakeLLM, good_question

CHUNKS = [{"id": "c1", "text": "404 means the server cannot find the resource."}]


def settings(**overrides):
    values = dict(provider="ollama", model="llama3.1:8b", ollama_base_url="http://ollama:11434", max_retries=1)
    values.update(overrides)
    return Settings(**values)


class Recorder:
    """A provider factory that hands out scripted models and remembers which configs it was given."""

    def __init__(self, llms_by_model=None, default=None):
        self.llms_by_model = llms_by_model or {}
        self.default = default or FakeLLM([])
        self.configs = []

    def __call__(self, config):
        self.configs.append(config)
        llm = self.llms_by_model.get(config.model, self.default)
        llm.model_id = config.model
        return llm


class FakeOllama:
    def __init__(self, models=None, fail=None):
        self.models = models or []
        self.fail = fail
        self.pulled = []

    def list_models(self, base_url):
        if self.fail:
            raise ConnectionError(self.fail)
        return self.models

    def pull(self, base_url, model):
        self.pulled.append((base_url, model))
        self.models.append(model)


def make_client(factory=None, ollama=None, database_url="sqlite://", **overrides):
    return TestClient(
        create_app(settings(database_url=database_url, **overrides), provider_factory=factory or Recorder(), ollama=ollama or FakeOllama())
    )


NEW_SETTINGS = {
    "primary": {"provider": "ollama", "model": "qwen3:4b"},
    "fallbacks": [{"provider": "ollama", "model": "llama3.2:3b"}],
    "ollamaBaseUrl": "http://gpu-node:11434/",
    "timeout": 300,
    "thinking": False,
    "maxRetries": 2,
    "discussionSuggestions": True,
    "contextChars": 8000,
}


def test_settings_start_from_the_environment_and_show_which_keys_are_set(monkeypatch):
    monkeypatch.setenv("NVIDIA_NIM_API_KEY", "secret-value")
    monkeypatch.delenv("OPENAI_API_KEY", raising=False)
    monkeypatch.delenv("ANTHROPIC_API_KEY", raising=False)

    view = make_client(llm_timeout=90).get("/settings/llm").json()

    assert view["settings"]["primary"] == {"provider": "ollama", "model": "llama3.1:8b"}
    assert view["settings"]["ollamaBaseUrl"] == "http://ollama:11434"
    assert view["settings"]["timeout"] == 90
    assert view["keys"] == {"ollama": True, "openai": False, "anthropic": False, "nvidia_nim": True}
    assert "secret-value" not in str(view)


def test_saved_settings_are_used_for_the_next_jobs_and_survive_a_restart(tmp_path):
    database = f"sqlite:///{tmp_path / 'aqg.db'}"
    factory = Recorder(default=FakeLLM([good_question(), GROUNDED, CLEAN_DISTRACTORS]))
    client = make_client(factory, database_url=database)

    saved = client.put("/settings/llm", json=NEW_SETTINGS)
    assert saved.status_code == 200
    assert saved.json()["settings"]["ollamaBaseUrl"] == "http://gpu-node:11434"

    job = client.post("/generate", json={"course_id": 1, "topic": "HTTP", "chunks": CHUNKS}).json()
    assert job["model_id"] == "ollama_chat/qwen3:4b"
    assert [config.model for config in factory.configs] == ["ollama_chat/qwen3:4b", "ollama_chat/llama3.2:3b"]
    assert factory.configs[0].api_base == "http://gpu-node:11434"
    assert factory.configs[0].timeout == 300

    again = make_client(database_url=database).get("/settings/llm").json()
    assert again["settings"]["primary"]["model"] == "qwen3:4b"


def test_a_provider_without_its_key_cannot_be_chosen(monkeypatch):
    monkeypatch.delenv("OPENAI_API_KEY", raising=False)
    body = {**NEW_SETTINGS, "fallbacks": [{"provider": "openai", "model": "gpt-4o-mini"}]}

    response = make_client().put("/settings/llm", json=body)

    assert response.status_code == 422
    assert "no API key for openai" in response.json()["detail"]


@pytest.mark.parametrize(
    "change",
    [
        {"ollamaBaseUrl": "file:///etc/passwd"},
        {"timeout": 1},
        {"maxRetries": 9},
        {"contextChars": 10},
        {"primary": {"provider": "ollama", "model": "  "}},
        {"primary": {"provider": "carrier-pigeon", "model": "x"}},
        {"fallbacks": [{"provider": "ollama", "model": f"m{i}"} for i in range(6)]},
    ],
)
def test_invalid_settings_are_refused(change):
    assert make_client().put("/settings/llm", json={**NEW_SETTINGS, **change}).status_code == 422


def test_a_failing_model_hands_over_to_the_fallback_and_the_job_says_who_wrote_it():
    class Down:
        def complete(self, system, user):
            raise TimeoutError("overloaded")

    backup = FakeLLM([good_question(), GROUNDED, CLEAN_DISTRACTORS])
    factory = Recorder({"ollama_chat/qwen3:4b": Down(), "ollama_chat/llama3.2:3b": backup})
    client = make_client(factory)
    client.put("/settings/llm", json=NEW_SETTINGS)

    job_id = client.post("/generate", json={"course_id": 1, "topic": "HTTP", "chunks": CHUNKS}).json()["id"]
    job = client.get(f"/jobs/{job_id}").json()

    assert job["status"] == "DONE"
    assert job["outcomes"][0]["status"] == "OK"
    assert job["model_id"] == "ollama_chat/llama3.2:3b"


def test_the_fallback_provider_raises_the_last_failure_when_every_model_fails():
    class Down:
        model_id = "down"

        def __init__(self, message):
            self.message = message

        def complete(self, system, user):
            raise ConnectionError(self.message)

    with pytest.raises(ConnectionError, match="second"):
        FallbackProvider([Down("first"), Down("second")]).complete("s", "u")


def test_a_model_can_be_tried_before_saving():
    factory = Recorder({"ollama_chat/qwen3:4b": FakeLLM([{"ok": True}])})

    result = make_client(factory).post("/settings/llm/test", json={"provider": "ollama", "model": "qwen3:4b"}).json()

    assert result["ok"] is True
    assert '"ok": true' in result["reply"]
    assert factory.configs[-1].timeout <= 60


def test_a_model_that_does_not_answer_says_why():
    class Missing:
        def complete(self, system, user):
            raise LookupError("model 'nope' not found")

    factory = Recorder({"ollama_chat/nope": Missing()})

    result = make_client(factory).post("/settings/llm/test", json={"provider": "ollama", "model": "nope"}).json()

    assert result["ok"] is False
    assert "not found" in result["error"]


def test_testing_a_provider_without_its_key_does_not_call_it(monkeypatch):
    monkeypatch.delenv("ANTHROPIC_API_KEY", raising=False)
    factory = Recorder()

    result = make_client(factory).post("/settings/llm/test", json={"provider": "anthropic", "model": "claude"}).json()

    assert result["ok"] is False and "no API key" in result["error"]
    assert factory.configs == []


def test_the_ollama_models_are_listed_and_a_new_one_can_be_downloaded():
    ollama = FakeOllama(["llama3.1:8b"])
    client = make_client(ollama=ollama)

    assert client.get("/settings/llm/ollama").json() == {"models": ["llama3.1:8b"], "pulls": {}, "error": None}

    accepted = client.post("/settings/llm/ollama/pull", json={"provider": "ollama", "model": "qwen3:4b"})

    assert accepted.status_code == 202
    assert ollama.pulled == [("http://ollama:11434", "qwen3:4b")]
    listed = client.get("/settings/llm/ollama").json()
    assert listed["models"] == ["llama3.1:8b", "qwen3:4b"]
    assert listed["pulls"] == {"qwen3:4b": "done"}


def test_an_ollama_that_is_not_running_is_reported_not_raised():
    result = make_client(ollama=FakeOllama(fail="connection refused")).get("/settings/llm/ollama").json()

    assert result["models"] == []
    assert "connection refused" in result["error"]


def test_only_ollama_models_are_downloaded():
    response = make_client().post("/settings/llm/ollama/pull", json={"provider": "nvidia_nim", "model": "x"})
    assert response.status_code == 422


def test_reply_suggestions_can_be_turned_off_from_the_settings():
    client = make_client()
    client.put("/settings/llm", json={**NEW_SETTINGS, "discussionSuggestions": False})

    response = client.post("/discussion/suggest", json={
        "course_id": 1, "question_stem": "Q?", "student_message": "Why?",
    })

    assert response.status_code == 403
