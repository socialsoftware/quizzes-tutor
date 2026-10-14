"""Offline evaluation harness: runs the real generation pipeline over a fixed set of cases and
reports how often drafts pass, why they fail and what they look like, so that models and prompt
variants can be compared with numbers instead of impressions.

The harness measures the *machinery* (does the model follow the format, survive the checks, stay
grounded), not whether the questions are good: that is what the teachers' reviews are for.
Nothing here touches the database or the Tutor; it needs only a provider and the cases.
"""
import json
import random
import statistics
import time
from collections import Counter
from dataclasses import dataclass, field
from pathlib import Path

from app.evaluation.pipeline import generate_verified_question
from app.llm.provider import LLMProvider
from app.schema import Chunk, Difficulty, GenerationRequest, GroundingMode, OutcomeStatus

FAILURE_KINDS = (
    ("bad_json", ("valid JSON", "question schema", "JSON object", "failed to run")),
    ("duplicate", ("repeats an existing",)),
    ("option_length", ("much longer than the distractors",)),
    ("grounding", ("not supported", "claims not supported", "anchored")),
    ("distractors", ("also correct", "implausible")),
    ("format", ("refers to the text", "prompt markup", "all/none", "starts with its own", "too short")),
)


def failure_kind(message: str) -> str:
    """Which family a failure message belongs to, for counting."""
    for kind, needles in FAILURE_KINDS:
        if any(needle in message for needle in needles):
            return kind
    return "structure" if "option" in message else "other"


@dataclass
class EvalCase:
    id: str
    topic: str
    chunks: list[Chunk]
    difficulty: Difficulty = Difficulty.MEDIUM
    grounding_mode: GroundingMode = GroundingMode.STRICT
    focus: str | None = None
    language: str | None = None

    def request(self) -> GenerationRequest:
        return GenerationRequest(
            course_id=0,
            topic=self.topic,
            difficulty=self.difficulty,
            grounding_mode=self.grounding_mode,
            chunks=self.chunks,
            focus=self.focus,
            language=self.language,
        )


def load_cases(path: Path) -> list[EvalCase]:
    raw = json.loads(Path(path).read_text(encoding="utf-8"))
    return [
        EvalCase(
            id=str(item["id"]),
            topic=item["topic"],
            chunks=[Chunk(**chunk) for chunk in item["chunks"]],
            difficulty=Difficulty(item.get("difficulty", "MEDIUM")),
            grounding_mode=GroundingMode(item.get("grounding_mode", "STRICT")),
            focus=item.get("focus"),
            language=item.get("language"),
        )
        for item in raw
    ]


class MeteredProvider:
    """Counts the calls made to a provider and the time they took."""

    def __init__(self, provider: LLMProvider):
        self.provider = provider
        self.model_id = provider.model_id
        self.calls = 0
        self.seconds = 0.0

    def with_temperature(self, temperature: float) -> "MeteredProvider":
        make = getattr(self.provider, "with_temperature", None)
        other = MeteredProvider(make(temperature) if make else self.provider)
        other.counter_of = self  # the cold copy reports into this one
        return other

    def complete(self, system: str, user: str) -> str:
        owner = getattr(self, "counter_of", self)
        started = time.monotonic()
        try:
            return self.provider.complete(system, user)
        finally:
            owner.calls += 1
            owner.seconds += time.monotonic() - started


@dataclass
class DraftResult:
    case_id: str
    run: int
    status: str
    retries: int = 0
    failures: list[str] = field(default_factory=list)
    question: dict | None = None
    llm_calls: int = 0
    seconds: float = 0.0
    error: str | None = None


def run_variant(
    provider: LLMProvider,
    cases: list[EvalCase],
    runs: int = 1,
    max_retries: int = 2,
    system: str | None = None,
    seed: int = 0,
    progress=None,
) -> list[DraftResult]:
    """One draft per case and run, through the same pipeline the service uses. A provider failure
    is recorded as an ERROR result instead of stopping the whole evaluation."""
    results: list[DraftResult] = []
    for run in range(runs):
        for case in cases:
            metered = MeteredProvider(provider)
            started = time.monotonic()
            try:
                outcome = generate_verified_question(
                    case.request(), metered, max_retries, system=system, rng=random.Random(f"{seed}/{case.id}/{run}")
                )
            except Exception as error:  # an outage must not lose the results so far
                results.append(DraftResult(case.id, run, "ERROR", error=f"{type(error).__name__}", llm_calls=metered.calls,
                                           seconds=time.monotonic() - started))
            else:
                results.append(DraftResult(
                    case.id, run, outcome.status.value, outcome.retries, list(outcome.failures),
                    outcome.question.model_dump(mode="json") if outcome.question else None,
                    metered.calls, time.monotonic() - started,
                ))
            if progress:
                progress(results[-1])
    return results


