from datetime import datetime, timezone

from sqlalchemy import func, select
from sqlalchemy.orm import sessionmaker

from app.db import JobRow
from app.schema import GenerationOutcome, GroundingMode, Job, JobStatus


class JobStore:
    """Generation jobs and their results, persisted in the aqg schema."""

    def __init__(self, session_factory: sessionmaker) -> None:
        self._session = session_factory

    def add(self, job: Job, course_id: int | None = None, question_count: int = 0) -> None:
        with self._session.begin() as session:
            session.add(
                JobRow(
                    course_id=course_id,
                    question_count=question_count,
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

    def pending(self) -> tuple[int, int]:
        """Jobs waiting or being written, and the questions they still owe."""
        unfinished = (JobStatus.PENDING.value, JobStatus.RUNNING.value)
        with self._session() as session:
            jobs, questions = session.execute(
                select(func.count(JobRow.id), func.coalesce(func.sum(JobRow.question_count), 0)).where(JobRow.status.in_(unfinished))
            ).one()
        return int(jobs), int(questions)

    def requested_today(self, course_id: int) -> int:
        """Questions the course asked for since 00:00 UTC (failed jobs count: they used the model)."""
        midnight = datetime.now(timezone.utc).replace(hour=0, minute=0, second=0, microsecond=0)
        with self._session() as session:
            total = session.execute(
                select(func.coalesce(func.sum(JobRow.question_count), 0)).where(
                    JobRow.course_id == course_id, JobRow.created_at >= midnight
                )
            ).scalar_one()
        return int(total)

    def fail_unfinished(self, reason: str) -> int:
        """At start-up: a job that was waiting or running died with the previous process, and
        would otherwise count against the queue for ever."""
        unfinished = (JobStatus.PENDING.value, JobStatus.RUNNING.value)
        with self._session.begin() as session:
            rows = list(session.scalars(select(JobRow).where(JobRow.status.in_(unfinished))))
            for row in rows:
                row.status = JobStatus.FAILED.value
                row.error = reason
        return len(rows)

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
