import logging
import threading
import time
import uuid
from typing import Callable

from fastapi import BackgroundTasks, FastAPI, File, Form, HTTPException, Request, UploadFile
from fastapi.responses import JSONResponse

from app.config import Settings
from app.db import create_session_factory
from app.evaluation.duplicate_check import normalise
from app.evaluation.pipeline import generate_verified_question
from app.generation.context_plan import plan_contexts
from app.generation.discussion import suggest_reply
from app.generation.prompts import PROMPT_VERSION
from app.generation.synthesizer import ModelReplyError
from app.ingestion.adapters import Parsers, UnsupportedFormat, adapter_for, build_parsers
from app.llm.catalog import ModelCatalog
from app.llm.ollama import OllamaClient
from app.limits import Limiters, RateLimited, RateLimitedProvider, same_secret
from app.llm.provider import LiteLLMProvider, LLMProvider, LoggingProvider, build_provider
from app.llm.settings import (
    LlmSettings,
    LlmSettingsStore,
    LlmSettingsView,
    ModelChoice,
    ModelTestResult,
    Provider,
    OllamaModels,
    ProviderModels,
    missing_keys,
    model_config_for,
)
from app.jobs import JobStore
from app.materials import MaterialStore, process_material, reprocess_material
from app.retrieval.retriever import LexicalRetriever, Retriever
from app.schema import (
    Chunk,
    DiscussionSuggestion,
    DiscussionSuggestRequest,
    GenerationRequest,
    Job,
    Material,
    MaterialChunk,
    ModelConfig,
)
from app.storage.material_storage import LocalMaterialStorage, MaterialStorage

ProviderFactory = Callable[[ModelConfig], LLMProvider]

MAX_UPLOAD_BYTES = 100 * 1024 * 1024

logger = logging.getLogger(__name__)


