"""Which pieces of a topic's text each question of a job is written from.

A topic (with its subtopics) can hold far more text than fits in one prompt. Instead of always
sending the same few pieces, which makes every question of a job about the same paragraph, the
text is cut into consecutive windows that fit and the questions take turns over them.
"""
from app.schema import Chunk


def plan_contexts(chunks: list[Chunk], count: int, budget_chars: int, ranked: bool = False) -> list[list[Chunk]]:
    """One context per question.

    When all the text fits in `budget_chars`, every question gets all of it. Otherwise, with
    `chunks` in reading order the questions are spread evenly over the whole topic; with
    `ranked` (most relevant first, e.g. for a focus) they take the best windows first. With
    more questions than windows, the windows are used again in turn.
    """
    if not chunks:
        return [[] for _ in range(count)]
    windows = _windows(chunks, budget_chars)
    if len(windows) == 1:
        return [windows[0] for _ in range(count)]
    if ranked or count >= len(windows):
        return [windows[index % len(windows)] for index in range(count)]
    # The middle of each of `count` equal stretches of the document
    return [windows[(2 * index + 1) * len(windows) // (2 * count)] for index in range(count)]


def _windows(chunks: list[Chunk], budget_chars: int) -> list[list[Chunk]]:
    windows: list[list[Chunk]] = []
    current: list[Chunk] = []
    size = 0
    for chunk in chunks:
        # A piece larger than the budget still goes alone rather than being dropped
        if current and size + len(chunk.text) > budget_chars:
            windows.append(current)
            current, size = [], 0
        current.append(chunk)
        size += len(chunk.text)
    if current:
        windows.append(current)
    return windows
