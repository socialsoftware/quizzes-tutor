from datetime import datetime, timedelta, timezone

import pytest
from fastapi.testclient import TestClient
from pydantic import ValidationError

from app.api.main import create_app
from app.config import Settings
from app.db import JobRow
from app.generation.prompts import GROUNDING_CHECK_SYSTEM, PROMPT_VERSION, system_prompt
from app.limits import Limiters, RateLimited, RateLimiter, RateLimitedProvider, provider_key, same_secret
from app.llm.settings import LlmSettings
from app.schema import GroundingMode, Job, JobStatus
from tests.conftest import CLEAN_DISTRACTORS, GROUNDED, FakeLLM, good_question
from tests.test_llm_settings import NEW_SETTINGS

CHUNK = {"id": "c1", "text": "404 means the server cannot find the resource."}


def settings(tmp_path, **overrides) -> Settings:
    base = dict(provider="ollama", model="m", ollama_base_url="http://x", max_retries=0, materials_dir=tmp_path)
    return Settings(**{**base, **overrides})


def make_app(tmp_path, llm=None, **overrides):
    llm = llm or FakeLLM([good_question(), GROUNDED, CLEAN_DISTRACTORS] * 20)
    return create_app(settings(tmp_path, **overrides), provider_factory=lambda config: llm)


def ask(client, count=1, course_id=1, **extra):
    return client.post("/generate", json={"course_id": course_id, "topic": "HTTP", "count": count, "chunks": [CHUNK], **extra})


def add_job(app, course_id=1, questions=1, status=JobStatus.PENDING, when=None):
    job = Job(id=f"j{id(object())}{questions}{course_id}{status.value}{when}", status=status, prompt_version="x", model_id="m",
              grounding_mode=GroundingMode.STRICT)
    app.state.jobs.add(job, course_id, questions)
    if status is not JobStatus.PENDING or when:
        with app.state.session_factory.begin() as session:
            row = session.get(JobRow, job.id)
            row.status = status.value
            if when:
                row.created_at = when
    return job


# ---------------------------------------------------------------- the per-minute limiter


class Clock:
    def __init__(self):
        self.now = 1000.0
        self.slept = []

    def __call__(self):
        return self.now

    def sleep(self, seconds):
        self.slept.append(round(seconds, 3))
        self.now += seconds


def limiter(per_minute, clock):
    return RateLimiter(per_minute, clock=clock, sleep=clock.sleep)


def test_calls_within_the_budget_do_not_wait():
    clock = Clock()
    rl = limiter(3, clock)

    assert [rl.acquire(0) for _ in range(3)] == [True, True, True]
    assert clock.slept == []


def test_the_call_over_the_budget_waits_for_the_oldest_one_to_leave_the_window():
    clock = Clock()
    rl = limiter(2, clock)
    rl.acquire(0)
    clock.now += 10
    rl.acquire(0)

    assert rl.acquire(120) is True
    assert clock.slept == [50.0]  # the first call was 10 s old: 50 s more


def test_it_gives_up_instead_of_waiting_longer_than_allowed():
    clock = Clock()
    rl = limiter(1, clock)
    rl.acquire(0)

    assert rl.acquire(20) is False
    assert clock.slept == []


def test_the_window_slides_so_a_minute_later_the_budget_is_back():
    clock = Clock()
    rl = limiter(2, clock)
    rl.acquire(0)
    rl.acquire(0)
    clock.now += 60

    assert rl.acquire(0) is True and rl.acquire(0) is True and rl.acquire(0) is False


def test_a_limiter_needs_a_positive_budget():
    with pytest.raises(ValueError):
        RateLimiter(0)


@pytest.mark.parametrize(
    "model, key",
    [("nvidia_nim/nvidia/nemotron", "nvidia_nim"), ("anthropic/claude-x", "anthropic"), ("gpt-4o-mini", "openai"),
     ("ollama_chat/llama3.1:8b", None), ("ollama/x", None)],
)
def test_each_cloud_provider_has_its_own_quota_and_local_models_none(model, key):
    assert provider_key(model) == key


def test_limiters_are_shared_per_provider_and_off_when_zero():
    limiters = Limiters(5)

    assert limiters.for_model("nvidia_nim/a") is limiters.for_model("nvidia_nim/b")
    assert limiters.for_model("nvidia_nim/a") is not limiters.for_model("openai-model")
    assert limiters.for_model("ollama_chat/x") is None
    assert Limiters(0).for_model("nvidia_nim/a") is None


def test_the_cold_copy_of_a_limited_provider_draws_on_the_same_budget():
    class Model:
        model_id = "nvidia_nim/m"

        def with_temperature(self, t):
            return self

        def complete(self, system, user):
            return "{}"

    clock = Clock()
    rl = limiter(2, clock)
    warm = RateLimitedProvider(Model(), rl, 0)

    warm.complete("s", "u")
    warm.with_temperature(0.0).complete("s", "u")

    with pytest.raises(RateLimited):
        warm.complete("s", "u")