def summarize(results: list[DraftResult]) -> dict:
    n = len(results)
    if n == 0:
        return {"drafts": 0}
    statuses = Counter(r.status for r in results)
    drafted = [r for r in results if r.question]
    kinds: Counter = Counter()
    for r in results:
        # one count per kind and draft, however many attempts failed for that reason
        kinds.update({failure_kind(f.split(": ", 1)[-1]) for f in r.failures})

    positions = Counter()
    ratios = []
    for r in drafted:
        options = r.question["options"]
        correct = [i for i, o in enumerate(options) if o["correct"]]
        if len(correct) == 1:
            positions[correct[0]] += 1
            others = [len(o["content"]) for o in options if not o["correct"]]
            if others and statistics.mean(others) > 0:
                ratios.append(len(options[correct[0]]["content"]) / statistics.mean(others))

    return {
        "drafts": n,
        "ok": statuses[OutcomeStatus.OK.value],
        "needs_human_attention": statuses[OutcomeStatus.NEEDS_HUMAN_ATTENTION.value],
        "insufficient_context": statuses[OutcomeStatus.INSUFFICIENT_CONTEXT.value],
        "errors": statuses["ERROR"],
        # the draft came out right at the first attempt, with no rewrite
        "first_try_rate": sum(1 for r in results if r.status == "OK" and r.retries == 0) / n,
        "ok_rate": statuses[OutcomeStatus.OK.value] / n,
        "mean_retries": statistics.mean(r.retries for r in results),
        "failure_kinds": dict(kinds.most_common()),
        "mean_llm_calls": statistics.mean(r.llm_calls for r in results),
        "mean_seconds": statistics.mean(r.seconds for r in results),
        "correct_position": {str(k): positions[k] for k in sorted(positions)},
        "mean_correct_length_ratio": round(statistics.mean(ratios), 2) if ratios else None,
    }


def render_markdown(summaries: dict[str, dict]) -> str:
    """A table with one column per variant (model and/or prompt)."""
    names = list(summaries)
    rows = [
        ("drafts", lambda s: s["drafts"]),
        ("OK", lambda s: f'{s["ok"]} ({s["ok_rate"]:.0%})'),
        ("first try (no rewrite)", lambda s: f'{s["first_try_rate"]:.0%}'),
        ("needs human attention", lambda s: s["needs_human_attention"]),
        ("insufficient context", lambda s: s["insufficient_context"]),
        ("provider errors", lambda s: s["errors"]),
        ("mean retries", lambda s: f'{s["mean_retries"]:.2f}'),
        ("model calls per draft", lambda s: f'{s["mean_llm_calls"]:.1f}'),
        ("seconds per draft", lambda s: f'{s["mean_seconds"]:.1f}'),
        ("correct option length / average distractor", lambda s: s["mean_correct_length_ratio"] if s["mean_correct_length_ratio"] is not None else "-"),
        ("position of the correct option", lambda s: ", ".join(f"{k}:{v}" for k, v in s["correct_position"].items()) or "-"),
    ]
    kinds = sorted({k for s in summaries.values() if s["drafts"] for k in s["failure_kinds"]})
    lines = ["| | " + " | ".join(names) + " |", "|---|" + "---|" * len(names)]
    for label, get in rows:
        lines.append(f"| {label} | " + " | ".join(str(get(s)) if s["drafts"] else "-" for s in summaries.values()) + " |")
    for kind in kinds:
        lines.append(f"| failed on {kind} (drafts) | " + " | ".join(str(s.get("failure_kinds", {}).get(kind, 0)) for s in summaries.values()) + " |")
    return "\n".join(lines) + "\n"
