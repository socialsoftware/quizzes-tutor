from app.schema import GeneratedMCQ

OPTIONS_PER_QUESTION = 4


def validate_structure(question: GeneratedMCQ) -> list[str]:
    """Deterministic checks, no LLM involved. Returns one message per problem found."""
    problems: list[str] = []

    if not question.stem.strip():
        problems.append("the question stem is empty")

    if len(question.options) != OPTIONS_PER_QUESTION:
        problems.append(f"expected {OPTIONS_PER_QUESTION} options, got {len(question.options)}")

    if any(not option.content.strip() for option in question.options):
        problems.append("an option is empty")

    correct = sum(option.correct for option in question.options)
    if correct != 1:
        problems.append(f"expected exactly 1 correct option, got {correct}")

    normalised = [" ".join(option.content.lower().split()) for option in question.options]
    if len(set(normalised)) != len(normalised):
        problems.append("two options have the same text")

    return problems
