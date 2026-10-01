import json

import pytest

from app.schema import Chunk, GenerationRequest


class FakeLLM:
    """Replies with scripted answers in order and records every prompt it receives."""

    model_id = "fake/model"

    def __init__(self, replies):
        self.replies = list(replies)
        self.prompts = []
        self.systems = []

    def complete(self, system, user):
        self.prompts.append(user)
        self.systems.append(system)
        reply = self.replies.pop(0)
        return reply if isinstance(reply, str) else json.dumps(reply)


def good_question():
    return {
        "stem": "What does HTTP status 404 mean?",
        "options": [
            {"content": "Resource not found", "correct": True},
            {"content": "Server error", "correct": False},
            {"content": "Unauthorized", "correct": False},
            {"content": "Redirect", "correct": False},
        ],
        "explanation": "404 signals a missing resource.",
    }


GROUNDED = {
    "correct_supported": True,
    "correct_chunk_ids": ["c1"],
    "distractors_anchored": 3,
    "unsupported_claims": [],
}
CLEAN_DISTRACTORS = {"also_correct": [], "implausible": []}


@pytest.fixture
def request_strict():
    return GenerationRequest(
        course_id=1,
        topic="HTTP",
        chunks=[Chunk(id="c1", text="404 means the server cannot find the resource.")],
    )