def test_keys_are_compared_exactly():
    assert same_secret("abc", "abc") and not same_secret("abd", "abc")
    assert not same_secret(None, "abc") and not same_secret("", "abc") and not same_secret("abcd", "abc")


# ---------------------------------------------------------------- only the Tutor may call


def test_with_an_api_key_every_call_without_it_is_refused(tmp_path):
    client = TestClient(make_app(tmp_path, api_key="secret"))

    assert client.get("/courses/1/materials").status_code == 401
    assert client.post("/generate", json={"course_id": 1, "topic": "x", "chunks": [CHUNK]}).status_code == 401
    assert client.post("/discussion/suggest", json={}).status_code == 401
    assert client.get("/settings/llm").status_code == 401
    assert client.get("/materials/m/chunks", headers={"X-API-Key": "wrong"}).status_code == 401


def test_with_the_key_calls_go_through_and_health_stays_open(tmp_path):
    client = TestClient(make_app(tmp_path, api_key="secret"))

    assert client.get("/health").status_code == 200
    assert client.get("/courses/1/materials", headers={"X-API-Key": "secret"}).status_code == 200
    accepted = client.post(
        "/generate", json={"course_id": 1, "topic": "HTTP", "chunks": [CHUNK]}, headers={"X-API-Key": "secret"}
    )
    assert accepted.status_code == 202


def test_the_interactive_documentation_is_off_when_there_is_a_key(tmp_path):
    keyed = TestClient(make_app(tmp_path, api_key="secret"))
    open_ = TestClient(make_app(tmp_path))

    assert keyed.get("/docs", headers={"X-API-Key": "secret"}).status_code == 404
    assert keyed.get("/openapi.json", headers={"X-API-Key": "secret"}).status_code == 404
    assert open_.get("/docs").status_code == 200


def test_without_a_key_the_service_warns_at_start(tmp_path, caplog):
    with caplog.at_level("WARNING"):
        make_app(tmp_path)

    assert "AQG_API_KEY is not set" in caplog.text


# ---------------------------------------------------------------- how many questions


def test_at_most_ten_questions_in_one_request(tmp_path):
    client = TestClient(make_app(tmp_path))

    assert ask(client, count=10).status_code == 202
    assert ask(client, count=11).status_code == 422
    assert ask(client, count=0).status_code == 422


# ---------------------------------------------------------------- the queue


def test_a_request_that_would_overfill_the_queue_is_refused_with_429(tmp_path):
    app = make_app(tmp_path, max_queued_questions=100)
    add_job(app, course_id=2, questions=95)
    client = TestClient(app)

    refused = ask(client, count=6)

    assert refused.status_code == 429 and "queue is full" in refused.json()["detail"]
    assert refused.headers["retry-after"]
    assert ask(client, count=5).status_code == 202


def test_too_many_waiting_jobs_are_refused_even_if_they_are_small(tmp_path):
    app = make_app(tmp_path, max_queued_jobs=2)
    add_job(app, course_id=2, questions=1)
    add_job(app, course_id=3, questions=1)

    assert ask(TestClient(app)).status_code == 429


def test_finished_jobs_do_not_take_a_place_in_the_queue(tmp_path):
    app = make_app(tmp_path, max_queued_questions=10)
    add_job(app, course_id=2, questions=10, status=JobStatus.DONE)
    add_job(app, course_id=2, questions=10, status=JobStatus.FAILED)

    assert ask(TestClient(app), count=10).status_code == 202


def test_a_refused_request_leaves_no_job_behind(tmp_path):
    app = make_app(tmp_path, max_queued_questions=3)
    client = TestClient(app)

    ask(client, count=5)

    assert app.state.jobs.pending() == (0, 0)


def test_jobs_left_over_by_a_crash_are_failed_at_start_and_free_the_queue(tmp_path):
    url = f"sqlite:///{tmp_path / 'aqg.db'}"
    first = make_app(tmp_path, database_url=url)
    stuck = add_job(first, questions=50, status=JobStatus.RUNNING)
    assert first.state.jobs.pending() == (1, 50)

    second = make_app(tmp_path, database_url=url)

    assert second.state.jobs.pending() == (0, 0)
    job = second.state.jobs.get(stuck.id)
    assert job.status is JobStatus.FAILED and "restarted" in job.error


# ---------------------------------------------------------------- the daily quota of a course


def test_a_course_cannot_ask_for_more_than_its_daily_quota(tmp_path):
    client = TestClient(make_app(tmp_path, daily_questions_per_course=5))

    assert ask(client, count=3).status_code == 202
    over = ask(client, count=3)

    assert over.status_code == 429
    assert "already requested 3 of its 5 questions" in over.json()["detail"] and "midnight UTC" in over.json()["detail"]
    assert ask(client, count=2).status_code == 202
    assert ask(client, count=1).status_code == 429


def test_the_quota_is_per_course(tmp_path):
    client = TestClient(make_app(tmp_path, daily_questions_per_course=2))

    assert ask(client, count=2, course_id=1).status_code == 202
    assert ask(client, count=2, course_id=2).status_code == 202
    assert ask(client, count=1, course_id=1).status_code == 429


