"""The cost of each question: calls, tokens and seconds, counted around the calls to the models."""

import json

from fastapi.testclient import TestClient

from app.llm import usage
from app.llm.provider import LiteLLMProvider
from app.schema import ModelConfig
from tests.conftest import CLEAN_DISTRACTORS, GROUNDED, FakeLLM, good_question
from tests.test_protections import make_app, ask


def test_calls_made_inside_the_meter_are_added_up_and_calls_outside_are_not_counted():
    usage.record(10, 5, 1.0)  # nobody is counting

    with usage.metering() as meter:
        usage.record(100, 20, 1.5)
        usage.record(50, None, 0.25)

    assert meter.totals() == {"calls": 2, "prompt_tokens": 150, "completion_tokens": 20, "seconds": 1.75}
    assert usage.Meter().totals()["calls"] == 0


def test_meters_do_not_leak_into_each_other():
    with usage.metering() as outer:
        usage.record(1, 1, 1.0)
        with usage.metering() as inner:
            usage.record(2, 2, 2.0)
        usage.record(3, 3, 3.0)

    assert (outer.calls, outer.prompt_tokens) == (2, 4)
    assert (inner.calls, inner.prompt_tokens) == (1, 2)


def piece(text):
    return type("Chunk", (), {"choices": [type("Choice", (), {"delta": type("Delta", (), {"content": text})()})()]})()


def test_a_streamed_call_records_the_tokens_litellm_counts(monkeypatch):
    import litellm

    monkeypatch.setattr(litellm, "completion", lambda **kwargs: iter([piece('{"a": 1}')]))
    monkeypatch.setattr(
        litellm,
        "stream_chunk_builder",
        lambda chunks, messages=None: type("R", (), {"usage": type("U", (), {"prompt_tokens": 42, "completion_tokens": 7})()})(),
    )

    with usage.metering() as meter:
        LiteLLMProvider(ModelConfig(model="nvidia_nim/m")).complete("s", "u")

    assert (meter.calls, meter.prompt_tokens, meter.completion_tokens) == (1, 42, 7)
    assert meter.seconds >= 0


def test_a_call_whose_tokens_cannot_be_counted_is_still_a_call(monkeypatch):
    import litellm

    monkeypatch.setattr(litellm, "completion", lambda **kwargs: iter([piece("{}")]))

    def broken(chunks, messages=None):
        raise RuntimeError("no tokenizer")

    monkeypatch.setattr(litellm, "stream_chunk_builder", broken)

    with usage.metering() as meter:
        text = LiteLLMProvider(ModelConfig(model="nvidia_nim/m")).complete("s", "u")

    assert text == "{}"
    assert (meter.calls, meter.prompt_tokens, meter.completion_tokens) == (1, 0, 0)


def test_a_whole_reply_gives_its_usage(monkeypatch):
    import litellm

    reply = type(
        "R",
        (),
        {
            "choices": [type("C", (), {"message": type("M", (), {"content": "{}"})()})()],
            "usage": type("U", (), {"prompt_tokens": 11, "completion_tokens": 3})(),
        },
    )()
    monkeypatch.setattr(litellm, "completion", lambda **kwargs: reply)

    with usage.metering() as meter:
        LiteLLMProvider(ModelConfig(model="ollama_chat/m")).complete("s", "u")

    assert (meter.prompt_tokens, meter.completion_tokens) == (11, 3)


class CountingLLM(FakeLLM):
    """A model that reports what each call cost, like the real provider does."""

    def complete(self, system, user):
        reply = super().complete(system, user)
        usage.record(100, 10, 0.5)
        return reply


def test_every_question_of_a_job_carries_what_its_calls_cost(tmp_path):
    llm = CountingLLM([good_question(), GROUNDED, CLEAN_DISTRACTORS] * 2)
    client = TestClient(make_app(tmp_path, llm=llm))

    job = ask(client, count=2).json()
    outcomes = client.get(f"/jobs/{job['id']}").json()["outcomes"]

    # the calls the model received (writing and the checks) are split between the two questions
    assert len(outcomes) == 2 and len(llm.prompts) >= 2
    assert sum(outcome["usage"]["calls"] for outcome in outcomes) == len(llm.prompts)
    for outcome in outcomes:
        calls = outcome["usage"]["calls"]
        assert calls >= 1
        assert outcome["usage"] == {
            "calls": calls, "prompt_tokens": 100 * calls, "completion_tokens": 10 * calls, "seconds": 0.5 * calls}


def test_usage_is_in_what_the_service_keeps_and_not_mixed_between_questions(tmp_path):
    llm = CountingLLM([good_question(), GROUNDED, CLEAN_DISTRACTORS] * 3)
    client = TestClient(make_app(tmp_path, llm=llm))

    first = client.get(f"/jobs/{ask(client).json()['id']}").json()["outcomes"][0]["usage"]
    second = client.get(f"/jobs/{ask(client).json()['id']}").json()["outcomes"][0]["usage"]

    assert first == second and first["calls"] >= 1
    assert json.dumps(first).count("secret") == 0
