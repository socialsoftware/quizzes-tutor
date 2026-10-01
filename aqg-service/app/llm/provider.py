from typing import Protocol

from app.schema import ModelConfig


class LLMProvider(Protocol):
    model_id: str

    def complete(self, system: str, user: str) -> str: ...


class LiteLLMProvider:
    """One code path for cloud APIs and local servers: LiteLLM routes on the model prefix
    (ollama/, nvidia_nim/, anthropic/, plain OpenAI names)."""

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
