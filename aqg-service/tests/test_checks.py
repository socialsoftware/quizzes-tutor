from app.evaluation.duplicate_check import find_duplicate, normalise
from app.evaluation.format_check import check_format
from app.evaluation.pipeline import generate_verified_question
from app.generation.prompts import build_generation_prompt, system_prompt
from app.schema import GeneratedMCQ, OutcomeStatus, Revision
from tests.conftest import CLEAN_DISTRACTORS, GROUNDED, FakeLLM, good_question


def question(**changes):
    data = good_question()
    data.update(changes)
    return GeneratedMCQ.model_validate(data)


def with_option(index, content):
    data = good_question()
    data["options"][index]["content"] = content
    return GeneratedMCQ.model_validate(data)


def test_a_good_question_passes_the_format_checks():
    assert check_format(question()) == []


def test_stems_that_mention_the_source_are_refused():
    for stem in ("According to the text, what does 404 mean?", "Segundo o texto, o que significa 404?"):
        assert any("refers to the text" in p for p in check_format(question(stem=stem)))


def test_catch_all_and_lettered_options_are_refused():
    assert any("all/none" in p for p in check_format(with_option(3, "None of the above")))
    assert any("all/none" in p for p in check_format(with_option(3, "Todas as anteriores")))
    assert any("own letter" in p for p in check_format(with_option(0, "A) Resource not found")))


def test_echoed_schema_placeholders_are_refused():
    assert any("placeholders" in p for p in check_format(with_option(1, "option text")))


def test_too_short_stems_are_refused():
    assert any("too short" in p for p in check_format(question(stem="404?")))


def test_normalise_ignores_case_accents_and_punctuation():
    assert normalise("  O que é o HTTP?! ") == normalise("o que e o http")


def test_near_identical_stems_are_duplicates():
    assert find_duplicate("What does HTTP status 404 mean?", ["what does the HTTP status 404 mean"])
    assert find_duplicate("What does HTTP status 404 mean?", ["What does HTTP status 500 mean?"]) is None


def test_a_draft_repeating_an_existing_question_is_retried(request_strict):
    fresh = good_question()
    fresh["stem"] = "Which status code reports a missing resource?"
    llm = FakeLLM([good_question(), fresh, GROUNDED, CLEAN_DISTRACTORS])
    outcome = generate_verified_question(request_strict, llm, 2, ["What does HTTP status 404 mean?"])
    assert outcome.status is OutcomeStatus.OK
    assert outcome.question.stem == fresh["stem"]
    assert "repeats an existing one" in llm.prompts[1]


def test_the_language_is_fixed_in_the_system_prompt(request_strict):
    assert "in Portuguese" in system_prompt("Portuguese")
    assert "language of the context" in system_prompt(None)

    llm = FakeLLM([good_question(), GROUNDED, CLEAN_DISTRACTORS])
    generate_verified_question(request_strict.model_copy(update={"language": "Portuguese"}), llm, 2)
    assert "in Portuguese" in llm.systems[0]


def test_a_revision_sends_the_previous_version_and_the_review(request_strict):
    revision = Revision(previous=question(), review="The distractors are too easy")
    prompt = build_generation_prompt(request_strict.model_copy(update={"revision": revision}))
    assert "<teacher_review>" in prompt
    assert "The distractors are too easy" in prompt
    assert "What does HTTP status 404 mean?" in prompt
