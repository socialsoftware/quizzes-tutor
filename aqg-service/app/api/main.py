import threading
import uuid
from typing import Callable

from fastapi import BackgroundTasks, FastAPI, HTTPException

from app.config import Settings, default_model_config
from app.evaluation.pipeline import generate_verified_question
from app.generation.prompts import PROMPT_VERSION
from app.llm.provider import LiteLLMProvider, LLMProvider
from app.schema import GenerationRequest, Job, JobStatus, ModelConfig

ProviderFactory = Callable[[ModelConfig], LLMProvider]


def create_app(
    settings: Settings | None = None, provider_factory: ProviderFactory = LiteLLMProvider
) -> FastAPI:
    settings = settings or Settings.from_env()
    app = FastAPI(title="Quizzes Tutor AQG service")
    jobs: dict[str, Job] = {}
    lock = threading.Lock()

    def run_job(job_id: str, request: GenerationRequest, provider: LLMProvider) -> None:
        with lock:
            jobs[job_id].status = JobStatus.RUNNING
        try:
            outcomes = [
                generate_verified_question(request, provider, settings.max_retries)
                for _ in range(request.count)
            ]
        except Exception as error:  # provider outages must fail the job, not the worker
            with lock:
                jobs[job_id].status = JobStatus.FAILED
                jobs[job_id].error = f"{type(error).__name__}: {error}"
            return
        with lock:
            jobs[job_id].outcomes = outcomes
            jobs[job_id].status = JobStatus.DONE

    @app.get("/health")
    def health() -> dict:
        return {"status": "ok", "provider": settings.provider}

    @app.post("/generate", status_code=202)
    def generate(request: GenerationRequest, background: BackgroundTasks) -> Job:
        model_config = request.model or default_model_config(settings)
        provider = provider_factory(model_config)
        job = Job(
            id=uuid.uuid4().hex,
            prompt_version=PROMPT_VERSION,
            model_id=model_config.model,
            grounding_mode=request.grounding_mode,
        )
        with lock:
            jobs[job.id] = job
        background.add_task(run_job, job.id, request, provider)
        return job

    @app.get("/jobs/{job_id}")
    def get_job(job_id: str) -> Job:
        with lock:
            job = jobs.get(job_id)
            if job is None:
                raise HTTPException(status_code=404, detail="job not found")
            return job.model_copy(deep=True)

    return app
