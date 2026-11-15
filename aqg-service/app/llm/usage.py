"""What the calls to the models of one piece of work cost: how many, how many tokens and how long.

A piece of work (one question, with its retries and checks) is run inside `metering()`; every call to a
model made meanwhile in the same thread adds itself to it. Nothing is stored here: the totals go with the
outcome of the question, so that the cost per question can be studied afterwards.
"""
import contextlib
import contextvars
from dataclasses import dataclass


@dataclass
class Meter:
    calls: int = 0
    prompt_tokens: int = 0
    completion_tokens: int = 0
    seconds: float = 0.0

    def totals(self) -> dict:
        return {
            "calls": self.calls,
            "prompt_tokens": self.prompt_tokens,
            "completion_tokens": self.completion_tokens,
            "seconds": round(self.seconds, 2),
        }


_active: contextvars.ContextVar[Meter | None] = contextvars.ContextVar("usage_meter", default=None)


@contextlib.contextmanager
def metering():
    """Counts the calls made inside the block."""
    meter = Meter()
    token = _active.set(meter)
    try:
        yield meter
    finally:
        _active.reset(token)


def record(prompt_tokens: int | None, completion_tokens: int | None, seconds: float) -> None:
    """One call to a model, added to the work being metered (nothing happens outside `metering()`)."""
    meter = _active.get()
    if meter is not None:
        meter.calls += 1
        meter.prompt_tokens += prompt_tokens or 0
        meter.completion_tokens += completion_tokens or 0
        meter.seconds += seconds
