import json
import random

import pytest

from app.evaluation.format_check import check_format
from app.evaluation.pipeline import generate_verified_question
from app.generation.options import shuffle_options
from app.llm.provider import FallbackProvider, LiteLLMProvider, LoggingProvider, precise
from app.schema import GeneratedMCQ, GenerationRequest, ModelConfig, OutcomeStatus
from tests.conftest import CLEAN_DISTRACTORS, GROUNDED, FakeLLM, good_question


def mcq(correct: str, *wrong: str) -> GeneratedMCQ:
    return GeneratedMCQ.model_validate({
        "stem": "What does the status code mean here?",
        "options": [{"content": correct, "correct": True}, *({"content": w, "correct": False} for w in wrong)],
    })


def request_with_text() -> GenerationRequest:
    return GenerationRequest(course_id=1, topic="HTTP", chunks=[{"id": "c1", "text": "404 means not found."}])


# ---------------------------------------------------------------- temperature 0 for the checks


class Recording:
    """Records the temperature each call was made at; precise() returns a cold copy of it."""

    model_id = "rec/model"

    def __init__(self, replies, temperature=0.7, calls=None):
        self.replies = replies
        self.temperature = temperature
        self.calls = calls if calls is not None else []

    def with_temperature(self, temperature):
        cold = Recording(self.replies, temperature, self.calls)
        cold.model_id = self.model_id
        return cold

    def complete(self, system, user):
        self.calls.append(self.temperature)
        reply = self.replies.pop(0)
        return reply if isinstance(reply, str) else json.dumps(reply)


def test_writing_is_warm_and_both_checks_are_cold():
    llm = Recording([good_question(), GROUNDED, CLEAN_DISTRACTORS])

    outcome = generate_verified_question(request_with_text(), llm, max_retries=0)

    assert outcome.status is OutcomeStatus.OK
    assert llm.calls == [0.7, 0.0, 0.0]


def test_litellm_provider_gets_a_cold_copy_and_keeps_its_own_temperature():
    warm = LiteLLMProvider(ModelConfig(model="m", temperature=0.7))

    cold = precise(warm)

    assert cold.config.temperature == 0.0 and warm.config.temperature == 0.7
    assert cold.config.model == "m"


def test_a_fallback_chain_stays_a_chain_and_records_the_models_that_answer():
    second = Recording(["{}"])
    second.model_id = "second/model"

    class Down:
        model_id = "down/model"

        def complete(self, system, user):
            raise TimeoutError()

    chain = FallbackProvider([Down(), second])
    cold = precise(chain)

    cold.complete("s", "u")

    assert isinstance(cold, FallbackProvider)
    assert chain.used == ["second/model"]  # shared with the original, which the job reads
    assert second.calls == [0.0]  # the model was asked cold


def test_the_logging_wrapper_is_kept_when_going_cold():
    cold = precise(LoggingProvider(Recording(["{}"])))

    assert isinstance(cold, LoggingProvider) and cold.provider.temperature == 0.0


def test_a_provider_that_cannot_change_temperature_is_left_alone():
    llm = FakeLLM([])

    assert precise(llm) is llm


# ---------------------------------------------------------------- option length


def test_a_correct_option_much_longer_than_the_others_is_flagged():
    long_answer = "The server cannot find the resource that was requested by the client"
    problems = check_format(mcq(long_answer, "Server error", "Unauthorized", "Redirect"))

    assert len(problems) == 1 and "much longer than the distractors" in problems[0]
    assert f"{len(long_answer)} characters" in problems[0]


@pytest.mark.parametrize(
    "correct, wrong",
    [
        ("Resource not found", ("Server error", "Unauthorized", "Redirect")),  # similar lengths
        ("Not found", ("Ok", "No", "Hi")),  # 4x longer but only a few characters: nobody can tell
        ("A short one", ("The server refused the request outright", "The server crashed while handling it", "The proxy timed out waiting")),
    ],
)
def test_options_of_similar_or_tiny_length_are_not_flagged(correct, wrong):
    assert check_format(mcq(correct, *wrong)) == []


