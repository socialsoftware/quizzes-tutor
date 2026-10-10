from sqlalchemy.orm import sessionmaker

from app.db import JobRow
from app.schema import GenerationOutcome, GroundingMode, Job, JobStatus


class JobStore:
    """Generation jobs and their results, persisted in the aqg schema."""

    def __init__(self, session_factory: sessionmaker) -> None:
        self._session = session_factory

    def add(self, job: Job) -> None:
        with self._session.begin() as session:
            session.add(
                JobRow(
                    id=job.id,
                    status=job.status.value,
                    prompt_version=job.prompt_version,
                    model_id=job.model_id,
                    grounding_mode=job.grounding_mode.value,
                    outcomes=[],
                )
            )

    def get(self, job_id: str) -> Job | None:
        with self._session() as session:
            row = session.get(JobRow, job_id)
            if row is None:
                return None
            return Job(
                id=row.id,
                status=JobStatus(row.status),
                prompt_version=row.prompt_version,
                model_id=row.model_id,
                grounding_mode=GroundingMode(row.grounding_mode),
                outcomes=[GenerationOutcome.model_validate(o) for o in row.outcomes],
                error=row.error,
            )

    def set_running(self, job_id: str) -> None:
        self._update(job_id, status=JobStatus.RUNNING.value)

    def set_done(self, job_id: str, outcomes: list[GenerationOutcome], model_id: str | None = None) -> None:
        values = {"status": JobStatus.DONE.value, "outcomes": [o.model_dump(mode="json") for o in outcomes]}
        if model_id:
            values["model_id"] = model_id[:128]
        self._update(job_id, **values)

    def set_failed(self, job_id: str, error: str) -> None:
        self._update(job_id, status=JobStatus.FAILED.value, error=error)

    def _update(self, job_id: str, **values) -> None:
        with self._session.begin() as session:
            row = session.get(JobRow, job_id)
            for name, value in values.items():
                setattr(row, name, value)