def test_yesterdays_questions_do_not_count_today(tmp_path):
    app = make_app(tmp_path, daily_questions_per_course=3)
    add_job(app, course_id=1, questions=3, status=JobStatus.DONE, when=datetime.now(timezone.utc) - timedelta(days=1, minutes=1))

    assert ask(TestClient(app), count=3).status_code == 202


def test_failed_jobs_still_count_because_they_used_the_model(tmp_path):
    app = make_app(tmp_path, daily_questions_per_course=3)
    add_job(app, course_id=1, questions=3, status=JobStatus.FAILED)

    assert ask(TestClient(app), count=1).status_code == 429


def test_the_quota_survives_a_restart(tmp_path):
    url = f"sqlite:///{tmp_path / 'q.db'}"
    first = TestClient(make_app(tmp_path, daily_questions_per_course=2, database_url=url))
    assert ask(first, count=2).status_code == 202

    second = TestClient(make_app(tmp_path, daily_questions_per_course=2, database_url=url))

    assert ask(second, count=1).status_code == 429


def test_no_quota_when_it_is_zero(tmp_path):
    client = TestClient(make_app(tmp_path, daily_questions_per_course=0))

    assert all(ask(client, count=10).status_code == 202 for _ in range(3))


# ---------------------------------------------------------------- the model provider's budget


def cloud(tmp_path, **overrides):
    return make_app(tmp_path, provider="nvidia_nim", **overrides)


def test_a_job_that_cannot_get_a_slot_in_time_fails_instead_of_hanging(tmp_path):
    app = cloud(tmp_path, llm_requests_per_minute=1, rate_limit_wait=0)
    client = TestClient(app)

    job = ask(client).json()
    result = client.get(f"/jobs/{job['id']}").json()

    assert result["status"] == "FAILED" and "RateLimited" in result["error"]


def test_a_job_within_the_budget_runs_normally(tmp_path):
    client = TestClient(cloud(tmp_path, llm_requests_per_minute=30, rate_limit_wait=0))

    job = ask(client).json()

    assert client.get(f"/jobs/{job['id']}").json()["status"] == "DONE"


def test_local_models_are_never_limited(tmp_path):
    client = TestClient(make_app(tmp_path, provider="ollama", llm_requests_per_minute=1, rate_limit_wait=0))

    job = ask(client).json()

    assert client.get(f"/jobs/{job['id']}").json()["status"] == "DONE"


def test_a_teacher_pressing_suggest_reply_is_told_to_try_again_instead_of_waiting(tmp_path):
    llm = FakeLLM([{"reply": "one"}, {"reply": "two"}])
    client = TestClient(make_app(tmp_path, llm=llm, provider="nvidia_nim", llm_requests_per_minute=1))
    body = {"course_id": 1, "question_stem": "What does 404 mean?", "student_message": "why?"}

    first = client.post("/discussion/suggest", json=body)
    second = client.post("/discussion/suggest", json=body)

    assert first.status_code == 200
    assert second.status_code == 429 and "busy" in second.json()["detail"] and second.headers["retry-after"]
    assert len(llm.prompts) == 1


# ---------------------------------------------------------------- choosing the model, addresses


def test_a_request_cannot_bring_its_own_model_or_key_by_default(tmp_path):
    client = TestClient(make_app(tmp_path))

    response = ask(client, model={"model": "openai/x", "api_base": "http://evil.example", "api_key": "k"})

    assert response.status_code == 422 and "not allowed" in response.json()["detail"]


def test_it_can_be_allowed_on_purpose(tmp_path):
    client = TestClient(make_app(tmp_path, allow_request_model=True))

    assert ask(client, model={"model": "openai/x"}).status_code == 202


@pytest.mark.parametrize(
    "url",
    ["http://169.254.169.254/latest/meta-data", "http://169.254.0.5:11434", "http://[fe80::1]:11434", "http://0.0.0.0:11434",
     "http://metadata.google.internal/", "http://user:pass@ollama:11434", "ftp://ollama:11434", "ollama:11434"],
)
def test_the_ollama_address_cannot_point_at_metadata_services_or_carry_credentials(url):
    with pytest.raises(ValidationError):
        LlmSettings.model_validate({**NEW_SETTINGS, "ollamaBaseUrl": url})


@pytest.mark.parametrize("url", ["http://ollama:11434", "http://localhost:11434/", "http://192.168.1.20:11434", "https://gpu.example.org"])
def test_ordinary_addresses_still_work(url):
    assert LlmSettings.model_validate({**NEW_SETTINGS, "ollamaBaseUrl": url}).ollama_base_url == url.rstrip("/")


# ---------------------------------------------------------------- text of documents is data


def test_the_prompts_tell_the_model_that_course_text_is_data():
    assert "never as instructions" in system_prompt(None) and "<context>" in system_prompt(None)
    assert "never instructions" in GROUNDING_CHECK_SYSTEM
    assert PROMPT_VERSION == "mcq-v3"
