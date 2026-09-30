import pytest
from fastapi.testclient import TestClient

from app.api.main import create_app
from app.config import Settings, default_model_config
from app.schema import JobStatus
from tests.conftest import CLEAN_DISTRACTORS, GROUNDED, FakeLLM, good_question

PAYLOAD = {
    "course_id": 1,
    "topic": "HTTP",
    "count": 2,
    "chunks": [{"id": "c1", "text": "404 means the server cannot find the resource."}],
}


def make_client(llm):
    settings = Settings(provider="ollama", model="m", ollama_base_url="http://x", max_retries=1)
    return TestClient(create_app(settings, provider_factory=lambda config: llm))


def test_generate_runs_the_job_and_exposes_the_results():
    llm = FakeLLM([good_question(), GROUNDED, CLEAN_DISTRACTORS] * 2)
    client = make_client(llm)

    accepted = client.post("/generate", json=PAYLOAD)
    assert accepted.status_code == 202
    job = client.get(f"/jobs/{accepted.json()['id']}").json()

    assert job["status"] == JobStatus.DONE
    assert [o["status"] for o in job["outcomes"]] == ["OK", "OK"]
    assert job["prompt_version"] == "mcq-v1"
    assert job["grounding_mode"] == "STRICT"


def test_a_provider_outage_fails_the_job_instead_of_crashing():
    class Down:
        model_id = "down"

        def complete(self, system, user):
            raise ConnectionError("ollama unreachable")

    client = make_client(Down())
    job_id = client.post("/generate", json=PAYLOAD).json()["id"]
    job = client.get(f"/jobs/{job_id}").json()
    assert job["status"] == JobStatus.FAILED
    assert "ollama unreachable" in job["error"]


def test_unknown_job_is_a_404():
    assert make_client(FakeLLM([])).get("/jobs/nope").status_code == 404


def test_request_without_context_is_rejected():
    client = make_client(FakeLLM([]))
    assert client.post("/generate", json={**PAYLOAD, "chunks": []}).status_code == 422


@pytest.mark.parametrize(
    "provider, expected_model, expected_base",
    [
        ("ollama", "ollama/llama3.1:8b", "http://ollama:11434"),
        ("nvidia_nim", "nvidia_nim/meta/llama-3.1-70b-instruct", None),
        ("anthropic", "anthropic/claude-sonnet-5", None),
        ("openai", "gpt-4o-mini", None),
    ],
)
def test_provider_selection_is_only_configuration(monkeypatch, provider, expected_model, expected_base):
    monkeypatch.setenv("LLM_PROVIDER", provider)
    monkeypatch.delenv("LLM_MODEL", raising=False)
    config = default_model_config(Settings.from_env())
    assert config.model == expected_model
    assert config.api_base == expected_base


def test_unknown_provider_is_refused(monkeypatch):
    monkeypatch.setenv("LLM_PROVIDER", "carrier-pigeon")
    with pytest.raises(ValueError):
        Settings.from_env()
