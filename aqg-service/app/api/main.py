import threading
import uuid
from typing import Callable

from fastapi import BackgroundTasks, FastAPI, File, Form, HTTPException, UploadFile

from app.config import Settings, default_model_config
from app.evaluation.pipeline import generate_verified_question
from app.generation.prompts import PROMPT_VERSION
from app.ingestion.adapters import ContentAdapter, MarkerAdapter, UnsupportedFormat, adapter_for
from app.llm.provider import LiteLLMProvider, LLMProvider
from app.materials import MaterialStore, process_material
from app.retrieval.retriever import LexicalRetriever, Retriever
from app.schema import GenerationRequest, Job, JobStatus, Material, ModelConfig
from app.storage.material_storage import LocalMaterialStorage, MaterialStorage

ProviderFactory = Callable[[ModelConfig], LLMProvider]

MAX_UPLOAD_BYTES = 100 * 1024 * 1024


def create_app(
    settings: Settings | None = None,
    provider_factory: ProviderFactory = LiteLLMProvider,
    marker: ContentAdapter | None = None,
    retriever: Retriever | None = None,
    storage: MaterialStorage | None = None,
) -> FastAPI:
    settings = settings or Settings.from_env()
    marker = marker or MarkerAdapter()
    retriever = retriever or LexicalRetriever()
    storage = storage or LocalMaterialStorage(settings.materials_dir)
    materials = MaterialStore()
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

    @app.post("/materials", status_code=202)
    def upload_material(
        background: BackgroundTasks, course_id: int = Form(), file: UploadFile = File()
    ) -> Material:
        filename = file.filename or ""
        try:
            adapter_for(filename, marker)
        except UnsupportedFormat as error:
            raise HTTPException(status_code=415, detail=str(error)) from error
        content = file.file.read(MAX_UPLOAD_BYTES + 1)
        if len(content) > MAX_UPLOAD_BYTES:
            raise HTTPException(status_code=413, detail="file is larger than 100 MB")

        material = Material(id=uuid.uuid4().hex, course_id=course_id, filename=filename)
        path = storage.save(course_id, material.id, filename, content)
        materials.add(material)
        background.add_task(process_material, materials, material.id, path, marker)
        return material

    @app.get("/materials/{material_id}")
    def get_material(material_id: str) -> Material:
        material = materials.get(material_id)
        if material is None:
            raise HTTPException(status_code=404, detail="material not found")
        return material

    @app.get("/courses/{course_id}/materials")
    def list_materials(course_id: int) -> list[Material]:
        return materials.list_for_course(course_id)

    @app.post("/generate", status_code=202)
    def generate(request: GenerationRequest, background: BackgroundTasks) -> Job:
        if request.material_ids:
            try:
                available = materials.chunks_for(request.course_id, request.material_ids)
            except LookupError as error:
                raise HTTPException(status_code=422, detail=str(error)) from error
            retrieved = retriever.top_k(request.topic, available, request.top_k)
            if not retrieved and not request.chunks:
                raise HTTPException(status_code=422, detail="the materials have nothing relevant to the topic")
            request = request.model_copy(update={"chunks": [*request.chunks, *retrieved]})

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
