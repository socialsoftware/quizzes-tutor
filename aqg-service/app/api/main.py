import time
import uuid
from typing import Callable

from fastapi import BackgroundTasks, FastAPI, File, Form, HTTPException, UploadFile

from app.config import Settings
from app.db import create_session_factory
from app.evaluation.duplicate_check import normalise
from app.evaluation.pipeline import generate_verified_question
from app.generation.context_plan import plan_contexts
from app.generation.discussion import suggest_reply
from app.generation.prompts import PROMPT_VERSION
from app.generation.synthesizer import ModelReplyError
from app.ingestion.adapters import Parsers, UnsupportedFormat, adapter_for, build_parsers
from app.llm.ollama import OllamaClient
from app.llm.provider import LiteLLMProvider, LLMProvider, build_provider
from app.llm.settings import (
    LlmSettings,
    LlmSettingsStore,
    LlmSettingsView,
    ModelChoice,
    ModelTestResult,
    OllamaModels,
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


def create_app(
    settings: Settings | None = None,
    provider_factory: ProviderFactory = LiteLLMProvider,
    parsers: Parsers | None = None,
    retriever: Retriever | None = None,
    storage: MaterialStorage | None = None,
    ollama: OllamaClient | None = None,
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
    # Model -> state of its last download to the Ollama server ("downloading", "done" or the error)
    ollama_pulls: dict[str, str] = {}
    app = FastAPI(title="Quizzes Tutor AQG service")
    app.state.materials = materials
    app.state.llm_settings = llm_settings

    def provider_for(request_model: ModelConfig | None, llm: LlmSettings) -> tuple[LLMProvider, str]:
        """The model of the request if it names one, otherwise the settings' model and its fallbacks."""
        configs = [request_model] if request_model else llm.model_configs()
        return build_provider(configs, provider_factory), configs[0].model

    def run_job(
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

        provider, model_id = provider_for(request.model, llm)
        job = Job(
            id=uuid.uuid4().hex,
            prompt_version=PROMPT_VERSION,
            model_id=model_id,
            grounding_mode=request.grounding_mode,
        )
        jobs.add(job)
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

        provider, _ = provider_for(request.model, llm)
        try:
            reply = suggest_reply(request, chunks, provider)
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
