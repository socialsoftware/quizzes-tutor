from dataclasses import dataclass, field

from app.generation.prompts import GROUNDING_CHECK_SYSTEM, build_grounding_check_prompt
from app.generation.synthesizer import ModelReplyError, parse_json_reply
from app.llm.provider import LLMProvider
from app.schema import Chunk, GeneratedMCQ, GroundingMode

MIN_ANCHORED_DISTRACTORS = 2


@dataclass
class GroundingResult:
    problems: list[str] = field(default_factory=list)
    supporting_chunk_ids: list[str] = field(default_factory=list)


def check_grounding(
    question: GeneratedMCQ, chunks: list[Chunk], mode: GroundingMode, provider: LLMProvider
) -> GroundingResult:
    prompt = build_grounding_check_prompt(chunks, question.model_dump_json(exclude={"difficulty", "topic"}))
    try:
        report = parse_json_reply(provider.complete(GROUNDING_CHECK_SYSTEM, prompt))
    except ModelReplyError as error:
        return GroundingResult(problems=[f"grounding check failed to run: {error}"])

    known_ids = {chunk.id for chunk in chunks}
    cited = [str(chunk_id) for chunk_id in report.get("correct_chunk_ids") or []]
    valid_cited = [chunk_id for chunk_id in cited if chunk_id in known_ids]

    problems: list[str] = []
    if not report.get("correct_supported") or not valid_cited:
        problems.append("the correct option is not supported by any provided chunk")

    unsupported = report.get("unsupported_claims") or []
    if mode is GroundingMode.STRICT and unsupported:
        problems.append("claims not supported by the context: " + "; ".join(map(str, unsupported)))

    if mode is GroundingMode.ENRICHED:
        anchored = report.get("distractors_anchored")
        if not isinstance(anchored, int) or anchored < MIN_ANCHORED_DISTRACTORS:
            problems.append(f"fewer than {MIN_ANCHORED_DISTRACTORS} distractors are anchored in the context")

    return GroundingResult(problems=problems, supporting_chunk_ids=valid_cited)
