from enum import Enum

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
    model: ModelConfig | None = None

    @model_validator(mode="after")
    def needs_context(self) -> "GenerationRequest":
        if not self.chunks and not self.material_ids:
            raise ValueError("provide chunks or material_ids")
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


class Material(BaseModel):
    id: str
    course_id: int
    filename: str
    status: MaterialStatus = MaterialStatus.PROCESSING
    chunk_count: int = 0
    error: str | None = None