def test_the_threshold_is_both_a_ratio_and_a_margin():
    # 1.6x the average but only 6 characters longer: not flagged
    assert check_format(mcq("abcdefghij", "abcdef", "abcdef", "abcdef")) == []
    # 1.6x and 20+ characters longer: flagged
    assert check_format(mcq("x" * 52, "y" * 32, "y" * 32, "y" * 32)) != []
    # 20+ characters longer but under 1.6x: not flagged
    assert check_format(mcq("x" * 100, "y" * 80, "y" * 80, "y" * 80)) == []


def test_a_malformed_draft_is_left_to_the_structure_check():
    two_correct = GeneratedMCQ.model_validate({
        "stem": "What does the status code mean here?",
        "options": [{"content": "x" * 80, "correct": True}, {"content": "y" * 80, "correct": True},
                    {"content": "z", "correct": False}, {"content": "w", "correct": False}],
    })

    assert not any("much longer" in problem for problem in check_format(two_correct))


def test_a_draft_flagged_for_length_is_rewritten_with_the_reason():
    long_draft = {
        "stem": "What does HTTP status 404 mean?",
        "options": [
            {"content": "The server cannot find the resource that was requested by the client", "correct": True},
            {"content": "Server error", "correct": False},
            {"content": "Unauthorized", "correct": False},
            {"content": "Redirect", "correct": False},
        ],
    }
    llm = FakeLLM([long_draft, good_question(), GROUNDED, CLEAN_DISTRACTORS])

    outcome = generate_verified_question(request_with_text(), llm, max_retries=1)

    assert outcome.status is OutcomeStatus.OK and outcome.retries == 1
    assert "much longer than the distractors" in llm.prompts[1]


# ---------------------------------------------------------------- shuffling


def test_shuffling_keeps_the_same_options_and_the_same_correct_one():
    question = GeneratedMCQ.model_validate(good_question())

    shuffled = shuffle_options(question, random.Random(3))

    assert sorted(o.content for o in shuffled.options) == sorted(o.content for o in question.options)
    assert [o.content for o in shuffled.options if o.correct] == ["Resource not found"]
    assert question.options[0].content == "Resource not found"  # the original is untouched


def test_the_correct_option_does_not_stay_where_the_model_put_it():
    question = GeneratedMCQ.model_validate(good_question())  # the model writes it first
    rng = random.Random(1)

    positions = {next(i for i, o in enumerate(shuffle_options(question, rng).options) if o.correct) for _ in range(60)}

    assert positions == {0, 1, 2, 3}


def test_the_pipeline_hands_over_shuffled_options():
    orders = set()
    for seed in range(12):
        llm = FakeLLM([good_question(), GROUNDED, CLEAN_DISTRACTORS])
        outcome = generate_verified_question(request_with_text(), llm, max_retries=0, rng=random.Random(seed))
        orders.add(tuple(o.content for o in outcome.question.options))
        assert sum(o.correct for o in outcome.question.options) == 1

    assert len(orders) > 3


def test_a_draft_flagged_for_the_teacher_is_shuffled_too():
    bad = dict(good_question(), stem="short")
    llm = FakeLLM([bad])

    outcome = generate_verified_question(request_with_text(), llm, max_retries=0, rng=random.Random(5))

    assert outcome.status is OutcomeStatus.NEEDS_HUMAN_ATTENTION
    assert [o.content for o in outcome.question.options] != [o["content"] for o in good_question()["options"]]


def test_the_checks_are_shown_the_draft_in_the_order_the_teacher_will_see():
    llm = FakeLLM([good_question(), GROUNDED, CLEAN_DISTRACTORS])

    outcome = generate_verified_question(request_with_text(), llm, max_retries=0, rng=random.Random(7))

    final = [o.content for o in outcome.question.options]
    check_prompt = llm.prompts[2]  # the distractor check
    shown = json.loads(check_prompt.split("<question>\n")[1].split("\n</question>")[0])
    assert [o["content"] for o in shown["options"]] == final
