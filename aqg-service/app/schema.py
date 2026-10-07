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


class GenerationRequest(BaseModel):
    course_id: int
    topic: str
    difficulty: Difficulty = Difficulty.MEDIUM
    count: int = Field(default=1, ge=1, le=20)
    grounding_mode: GroundingMode = GroundingMode.STRICT
    chunks: list[Chunk] = Field(default_factory=list)
    material_ids: list[str] = Field(default_factory=list)
    top_k: int = Field(default=5, ge=1, le=20)
    style_examples: list[str] = Field(default_factory=list, max_length=3)
    # e.g. "Portuguese"; None keeps the language of the materials
    language: str | None = Field(default=None, max_length=40)
    revision: Revision | None = None
    # Heading paths to draw from (a path also covers its subsections); empty means everything
    sections: list[str] = Field(default_factory=list, max_length=500)
    # Sections to read per material, matched exactly: a path does not cover the ones below it.
    # Used when the selection comes from a topic tree, where a subsection may belong to another
    # topic. A material listed here is read even if it is not in material_ids.
    material_sections: dict[str, list[str]] = Field(default_factory=dict, max_length=100)
    # What to ask about inside the topic, e.g. "sign rules of the product"
    focus: str | None = Field(default=None, max_length=500)
    # Stems already in the course, so new drafts that repeat one are retried
    existing_stems: list[str] = Field(default_factory=list, max_length=5000)
    model: ModelConfig | None = None

    @model_validator(mode="after")
    def needs_context(self) -> "GenerationRequest":
        if not self.chunks and not self.material_ids and not self.material_sections:
            raise ValueError("provide chunks or material_ids")
        if any(len(paths) > 500 for paths in self.material_sections.values()):
            raise ValueError("at most 500 sections per material")
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


class Section(BaseModel):
    """A heading path of a material (e.g. "Part I > 1 Numbers > §3. Rules for multiplication")."""

    path: str
    title: str
    chunk_count: int


class OutlineEdit(BaseModel):
    """One change to the sections of a document."""

    op: Literal["rename", "merge", "shift", "split"]
    path: str = Field(min_length=1, max_length=1000)
    # rename: the new name; split: the name of the new section
    title: str | None = Field(default=None, max_length=200)
    # shift: -1 moves the section up a level, +1 under the section above it
    delta: int | None = None
    # split: the new section starts at this paragraph (counting from 0) of the section
    paragraph: int | None = Field(default=None, ge=0)


class OutlineNode(BaseModel):
    """A heading of a document, with the text under it, to edit the sections by hand."""

    path: str
    title: str
    depth: int
    has_text: bool
    paragraph_count: int
    preview: str


class OutlineEditResult(BaseModel):
    # old section path -> where its text is now; paths not listed did not change
    path_map: dict[str, list[str]]
    outline: list[OutlineNode]


class Paragraph(BaseModel):
    index: int
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
    # Where the question's topics are taught (document -> sections); empty = no course material
    material_sections: dict[str, list[str]] = Field(default_factory=dict, max_length=100)
    top_k: int = Field(default=3, ge=1, le=10)
    language: str | None = Field(default=None, max_length=40)
    model: "ModelConfig | None" = None


class DiscussionSuggestion(BaseModel):
    reply: str
    # Sections of the course material the draft was written with
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