def create_app(
    settings: Settings | None = None,
    provider_factory: ProviderFactory = LiteLLMProvider,
    parsers: Parsers | None = None,
    retriever: Retriever | None = None,
    storage: MaterialStorage | None = None,
    ollama: OllamaClient | None = None,
    catalog: ModelCatalog | None = None,
) -> FastAPI:
    settings = settings or Settings.from_env()
    parsers = parsers or build_parsers(settings.pdf_parser, settings.office_parser)
    retriever = retriever or LexicalRetriever()
    storage = storage or LocalMaterialStorage(settings.materials_dir)
    session_factory = create_session_factory(settings.database_url)
    materials = MaterialStore(session_factory)
    jobs = JobStore(session_factory)
    llm_settings = LlmSettingsStore(session_factory, LlmSettings.from_env(settings))
    ollama = ollama or OllamaClient()
    catalog = catalog or ModelCatalog(ollama)
    # Model -> state of its last download to the Ollama server ("downloading", "done" or the error)
    ollama_pulls: dict[str, str] = {}
    # With an API key the interactive documentation is off: nobody browses it, and it lists every endpoint
    docs = {} if not settings.api_key else {"docs_url": None, "redoc_url": None, "openapi_url": None}
    app = FastAPI(title="Quizzes Tutor AQG service", **docs)
    app.state.materials = materials
    app.state.jobs = jobs
    app.state.session_factory = session_factory
    limiters = Limiters(settings.llm_requests_per_minute)
    # A job waiting for a turn holds a worker thread, which is why the number of queued jobs is capped
    job_slots = threading.BoundedSemaphore(max(settings.max_concurrent_jobs, 1))
    admission = threading.Lock()
    died = jobs.fail_unfinished("The service was restarted before the job finished")
    if died:
        logger.warning("%d unfinished job(s) of the previous run were marked as failed", died)

    if settings.api_key:

        @app.middleware("http")
        async def require_api_key(request: Request, call_next):
            if request.url.path != "/health" and not same_secret(request.headers.get("x-api-key"), settings.api_key):
                return JSONResponse({"detail": "missing or wrong API key"}, status_code=401)
            return await call_next(request)

    else:
        logger.warning("AQG_API_KEY is not set: anyone who can reach this service can use it. Set it in production.")
    app.state.llm_settings = llm_settings

    if settings.log_prompts:
        logging.getLogger("aqg.prompts").setLevel(logging.INFO)
        if not logging.getLogger("aqg.prompts").handlers:
            logging.getLogger("aqg.prompts").addHandler(logging.StreamHandler())

    def provider_for(
        request_model: ModelConfig | None, llm: LlmSettings, log_prompts: bool = False, interactive: bool = False
    ) -> tuple[LLMProvider, str]:
        """The settings' model and its fallbacks. With LOG_PROMPTS on, the questions' prompts are
        logged; those of discussions never are, because they hold what a student wrote. Cloud
        models draw on a per-minute budget; a job waits for it, an `interactive` call (a teacher
        pressing a button) gives up quickly and the teacher is told to try again."""
        if request_model is not None and not settings.allow_request_model:
            raise HTTPException(status_code=422, detail="choosing the model per request is not allowed")
        configs = [request_model] if request_model else llm.model_configs()
        wait = settings.interactive_rate_limit_wait if interactive else settings.rate_limit_wait

        def build(config: ModelConfig) -> LLMProvider:
            provider = provider_factory(config)
            if log_prompts and settings.log_prompts:
                provider = LoggingProvider(provider)
            limiter = limiters.for_model(config.model)
            return RateLimitedProvider(provider, limiter, wait) if limiter else provider

        return build_provider(configs, build), configs[0].model

    def run_job(
        job_id: str, request: GenerationRequest, provider: LLMProvider, contexts: list[list[Chunk]], max_retries: int
    ) -> None:
        with job_slots:  # at most MAX_CONCURRENT_JOBS jobs write at the same time; the rest wait here
            _write_job(job_id, request, provider, contexts, max_retries)

    def _write_job(
        job_id: str, request: GenerationRequest, provider: LLMProvider, contexts: list[list[Chunk]], max_retries: int
    ) -> None:
        jobs.set_running(job_id)
        try:
            # Drafts of this job count as existing too, so a job never repeats itself
            seen = list(request.existing_stems)
            if request.revision:
                # The version being rewritten is not a question to keep away from
                previous = normalise(request.revision.previous.stem)
                seen = [stem for stem in seen if normalise(stem) != previous]
            outcomes = []
            for context in contexts:
                question_request = request.model_copy(update={"chunks": context})
                outcome = generate_verified_question(question_request, provider, max_retries, seen)
                if outcome.question:
                    seen.append(outcome.question.stem)
                outcomes.append(outcome)
        except Exception as error:  # provider outages must fail the job, not the worker
            jobs.set_failed(job_id, f"{type(error).__name__}: {error}")
            return
        # A fallback model that stepped in is recorded with the job, so the questions say who wrote them
        used = getattr(provider, "used", [])
        jobs.set_done(job_id, outcomes, model_id=", ".join(used) if used and used != [provider.model_id] else None)

    @app.get("/health")
    def health() -> dict:
        return {"status": "ok", "provider": llm_settings.get().primary.provider}

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

    @app.get("/materials/{material_id}/chunks")
    def get_chunks(material_id: str) -> list[MaterialChunk]:
        if materials.get(material_id) is None:
            raise HTTPException(status_code=404, detail="material not found")
        return materials.chunks(material_id)

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
        try:
            available = [*request.chunks, *materials.chunks_by_ids(request.course_id, request.chunk_ids)]
        except LookupError as error:
            raise HTTPException(status_code=422, detail=str(error)) from error
        if not any(chunk.text.strip() for chunk in available):
            raise HTTPException(status_code=422, detail="the topic has no text to write questions from")
        # With a focus the most relevant pieces come first; otherwise they stay in reading order
        ranked = False
        if request.focus:
            relevant = retriever.top_k(f"{request.topic} {request.focus}", available, len(available))
            if relevant:
                chosen = {chunk.id for chunk in relevant}
                available = [*relevant, *(chunk for chunk in available if chunk.id not in chosen)]
                ranked = True
        llm = llm_settings.get()
        contexts = plan_contexts(available, request.count, llm.context_chars, ranked)

        provider, model_id = provider_for(request.model, llm, log_prompts=True)
        job = Job(
            id=uuid.uuid4().hex,
            prompt_version=PROMPT_VERSION,
            model_id=model_id,
            grounding_mode=request.grounding_mode,
        )
        with admission:  # check and register together, so two requests cannot both take the last place
            queued_jobs, queued_questions = jobs.pending()
            if queued_jobs >= settings.max_queued_jobs or queued_questions + request.count > settings.max_queued_questions:
                raise HTTPException(
                    status_code=429,
                    detail="The question generation queue is full. Try again in a few minutes.",
                    headers={"Retry-After": "120"},
                )
            quota = settings.daily_questions_per_course
            if quota > 0:
                used = jobs.requested_today(request.course_id)
                if used + request.count > quota:
                    raise HTTPException(
                        status_code=429,
                        detail=(
                            f"This course has already requested {used} of its {quota} questions for today. "
                            "The count starts again at midnight UTC."
                        ),
                        headers={"Retry-After": "3600"},
                    )
            jobs.add(job, request.course_id, request.count)
        background.add_task(run_job, job.id, request, provider, contexts, llm.max_retries)
        return job

    @app.post("/discussion/suggest")
    def suggest_discussion_reply(request: DiscussionSuggestRequest) -> DiscussionSuggestion:
        llm = llm_settings.get()
        if not llm.discussion_suggestions:
            raise HTTPException(status_code=403, detail="reply suggestions are turned off in this service")

        chunks = []
        if request.chunk_ids:
            try:
                available = materials.chunks_by_ids(request.course_id, request.chunk_ids)
            except LookupError as error:
                raise HTTPException(status_code=422, detail=str(error)) from error
            chunks = retriever.top_k(f"{request.question_stem} {request.student_message}", available, request.top_k)

        provider, _ = provider_for(request.model, llm, interactive=True)
        try:
            reply = suggest_reply(request, chunks, provider)
        except RateLimited as error:
            raise HTTPException(
                status_code=429, detail="The model is busy right now. Try again in a minute.", headers={"Retry-After": "60"}
            ) from error
        except ModelReplyError as error:
            raise HTTPException(status_code=502, detail=f"the model gave no usable reply ({error})") from error
        except Exception as error:  # a provider outage must not look like a bug here
            # what the student wrote must not end up in the logs through the message
            raise HTTPException(status_code=502, detail=f"the model did not answer ({type(error).__name__})") from None
        return DiscussionSuggestion(reply=reply, sources=list(dict.fromkeys(c.source for c in chunks if c.source)))

    @app.get("/settings/llm")
    def get_llm_settings() -> LlmSettingsView:
        return llm_settings.view()

    @app.put("/settings/llm")
    def save_llm_settings(new_settings: LlmSettings) -> LlmSettingsView:
        missing = missing_keys(new_settings)
        if missing:
            raise HTTPException(
                status_code=422,
                detail=f"no API key for {', '.join(missing)} in the service's environment; set it in its .env first",
            )
        llm_settings.save(new_settings)
        return llm_settings.view()

    @app.post("/settings/llm/test")
    def test_model(choice: ModelChoice) -> ModelTestResult:
        """One tiny call to the model, to see that the name is right and it answers in time."""
        llm = llm_settings.get()
        if choice.provider in missing_keys(llm.model_copy(update={"primary": choice, "fallbacks": []})):
            return ModelTestResult(ok=False, seconds=0, error=f"no API key for {choice.provider} in the service's environment")
        config = model_config_for(choice, llm).model_copy(update={"timeout": min(llm.timeout, 60)})
        started = time.perf_counter()
        try:
            reply = provider_factory(config).complete("Reply with a JSON object and nothing else.", 'Return {"ok": true}')
        except Exception as error:  # the error is the answer to the test
            return ModelTestResult(ok=False, seconds=round(time.perf_counter() - started, 2), error=_short_error(error))
        return ModelTestResult(ok=True, seconds=round(time.perf_counter() - started, 2), reply=reply[:200])

    @app.get("/settings/llm/models")
    def provider_models(provider: Provider, refresh: bool = False) -> ProviderModels:
        """The models of a provider that can write text; a list that cannot be read says why."""
        try:
            models = catalog.models(provider, llm_settings.get().ollama_base_url, refresh)
        except Exception as error:  # an unreachable provider or a missing key are normal answers here
            return ProviderModels(provider=provider, error=_short_error(error))
        return ProviderModels(provider=provider, models=models)

    @app.get("/settings/llm/ollama")
    def ollama_models() -> OllamaModels:
        try:
            models = ollama.list_models(llm_settings.get().ollama_base_url)
        except Exception as error:  # an Ollama that is not running is a normal state here
            return OllamaModels(pulls=dict(ollama_pulls), error=_short_error(error))
        return OllamaModels(models=models, pulls=dict(ollama_pulls))

    @app.post("/settings/llm/ollama/pull", status_code=202)
    def pull_ollama_model(choice: ModelChoice, background: BackgroundTasks) -> OllamaModels:
        if choice.provider != "ollama":
            raise HTTPException(status_code=422, detail="only Ollama models are downloaded by the service")
        if ollama_pulls.get(choice.model) == "downloading":
            raise HTTPException(status_code=409, detail=f"{choice.model} is already being downloaded")
        base_url = llm_settings.get().ollama_base_url

        def pull() -> None:
            try:
                ollama.pull(base_url, choice.model)
                ollama_pulls[choice.model] = "done"
            except Exception as error:
                ollama_pulls[choice.model] = _short_error(error)

        ollama_pulls[choice.model] = "downloading"
        background.add_task(pull)
        return OllamaModels(pulls=dict(ollama_pulls))

    @app.get("/jobs/{job_id}")
    def get_job(job_id: str) -> Job:
        job = jobs.get(job_id)
        if job is None:
            raise HTTPException(status_code=404, detail="job not found")
        return job

    return app


def _short_error(error: Exception) -> str:
    return f"{type(error).__name__}: {str(error)[:300]}"
