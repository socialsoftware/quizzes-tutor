from enum import Enum
from typing import Literal

from pydantic import BaseModel, Field, model_validator


class Difficulty(str, Enum):
    EASY = "EASY"
    MEDIUM = "MEDIUM"
    HARD = "HARD"


class GroundingMode(str, Enum):
    STRICT = "STRICT"
    ENRICHED = "ENRICHED"


class Chunk(BaseModel):
    id: str
    text: str
    source: str | None = None


class MCQOption(BaseModel):
    content: str
    correct: bool


class GeneratedMCQ(BaseModel):
    stem: str
    options: list[MCQOption]
    explanation: str = ""
    difficulty: Difficulty | None = None
    topic: str | None = None


class ModelConfig(BaseModel):
    model: str
    api_base: str | None = None
    api_key: str | None = None
    temperature: float = 0.7
    # Seconds before a call is abandoned, so a stalled provider fails the job
    timeout: float = 120
    # Provider-specific request fields (e.g. NIM's chat_template_kwargs)
    extra_body: dict | None = None


class Revision(BaseModel):
    """A question to rewrite following a teacher's review ("Regenerate with review")."""

    previous: GeneratedMCQ
    review: str = Field(min_length=1, max_length=4000)


MAX_CHUNK_IDS = 5000


class GenerationRequest(BaseModel):
    course_id: int
    topic: str
    difficulty: Difficulty = Difficulty.MEDIUM
    count: int = Field(default=1, ge=1, le=20)
    grounding_mode: GroundingMode = GroundingMode.STRICT
    # Text given inline (tests, tools); the Tutor sends chunk_ids instead
    chunks: list[Chunk] = Field(default_factory=list)
    # The pieces of the course materials the teacher put under the topic and its subtopics
    chunk_ids: list[str] = Field(default_factory=list, max_length=MAX_CHUNK_IDS)
    style_examples: list[str] = Field(default_factory=list, max_length=3)
    # e.g. "Portuguese"; None keeps the language of the materials
    language: str | None = Field(default=None, max_length=40)
    revision: Revision | None = None
    # What to ask about inside the topic, e.g. "sign rules of the product"
    focus: str | None = Field(default=None, max_length=500)
    # Stems already in the course, so new drafts that repeat one are retried
    existing_stems: list[str] = Field(default_factory=list, max_length=5000)
    model: ModelConfig | None = None

    @model_validator(mode="after")
    def needs_context(self) -> "GenerationRequest":
        if not self.chunks and not self.chunk_ids:
            raise ValueError("provide chunks or chunk_ids")
        return self


class OutcomeStatus(str, Enum):
    OK = "OK"
    NEEDS_HUMAN_ATTENTION = "NEEDS_HUMAN_ATTENTION"
    INSUFFICIENT_CONTEXT = "INSUFFICIENT_CONTEXT"


class GenerationOutcome(BaseModel):
    status: OutcomeStatus
    question: GeneratedMCQ | None = None
    retries: int = 0
    failures: list[str] = Field(default_factory=list)
    source_chunk_ids: list[str] = Field(default_factory=list)


class JobStatus(str, Enum):
    PENDING = "PENDING"
    RUNNING = "RUNNING"
    DONE = "DONE"
    FAILED = "FAILED"


class Job(BaseModel):
    id: str
    status: JobStatus = JobStatus.PENDING
    prompt_version: str
    model_id: str
    grounding_mode: GroundingMode
    outcomes: list[GenerationOutcome] = Field(default_factory=list)
    error: str | None = None


class MaterialStatus(str, Enum):
    PROCESSING = "PROCESSING"
    READY = "READY"
    FAILED = "FAILED"


class MaterialChunk(BaseModel):
    """A piece of a material as the teacher sees it when putting the material under topics."""

    id: str
    position: int
    # The headings above the piece ("Part I > 1 Numbers"), only to find one's way in the document
    heading: str | None = None
    text: str


class DiscussionTurn(BaseModel):
    role: Literal["student", "teacher"]
    message: str = Field(min_length=1, max_length=4000)


class DiscussionSuggestRequest(BaseModel):
    """A student's doubt about a question, to draft the teacher's reply. Names are never sent."""

    course_id: int
    question_stem: str = Field(min_length=1, max_length=4000)
    options: list[MCQOption] = Field(default_factory=list, max_length=10)
    explanation: str = Field(default="", max_length=4000)
    student_choice: str | None = Field(default=None, max_length=1000)
    student_message: str = Field(min_length=1, max_length=4000)
    replies: list[DiscussionTurn] = Field(default_factory=list, max_length=20)
    # The pieces of the course materials under the question's topics; empty = no course material
    chunk_ids: list[str] = Field(default_factory=list, max_length=MAX_CHUNK_IDS)
    top_k: int = Field(default=3, ge=1, le=10)
    language: str | None = Field(default=None, max_length=40)
    model: "ModelConfig | None" = None


class DiscussionSuggestion(BaseModel):
    reply: str
    # Headings of the course material the draft was written with
    sources: list[str] = Field(default_factory=list)


class Material(BaseModel):
    id: str
    course_id: int
    filename: str
    status: MaterialStatus = MaterialStatus.PROCESSING
    chunk_count: int = 0
    parser: str | None = None
    parse_seconds: float | None = None
    error: str | None = None
