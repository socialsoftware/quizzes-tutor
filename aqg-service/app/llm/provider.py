import logging
import time
from typing import Callable, Protocol

from app.schema import ModelConfig


class LLMProvider(Protocol):
    model_id: str

    def complete(self, system: str, user: str) -> str: ...


def precise(provider: "LLMProvider") -> "LLMProvider":
    """The same model asked with temperature 0, for the checks: classifying a draft should give the
    same answer every time, unlike writing it. Providers that cannot (test doubles) stay as they are."""
    make = getattr(provider, "with_temperature", None)
    return make(0.0) if make else provider


class LiteLLMProvider:
    """One code path for cloud APIs and local servers: LiteLLM routes on the model prefix
    (ollama_chat/, nvidia_nim/, anthropic/, plain OpenAI names)."""

    def __init__(self, config: ModelConfig):
        self.config = config
        self.model_id = config.model

    def with_temperature(self, temperature: float) -> "LiteLLMProvider":
        return LiteLLMProvider(self.config.model_copy(update={"temperature": temperature}))

    def complete(self, system: str, user: str) -> str:
        import litellm

        response = litellm.completion(
            model=self.config.model,
            api_base=self.config.api_base,
            api_key=self.config.api_key,
            temperature=self.config.temperature,
            timeout=self.config.timeout,
            extra_body=self.config.extra_body,
            **(self.config.options or {}),
            messages=[
                {"role": "system", "content": system},
                {"role": "user", "content": user},
            ],
            response_format={"type": "json_object"},
        )
        return response.choices[0].message.content


logger = logging.getLogger(__name__)


prompt_logger = logging.getLogger("aqg.prompts")


class LoggingProvider:
    """Debugging aid (LOG_PROMPTS=true): writes to the log exactly what is sent to the model and
    what comes back, then passes the call on. Each model of a fall-back chain is wrapped on its own,
    so the log also says which model was asked."""

    def __init__(self, provider: LLMProvider):
        self.provider = provider
        self.model_id = provider.model_id

    def with_temperature(self, temperature: float) -> "LoggingProvider":
        return LoggingProvider(precise(self.provider) if temperature == 0 else self.provider)

    def complete(self, system: str, user: str) -> str:
        prompt_logger.info("PROMPT to %s\n--- system ---\n%s\n--- user ---\n%s", self.model_id, system, user)
        started = time.monotonic()
        try:
            reply = self.provider.complete(system, user)
        except Exception as error:
            prompt_logger.info("FAILED %s after %.1fs: %s", self.model_id, time.monotonic() - started, type(error).__name__)
            raise
        prompt_logger.info("REPLY from %s in %.1fs (%d characters)\n%s", self.model_id, time.monotonic() - started, len(reply), reply)
        return reply


class FallbackProvider:
    """Asks each model in turn until one answers: an outage, an overloaded or withdrawn model or
    a timeout moves on to the next one instead of failing the job. `used` lists the models that
    answered, so a job records which ones wrote it."""

    def __init__(self, providers: list[LLMProvider]):
        if not providers:
            raise ValueError("at least one model is needed")
        self.providers = providers
        self.model_id = providers[0].model_id
        self.used: list[str] = []

    def with_temperature(self, temperature: float) -> "FallbackProvider":
        """Same chain at another temperature; the models that answer are still recorded in `used`."""
        other = FallbackProvider([precise(p) if temperature == 0 else p for p in self.providers])
        other.used = self.used
        return other

    def complete(self, system: str, user: str) -> str:
        failure: Exception | None = None
        for provider in self.providers:
            try:
                reply = provider.complete(system, user)
            except Exception as error:  # any provider failure is a reason to try the next model
                logger.warning("model %s failed (%s), trying the next one", provider.model_id, type(error).__name__)
                failure = error
                continue
            if provider.model_id not in self.used:
                self.used.append(provider.model_id)
            return reply
        raise failure


def build_provider(configs: list[ModelConfig], factory: Callable[[ModelConfig], LLMProvider]) -> LLMProvider:
    """One model as it is; several as a FallbackProvider in the given order."""
    if len(configs) == 1:
        return factory(configs[0])
    return FallbackProvider([factory(config) for config in configs])
