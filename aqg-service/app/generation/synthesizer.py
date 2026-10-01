import json
import re

from pydantic import ValidationError

from app.generation.prompts import build_generation_prompt, system_prompt
from app.llm.provider import LLMProvider
from app.schema import GeneratedMCQ, GenerationRequest


class ModelReplyError(Exception):
    """The model reply could not be turned into the expected JSON shape."""


class InsufficientContext(Exception):
    def __init__(self, reason: str):
        super().__init__(reason)
        self.reason = reason


_FENCE = re.compile(r"^```(?:json)?\s*(.*?)\s*```$", re.DOTALL)


def parse_json_reply(raw: str) -> dict:
    text = raw.strip()
    fenced = _FENCE.match(text)
    if fenced:
        text = fenced.group(1)
    try:
        data = json.loads(text)
    except json.JSONDecodeError as error:
        raise ModelReplyError(f"reply is not valid JSON ({error.msg})") from error
    if not isinstance(data, dict):
        raise ModelReplyError("reply is not a JSON object")
    return data


def synthesize(
    request: GenerationRequest, provider: LLMProvider, feedback: str | None = None
) -> GeneratedMCQ:
    raw = provider.complete(system_prompt(request.language), build_generation_prompt(request, feedback))
    data = parse_json_reply(raw)
    if data.get("insufficient_context"):
        raise InsufficientContext(str(data.get("reason", "")))
    try:
        question = GeneratedMCQ.model_validate(data)
    except ValidationError as error:
        raise ModelReplyError(f"reply does not match the question schema: {error.errors()[0]['msg']}") from error
    question.difficulty = request.difficulty
    question.topic = request.topic
    return question
