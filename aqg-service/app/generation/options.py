import random

from app.schema import GeneratedMCQ


def shuffle_options(question: GeneratedMCQ, rng: random.Random | None = None) -> GeneratedMCQ:
    """The question with its options in a random order. Models put the correct option in a few
    favourite positions, so the order is decided here and not by the model. `rng` makes it
    repeatable (tests, offline evaluation)."""
    options = list(question.options)
    (rng or random).shuffle(options)
    return question.model_copy(update={"options": options})
