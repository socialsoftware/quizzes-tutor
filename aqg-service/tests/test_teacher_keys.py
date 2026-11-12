"""A teacher's own API key: used for their requests only, and never kept, shown or sent back."""

import json

from fastapi.testclient import TestClient

from app.api.main import create_app
from tests.conftest import CLEAN_DISTRACTORS, GROUNDED, FakeLLM, good_question
from tests.test_protections import CHUNK, settings

KEY = "nvapi-SECRET-teacher-key-123456"


def app_with(tmp_path, llm, seen, **overrides):
    def factory(config):
        seen.append(config)
        return llm

    return create_app(settings(tmp_path, provider="nvidia_nim", model="m", **overrides), provider_factory=factory)


def ask(client, **extra):
    return client.post("/generate", json={"course_id": 1, "topic": "HTTP", "count": 1, "chunks": [CHUNK], **extra})


def test_the_models_of_the_teachers_provider_use_the_teachers_key_and_the_others_keep_the_services(tmp_path):
    seen = []
    client = TestClient(app_with(tmp_path, FakeLLM([good_question(), GROUNDED, CLEAN_DISTRACTORS]), seen))

    ask(client, api_keys={"nvidia_nim": KEY, "openai": "sk-other"})

    assert [config.api_key for config in seen] == [KEY]
    assert seen[0].model.startswith("nvidia_nim/")


def test_without_a_key_of_their_own_the_service_key_is_left_to_the_environment(tmp_path):
    seen = []
    client = TestClient(app_with(tmp_path, FakeLLM([good_question(), GROUNDED, CLEAN_DISTRACTORS]), seen))

    ask(client)

    assert [config.api_key for config in seen] == [None]


def test_a_teachers_own_key_does_not_draw_on_the_services_per_minute_budget(tmp_path):
    llm = FakeLLM([good_question(), GROUNDED, CLEAN_DISTRACTORS] * 4)
    client = TestClient(app_with(tmp_path, llm, [], llm_requests_per_minute=1, rate_limit_wait=0))

    first = ask(client, api_keys={"nvidia_nim": KEY}).json()
    second = ask(client, api_keys={"nvidia_nim": KEY}).json()

    assert client.get(f"/jobs/{first['id']}").json()["status"] == "DONE"
    assert client.get(f"/jobs/{second['id']}").json()["status"] == "DONE"


def test_the_key_is_not_in_what_the_service_answers_or_keeps(tmp_path):
    client = TestClient(app_with(tmp_path, FakeLLM([good_question(), GROUNDED, CLEAN_DISTRACTORS]), []))

    response = ask(client, api_keys={"nvidia_nim": KEY})
    stored = client.get(f"/jobs/{response.json()['id']}").text

    assert KEY not in response.text and KEY not in stored


def test_a_failure_that_repeats_the_key_is_reported_without_it(tmp_path, caplog):
    class Failing:
        model_id = "fake/model"

        def complete(self, system, user):
            raise RuntimeError(f"Incorrect API key provided: {KEY}")

    client = TestClient(app_with(tmp_path, Failing(), []))

    with caplog.at_level("WARNING"):
        job = ask(client, api_keys={"nvidia_nim": KEY}).json()
        state = client.get(f"/jobs/{job['id']}").json()

    assert state["status"] == "FAILED"
    assert KEY not in json.dumps(state)
    assert "<key>" in state["error"]
    assert KEY not in caplog.text


def test_a_key_given_to_test_a_model_is_used_for_that_call_and_never_echoed(tmp_path):
    seen = []

    class Failing:
        model_id = "fake/model"

        def complete(self, system, user):
            raise RuntimeError(f"401 for key {KEY}")

    def factory(config):
        seen.append(config)
        return Failing()

    client = TestClient(create_app(settings(tmp_path, provider="nvidia_nim", model="m"), provider_factory=factory))

    result = client.post("/settings/llm/test", json={"provider": "nvidia_nim", "model": "m", "apiKey": KEY}).json()

    assert seen[0].api_key == KEY
    assert result["ok"] is False
    assert KEY not in json.dumps(result)


def test_the_suggested_reply_uses_the_teachers_key_too(tmp_path):
    seen = []
    client = TestClient(app_with(tmp_path, FakeLLM([{"reply": "ok"}]), seen))

    client.post(
        "/discussion/suggest",
        json={"course_id": 1, "question_stem": "What does 404 mean?", "student_message": "why?", "api_keys": {"nvidia_nim": KEY}},
    )

    assert [config.api_key for config in seen] == [KEY]


def test_the_model_a_teacher_picked_is_tried_first_with_their_key_and_the_usual_ones_follow(tmp_path):
    seen = []
    client = TestClient(app_with(tmp_path, FakeLLM([good_question(), GROUNDED, CLEAN_DISTRACTORS]), seen))

    ask(client, chosen_model={"provider": "openai", "model": "gpt-4o-mini"}, api_keys={"openai": KEY})

    assert [(config.model, config.api_key) for config in seen] == [("gpt-4o-mini", KEY), ("nvidia_nim/m", None)]


def test_a_model_picked_that_is_already_the_main_one_is_not_tried_twice(tmp_path):
    seen = []
    client = TestClient(app_with(tmp_path, FakeLLM([good_question(), GROUNDED, CLEAN_DISTRACTORS]), seen))

    ask(client, chosen_model={"provider": "nvidia_nim", "model": "m"})

    assert [config.model for config in seen] == ["nvidia_nim/m"]


def test_a_model_name_that_is_not_a_model_name_is_refused(tmp_path):
    client = TestClient(app_with(tmp_path, FakeLLM([]), []))

    assert ask(client, chosen_model={"provider": "nvidia_nim", "model": "m; rm -rf /"}).status_code == 422
    assert ask(client, chosen_model={"provider": "nowhere", "model": "m"}).status_code == 422
    assert ask(client, chosen_model={"provider": "nvidia_nim", "model": ""}).status_code == 422


def test_the_models_teachers_may_pick_are_kept_with_the_settings_and_need_the_services_key(tmp_path, monkeypatch):
    monkeypatch.setenv("NVIDIA_NIM_API_KEY", "service-key")
    monkeypatch.delenv("OPENAI_API_KEY", raising=False)
    client = TestClient(app_with(tmp_path, FakeLLM([]), []))
    current = client.get("/settings/llm").json()["settings"]
    assert current["teacherModels"] == []

    saved = client.put("/settings/llm", json={**current, "teacherModels": [{"provider": "nvidia_nim", "model": "free/model"}]})
    assert saved.status_code == 200
    assert client.get("/settings/llm").json()["settings"]["teacherModels"] == [{"provider": "nvidia_nim", "model": "free/model"}]

    refused = client.put("/settings/llm", json={**current, "teacherModels": [{"provider": "openai", "model": "gpt-4o-mini"}]})
    assert refused.status_code == 422
