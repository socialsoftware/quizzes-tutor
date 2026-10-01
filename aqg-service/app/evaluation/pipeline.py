from app.evaluation.distractor_check import check_distractors
from app.evaluation.duplicate_check import find_duplicate
from app.evaluation.format_check import check_format
from app.evaluation.grounding_check import check_grounding
from app.evaluation.structural_validator import validate_structure
from app.generation.synthesizer import InsufficientContext, ModelReplyError, synthesize
from app.llm.provider import LLMProvider
from app.schema import GenerationOutcome, GenerationRequest, OutcomeStatus


def generate_verified_question(
    request: GenerationRequest,
    provider: LLMProvider,
    max_retries: int,
    seen_stems: list[str] | None = None,
) -> GenerationOutcome:
    """Generate one question and run it through the verification chain (structure, then
    grounding, then distractors; structure covers the regex format checks and repeats of
    `seen_stems`). A failed check sends its reasons back into the next
    attempt; once the retries are spent the draft is flagged for the teacher instead of
    looping or being silently dropped."""
    failures: list[str] = []
    feedback: str | None = None
    last_draft = None
    supporting: list[str] = []

    for attempt in range(max_retries + 1):
        try:
            draft = synthesize(request, provider, feedback)
        except InsufficientContext as error:
            failures.append(f"insufficient context: {error.reason}")
            return GenerationOutcome(
                status=OutcomeStatus.INSUFFICIENT_CONTEXT, retries=attempt, failures=failures
            )
        except ModelReplyError as error:
            problems = [str(error)]
        else:
            last_draft = draft
            problems = validate_structure(draft) + check_format(draft)
            duplicate = find_duplicate(draft.stem, seen_stems or [])
            if duplicate:
                problems.append(f'the question repeats an existing one: "{duplicate[:200]}"')
            if not problems:
                grounding = check_grounding(draft, request.chunks, request.grounding_mode, provider)
                supporting = grounding.supporting_chunk_ids
                problems = grounding.problems
            if not problems:
                problems = check_distractors(draft, provider)
            if not problems:
                return GenerationOutcome(
                    status=OutcomeStatus.OK,
                    question=draft,
                    retries=attempt,
                    failures=failures,
                    source_chunk_ids=supporting,
                )

        failures.extend(f"attempt {attempt + 1}: {problem}" for problem in problems)
        feedback = "\n".join(f"- {problem}" for problem in problems)

    return GenerationOutcome(
        status=OutcomeStatus.NEEDS_HUMAN_ATTENTION,
        question=last_draft,
        retries=max_retries,
        failures=failures,
        source_chunk_ids=supporting,
    )
