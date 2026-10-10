import logging
from typing import Callable, Protocol

from app.schema import ModelConfig


class LLMProvider(Protocol):
    model_id: str

    def complete(self, system: str, user: str) -> str: ...


class LiteLLMProvider:
    """One code path for cloud APIs and local servers: LiteLLM routes on the model prefix
    (ollama_chat/, nvidia_nim/, anthropic/, plain OpenAI names)."""

    def __init__(self, config: ModelConfig):
        self.config = config
        self.model_id = config.model

    def complete(self, system: str, user: str) -> str:
        import litellm

        response = litellm.completion(
            model=self.config.model,
            api_base=self.config.api_base,
            api_key=self.config.api_key,
            temperature=self.config.temperature,
            timeout=self.config.timeout,
            extra_body=self.config.extra_body,
            messages=[
                {"role": "system", "content": system},
                {"role": "user", "content": user},
            ],
            response_format={"type": "json_object"},
        )
        return response.choices[0].message.content


logger = logging.getLogger(__name__)


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
