"""Limits that keep one user, one course or a bug from using up the model provider's quota.

Two kinds, both in memory and so per process (the service runs as one):

* `RateLimiter`: at most N calls to a provider in any minute. Writing a question takes 3 calls or
  more (the draft and two checks, and again for every rewrite), so a provider's requests-per-minute
  limit is reached long before its token limits. Callers wait for a free slot, up to a maximum.
* `RateLimitedProvider`: puts a limiter in front of a model. Local models (Ollama) have no limiter.

The per-course daily quota and the cap on queued work are counted from the jobs table
(`JobStore.requested_today` and `JobStore.pending`), so they survive a restart and need no cron job.
"""
import hmac
import threading
import time
from collections import deque
from typing import Callable

from app.llm.provider import LLMProvider, precise


class RateLimited(Exception):
    """The provider's per-minute budget is spent and the wait would be too long."""


class RateLimiter:
    WINDOW_SECONDS = 60.0

    def __init__(
        self, per_minute: int, clock: Callable[[], float] = time.monotonic, sleep: Callable[[float], None] = time.sleep
    ):
        if per_minute < 1:
            raise ValueError("a limiter needs at least one call per minute")
        self.per_minute = per_minute
        self._clock = clock
        self._sleep = sleep
        self._stamps: deque[float] = deque()
        self._lock = threading.Lock()

    def acquire(self, max_wait: float) -> bool:
        """Takes a slot, waiting for one if needed; False when it would take longer than `max_wait`."""
        deadline = self._clock() + max_wait
        while True:
            with self._lock:
                now = self._clock()
                while self._stamps and now - self._stamps[0] >= self.WINDOW_SECONDS:
                    self._stamps.popleft()
                if len(self._stamps) < self.per_minute:
                    self._stamps.append(now)
                    return True
                wait = self.WINDOW_SECONDS - (now - self._stamps[0])
            if self._clock() + wait > deadline:
                return False
            self._sleep(wait)


def provider_key(model: str) -> str | None:
    """Which provider's quota a LiteLLM model name draws on; None for local models, which have none."""
    if model.startswith("ollama"):
        return None
    return model.split("/")[0] if "/" in model else "openai"


class Limiters:
    """One limiter per cloud provider, shared by every request of the process."""

    def __init__(self, per_minute: int):
        self.per_minute = per_minute
        self._by_provider: dict[str, RateLimiter] = {}
        self._lock = threading.Lock()

    def for_model(self, model: str) -> RateLimiter | None:
        key = provider_key(model)
        if self.per_minute <= 0 or key is None:
            return None
        with self._lock:
            if key not in self._by_provider:
                self._by_provider[key] = RateLimiter(self.per_minute)
            return self._by_provider[key]


class RateLimitedProvider:
    """Every call waits for a slot of the provider's limiter (up to `max_wait` seconds)."""

    def __init__(self, provider: LLMProvider, limiter: RateLimiter, max_wait: float):
        self.provider = provider
        self.limiter = limiter
        self.max_wait = max_wait
        self.model_id = provider.model_id

    def with_temperature(self, temperature: float) -> "RateLimitedProvider":
        # the cold copy used by the checks draws on the same budget
        inner = precise(self.provider) if temperature == 0 else self.provider
        return RateLimitedProvider(inner, self.limiter, self.max_wait)

    def complete(self, system: str, user: str) -> str:
        if not self.limiter.acquire(self.max_wait):
            raise RateLimited(f"the model provider's limit of {self.limiter.per_minute} calls per minute is reached")
        return self.provider.complete(system, user)


def same_secret(given: str | None, expected: str) -> bool:
    """Compares a presented key with the configured one without leaking how much of it matched."""
    return hmac.compare_digest((given or "").encode(), expected.encode())
