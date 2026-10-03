import uuid
from typing import Callable

from fastapi import BackgroundTasks, FastAPI, File, Form, HTTPException, UploadFile

from app.config import Settings, default_model_config
from app.db import create_session_factory
from app.evaluation.duplicate_check import normalise
from app.evaluation.pipeline import generate_verified_question
from app.generation.prompts import PROMPT_VERSION
from app.ingestion.adapters import Parsers, UnsupportedFormat, adapter_for, build_parsers
from app.llm.provider import LiteLLMProvider, LLMProvider
from app.jobs import JobStore
from app.materials import MaterialStore, process_material, reprocess_material
from app.retrieval.retriever import LexicalRetriever, Retriever
from app.schema import GenerationRequest, Job, Material, ModelConfig, Section
from app.storage.material_storage import LocalMaterialStorage, MaterialStorage

ProviderFactory = Callable[[ModelConfig], LLMProvider]

MAX_UPLOAD_BYTES = 100 * 1024 * 1024


def create_app(
    settings: Settings | None = None,
    provider_factory: ProviderFactory = LiteLLMProvider,
    parsers: Parsers | None = None,
    retriever: Retriever | None = None,
    storage: MaterialStorage | None = None,
) -> FastAPI:
    settings = settings or Settings.from_env()
    parsers = parsers or build_parsers(settings.pdf_parser, settings.office_parser)
    retriever = retriever or LexicalRetriever()
    storage = storage or LocalMaterialStorage(settings.materials_dir)
    session_factory = create_session_factory(settings.database_url)
    materials = MaterialStore(session_factory)
    jobs = JobStore(session_factory)
    app = FastAPI(title="Quizzes Tutor AQG service")
    app.state.materials = materials

    def run_job(job_id: str, request: GenerationRequest, provider: LLMProvider) -> None:
        jobs.set_running(job_id)
        try:
            # Drafts of this job count as existing too, so a job never repeats itself
            seen = list(request.existing_stems)
            if request.revision:
                # The version being rewritten is not a question to keep away from
                previous = normalise(request.revision.previous.stem)
                seen = [stem for stem in seen if normalise(stem) != previous]
            outcomes = []
            for _ in range(request.count):
                outcome = generate_verified_question(request, provider, settings.max_retries, seen)
                if outcome.question:
                    seen.append(outcome.question.stem)
                outcomes.append(outcome)
        except Exception as error:  # provider outages must fail the job, not the worker
            jobs.set_failed(job_id, f"{type(error).__name__}: {error}")
            return
        jobs.set_done(job_id, outcomes)

    @app.get("/health")
    def health() -> dict:
        return {"status": "ok", "provider": settings.provider}

    @app.post("/materials", status_code=202)
    def upload_material(
        background: BackgroundTasks, course_id: int = Form(), file: UploadFile = File()
    ) -> Material:
        filename = file.filename or ""
        try:
            adapter_for(filename, parsers)
        except UnsupportedFormat as error:
            raise HTTPException(status_code=415, detail=str(error)) from error
        content = file.file.read(MAX_UPLOAD_BYTES + 1)
        if len(content) > MAX_UPLOAD_BYTES:
            raise HTTPException(status_code=413, detail="file is larger than 100 MB")

        material = Material(id=uuid.uuid4().hex, course_id=course_id, filename=filename)
        path = storage.save(course_id, material.id, filename, content)
        materials.add(material)
        background.add_task(
            process_material, materials, material.id, path, parsers, storage, settings.keep_originals
        )
        return material

    @app.get("/materials/{material_id}/sections")
    def get_sections(material_id: str) -> list[Section]:
        if materials.get(material_id) is None:
            raise HTTPException(status_code=404, detail="material not found")
        return materials.sections(material_id)

    @app.post("/materials/{material_id}/reprocess", status_code=202)
    def reprocess(material_id: str, background: BackgroundTasks) -> Material:
        """Rebuilds the chunks after a parser or heading-cleanup change."""
        material = materials.get(material_id)
        if material is None:
            raise HTTPException(status_code=404, detail="material not found")
        materials.set_processing(material_id)
        background.add_task(reprocess_material, materials, material_id, parsers, storage)
        return materials.get(material_id)

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
                available = materials.chunks_for(request.course_id, request.material_ids, request.sections)
            except LookupError as error:
                raise HTTPException(status_code=422, detail=str(error)) from error
            if request.sections and not available:
                raise HTTPException(status_code=422, detail="the selected sections have no text")
            query = f"{request.topic} {request.focus}" if request.focus else request.topic
            retrieved = retriever.top_k(query, available, request.top_k)
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
        jobs.add(job)
        background.add_task(run_job, job.id, request, provider)
        return job

    @app.get("/jobs/{job_id}")
    def get_job(job_id: str) -> Job:
        job = jobs.get(job_id)
        if job is None:
            raise HTTPException(status_code=404, detail="job not found")
        return job

    return app
