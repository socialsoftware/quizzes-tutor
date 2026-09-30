from app.generation.prompts import DISTRACTOR_CHECK_SYSTEM, build_distractor_check_prompt
from app.generation.synthesizer import ModelReplyError, parse_json_reply
from app.llm.provider import LLMProvider
from app.schema import GeneratedMCQ


def check_distractors(question: GeneratedMCQ, provider: LLMProvider) -> list[str]:
    prompt = build_distractor_check_prompt(question.model_dump_json(exclude={"difficulty", "topic"}))
    try:
        report = parse_json_reply(provider.complete(DISTRACTOR_CHECK_SYSTEM, prompt))
    except ModelReplyError as error:
        return [f"distractor check failed to run: {error}"]

    problems: list[str] = []
    also_correct = report.get("also_correct") or []
    implausible = report.get("implausible") or []
    if also_correct:
        problems.append(f"options {sorted(also_correct)} (0-based) are also correct")
    if implausible:
        problems.append(f"options {sorted(implausible)} (0-based) are implausible distractors")
    return problems
