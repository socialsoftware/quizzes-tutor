import importlib.util
import json
from pathlib import Path

import pytest

from app.evaluation.harness import (
    EvalCase,
    MeteredProvider,
    failure_kind,
    load_cases,
    render_markdown,
    run_variant,
    summarize,
)
from app.generation.prompts import system_prompt
from app.schema import Chunk, Difficulty
from tests.conftest import CLEAN_DISTRACTORS, GROUNDED, FakeLLM, good_question

LONG_ANSWER = {
    "stem": "What does HTTP status 404 mean?",
    "options": [
        {"content": "The server cannot find the resource that was requested by the client", "correct": True},
        {"content": "Server error", "correct": False},
        {"content": "Unauthorized", "correct": False},
        {"content": "Redirect", "correct": False},
    ],
}


def case(case_id="c1", difficulty=Difficulty.MEDIUM) -> EvalCase:
    return EvalCase(case_id, "HTTP", [Chunk(id="c1", text="404 means the server cannot find the resource.")], difficulty)


def good_run():
    return [good_question(), GROUNDED, CLEAN_DISTRACTORS]


@pytest.mark.parametrize(
    "message, kind",
    [
        ("reply is not valid JSON (Expecting value)", "bad_json"),
        ("reply does not match the question schema: Field required", "bad_json"),
        ("grounding check failed to run: reply is not valid JSON", "bad_json"),
        ('the question repeats an existing one: "What is X?"', "duplicate"),
        ("the correct option (70 characters) is much longer than the distractors (about 12)", "option_length"),
        ("the correct option is not supported by any provided chunk", "grounding"),
        ("claims not supported by the context: x", "grounding"),
        ("options [1] (0-based) are also correct", "distractors"),
        ("options [2] (0-based) are implausible distractors", "distractors"),
        ("the stem refers to the text/context, which the students never see", "format"),
        ('option 2 is an "all/none of the above" option', "format"),
        ("exactly one option must be correct, found 2", "structure"),
        ("something unforeseen", "other"),
    ],
)
def test_failures_are_counted_by_family(message, kind):
    assert failure_kind(message) == kind


def test_a_draft_that_passes_at_once_is_a_first_try_ok_with_three_calls():
    results = run_variant(FakeLLM(good_run()), [case()])

    assert [(r.status, r.retries, r.llm_calls) for r in results] == [("OK", 0, 3)]
    assert results[0].question["options"]


def test_a_rewrite_shows_up_as_a_retry_with_its_failure_family():
    llm = FakeLLM([LONG_ANSWER, *good_run()])

    [result] = run_variant(llm, [case()], max_retries=1)

    assert (result.status, result.retries, result.llm_calls) == ("OK", 1, 4)
    assert summarize([result])["failure_kinds"] == {"option_length": 1}
    assert summarize([result])["first_try_rate"] == 0.0


def test_a_provider_outage_is_recorded_and_the_other_cases_still_run():
    class Flaky:
        model_id = "flaky/model"

        def __init__(self):
            self.n = 0

        def complete(self, system, user):
            self.n += 1
            if self.n == 1:
                raise TimeoutError("slow")
            return json.dumps([good_question(), GROUNDED, CLEAN_DISTRACTORS][(self.n - 2) % 3])

    results = run_variant(Flaky(), [case("a"), case("b")])

    assert [r.status for r in results] == ["ERROR", "OK"]
    assert results[0].error == "TimeoutError"
    assert summarize(results)["errors"] == 1


def test_runs_repeat_every_case_and_the_option_order_is_repeatable():
    def run():
        llm = FakeLLM(good_run() * 4)
        return run_variant(llm, [case("a"), case("b")], runs=2, seed=3)

    first, second = run(), run()

    assert [(r.case_id, r.run) for r in first] == [("a", 0), ("b", 0), ("a", 1), ("b", 1)]
    assert [r.question["options"] for r in first] == [r.question["options"] for r in second]


def test_another_system_prompt_reaches_the_model():
    llm = FakeLLM(good_run())

    run_variant(llm, [case()], system="Be terse. {language}")

    assert llm.systems[0] == "Be terse. {language}"
    assert llm.systems[1] != llm.systems[0]  # the checks keep their own


