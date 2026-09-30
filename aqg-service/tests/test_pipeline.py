from app.evaluation.pipeline import generate_verified_question
from app.evaluation.structural_validator import validate_structure
from app.generation.prompts import build_generation_prompt
from app.generation.synthesizer import parse_json_reply
from app.schema import GeneratedMCQ, GroundingMode, OutcomeStatus
from tests.conftest import CLEAN_DISTRACTORS, GROUNDED, FakeLLM, good_question


def test_structural_validator_accepts_a_good_question():
    assert validate_structure(GeneratedMCQ.model_validate(good_question())) == []


def test_structural_validator_reports_every_problem():
    bad = good_question()
    bad["options"][1]["correct"] = True
    bad["options"][2]["content"] = bad["options"][1]["content"]
    del bad["options"][3]
    problems = validate_structure(GeneratedMCQ.model_validate(bad))
    assert any("expected 4 options" in p for p in problems)
    assert any("exactly 1 correct" in p for p in problems)
    assert any("same text" in p for p in problems)


def test_parse_json_reply_accepts_markdown_fences():
    assert parse_json_reply('```json\n{"a": 1}\n```') == {"a": 1}


def test_first_attempt_passes_all_checks(request_strict):
    llm = FakeLLM([good_question(), GROUNDED, CLEAN_DISTRACTORS])
    outcome = generate_verified_question(request_strict, llm, max_retries=2)
    assert outcome.status is OutcomeStatus.OK
    assert outcome.retries == 0
    assert outcome.source_chunk_ids == ["c1"]
    assert outcome.question.topic == "HTTP"


def test_failure_reason_is_fed_back_into_the_next_attempt(request_strict):
    broken = good_question()
    broken["options"][1]["correct"] = True
    llm = FakeLLM([broken, good_question(), GROUNDED, CLEAN_DISTRACTORS])
    outcome = generate_verified_question(request_strict, llm, max_retries=2)
    assert outcome.status is OutcomeStatus.OK
    assert outcome.retries == 1
    assert "expected exactly 1 correct option" in llm.prompts[1]


def test_retries_are_capped_and_the_draft_is_flagged(request_strict):
    broken = good_question()
    broken["options"][1]["correct"] = True
    llm = FakeLLM([broken, broken, broken])
    outcome = generate_verified_question(request_strict, llm, max_retries=2)
    assert outcome.status is OutcomeStatus.NEEDS_HUMAN_ATTENTION
    assert outcome.question is not None
    assert len(llm.prompts) == 3


def test_unparseable_reply_counts_as_a_failed_attempt(request_strict):
    llm = FakeLLM(["not json at all", good_question(), GROUNDED, CLEAN_DISTRACTORS])
    outcome = generate_verified_question(request_strict, llm, max_retries=2)
    assert outcome.status is OutcomeStatus.OK
    assert outcome.retries == 1


def test_strict_mode_reports_insufficient_context_without_retrying(request_strict):
    llm = FakeLLM([{"insufficient_context": True, "reason": "context too short"}])
    outcome = generate_verified_question(request_strict, llm, max_retries=2)
    assert outcome.status is OutcomeStatus.INSUFFICIENT_CONTEXT
    assert len(llm.prompts) == 1


def test_ungrounded_answer_is_rejected(request_strict):
    ungrounded = {**GROUNDED, "correct_supported": False, "correct_chunk_ids": []}
    llm = FakeLLM([good_question(), ungrounded] * 3)
    outcome = generate_verified_question(request_strict, llm, max_retries=2)
    assert outcome.status is OutcomeStatus.NEEDS_HUMAN_ATTENTION
    assert any("not supported" in f for f in outcome.failures)


def test_chunk_ids_the_model_invents_do_not_count_as_support(request_strict):
    invented = {**GROUNDED, "correct_chunk_ids": ["nope"]}
    llm = FakeLLM([good_question(), invented] * 3)
    outcome = generate_verified_question(request_strict, llm, max_retries=2)
    assert outcome.status is OutcomeStatus.NEEDS_HUMAN_ATTENTION


def test_strict_mode_rejects_unsupported_claims(request_strict):
    claims = {**GROUNDED, "unsupported_claims": ["the moon is cheese"]}
    llm = FakeLLM([good_question(), claims] * 3)
    outcome = generate_verified_question(request_strict, llm, max_retries=2)
    assert outcome.status is OutcomeStatus.NEEDS_HUMAN_ATTENTION


def test_enriched_mode_needs_two_anchored_distractors(request_strict):
    request_strict.grounding_mode = GroundingMode.ENRICHED
    few = {**GROUNDED, "distractors_anchored": 1}
    llm = FakeLLM([good_question(), few] * 3)
    assert generate_verified_question(request_strict, llm, 2).status is OutcomeStatus.NEEDS_HUMAN_ATTENTION

    llm = FakeLLM([good_question(), {**GROUNDED, "distractors_anchored": 2}, CLEAN_DISTRACTORS])
    assert generate_verified_question(request_strict, llm, 2).status is OutcomeStatus.OK


def test_ambiguous_distractors_are_rejected(request_strict):
    ambiguous = {"also_correct": [2], "implausible": []}
    llm = FakeLLM([good_question(), GROUNDED, ambiguous] * 3)
    outcome = generate_verified_question(request_strict, llm, max_retries=2)
    assert outcome.status is OutcomeStatus.NEEDS_HUMAN_ATTENTION
    assert any("also correct" in f for f in outcome.failures)


def test_prompt_carries_rubric_grounding_and_context(request_strict):
    prompt = build_generation_prompt(request_strict)
    assert '[chunk id="c1"]' in prompt
    assert "Difficulty: MEDIUM" in prompt
    assert "insufficient_context" in prompt
