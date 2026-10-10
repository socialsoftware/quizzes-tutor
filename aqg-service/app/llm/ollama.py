import httpx


class OllamaClient:
    """The few calls to an Ollama server the administrator needs: which models it has, and
    downloading another one."""

    def list_models(self, base_url: str) -> list[str]:
        response = httpx.get(f"{base_url}/api/tags", timeout=10)
        response.raise_for_status()
        return sorted(model["name"] for model in response.json().get("models", []))

    def pull(self, base_url: str, model: str) -> None:
        # A model is several GB: no timeout, the call returns when the download is over
        response = httpx.post(f"{base_url}/api/pull", json={"model": model, "stream": False}, timeout=None)
        response.raise_for_status()
        status = response.json().get("status")
        if status != "success":
            raise RuntimeError(status or "the download did not finish")