def test_without_an_override_the_standard_prompt_is_used():
    llm = FakeLLM(good_run())

    run_variant(llm, [case()])

    assert llm.systems[0] == system_prompt(None)


def test_the_metered_provider_counts_cold_copies_too():
    class Cold:
        model_id = "m"

        def with_temperature(self, t):
            return self

        def complete(self, system, user):
            return "{}"

    metered = MeteredProvider(Cold())
    cold = metered.with_temperature(0.0)

    metered.complete("s", "u")
    cold.complete("s", "u")

    assert metered.calls == 2


def test_the_summary_counts_statuses_positions_and_length():
    llm = FakeLLM([*good_run(), dict(good_question(), stem="short"), {"insufficient_context": True, "reason": "no"}])
    results = run_variant(llm, [case("a"), case("b"), case("c")], max_retries=0)

    summary = summarize(results)

    assert (summary["drafts"], summary["ok"], summary["needs_human_attention"], summary["insufficient_context"]) == (3, 1, 1, 1)
    assert summary["ok_rate"] == pytest.approx(1 / 3) and summary["first_try_rate"] == pytest.approx(1 / 3)
    assert sum(summary["correct_position"].values()) == 2  # drafts with one correct option
    assert summary["failure_kinds"]["format"] == 1
    assert summary["mean_correct_length_ratio"] > 0


def test_an_empty_run_has_a_summary_and_a_report():
    assert summarize([]) == {"drafts": 0}
    assert "| drafts | - |" in render_markdown({"x": {"drafts": 0}})


def test_the_report_puts_one_column_per_variant():
    a = summarize(run_variant(FakeLLM(good_run()), [case()]))
    b = summarize(run_variant(FakeLLM([LONG_ANSWER, *good_run()]), [case()], max_retries=1))

    table = render_markdown({"small model": a, "big model": b})

    header = table.splitlines()[0]
    assert "small model" in header and "big model" in header
    assert "| first try (no rewrite) | 100% | 0% |" in table
    assert "| failed on option_length (drafts) | 0 | 1 |" in table


def test_cases_are_read_with_their_defaults(tmp_path):
    path = tmp_path / "cases.json"
    path.write_text(json.dumps([
        {"id": 1, "topic": "HTTP", "chunks": [{"id": "x", "text": "t"}]},
        {"id": "b", "topic": "DNS", "difficulty": "HARD", "grounding_mode": "ENRICHED", "language": "Portuguese",
         "chunks": [{"id": "y", "text": "u", "source": "A > B"}]},
    ]), encoding="utf-8")

    first, second = load_cases(path)

    assert (first.id, first.difficulty.value, first.grounding_mode.value, first.language) == ("1", "MEDIUM", "STRICT", None)
    assert (second.difficulty.value, second.grounding_mode.value, second.language) == ("HARD", "ENRICHED", "Portuguese")
    assert second.request().chunks[0].source == "A > B"


def test_the_case_builder_spreads_cases_over_the_document():
    spec = importlib.util.spec_from_file_location("make_cases", Path(__file__).parents[1] / "scripts" / "make_cases.py")
    module = importlib.util.module_from_spec(spec)
    spec.loader.exec_module(module)
    markdown = "\n\n".join(f"# Part {i}\n\n" + ("word " * 120) for i in range(30))

    cases = module.build_cases(markdown, 5, 2, "Portuguese", "STRICT")

    assert len(cases) == 5
    assert [c["difficulty"] for c in cases] == ["EASY", "MEDIUM", "HARD", "EASY", "MEDIUM"]
    assert all(len(c["chunks"]) == 2 and c["language"] == "Portuguese" for c in cases)
    first_ids = [int(c["chunks"][0]["id"].split(":")[1]) for c in cases]
    assert first_ids == sorted(first_ids) and first_ids[-1] - first_ids[0] > 20
    assert cases[1]["topic"].startswith("Part ")


def test_a_reason_that_fails_two_attempts_of_one_draft_is_counted_once():
    llm = FakeLLM([LONG_ANSWER, LONG_ANSWER, *good_run()])

    [result] = run_variant(llm, [case()], max_retries=2)

    assert result.retries == 2 and len(result.failures) == 2
    assert summarize([result])["failure_kinds"] == {"option_length": 1}
