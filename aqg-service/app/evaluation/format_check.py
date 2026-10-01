import re

from app.schema import GeneratedMCQ

# Regex checks of the question's form, no LLM involved. Patterns cover the course
# languages (English, Portuguese, Spanish).

_MIN_STEM_LENGTH = 10

# The stem must stand on its own: students never see the material it came from
_REFERS_TO_SOURCE = re.compile(
    r"\b(?:the|this|that)\s+(?:text|context|passage|excerpt|chunk|document)\b"
    r"|\b(?:o|este|esse|do|no|segundo\s+o)\s+(?:texto|contexto|excerto|documento)\b"
    r"|\b(?:el|este|del|en\s+el|según\s+el)\s+(?:texto|contexto|fragmento|documento)\b",
    re.IGNORECASE,
)

# Options that are judged against the others instead of standing alone
_CATCH_ALL_OPTION = re.compile(
    r"\b(?:all|none|both)\s+of\s+the\s+(?:above|options|answers)\b"
    r"|\b(?:todas|nenhuma|ambas)\s+(?:as|das)\s+(?:anteriores|opções|respostas)\b"
    r"|\b(?:todas|ninguna|ambas)\s+(?:las|de\s+las)\s+(?:anteriores|opciones|respuestas)\b",
    re.IGNORECASE,
)

# "A) ...", "b. ...", "3 - ...": the Tutor letters the options itself
_ENUMERATED_OPTION = re.compile(r"^\s*(?:[A-Da-d]|[1-4])\s*[).:\-]\s+")

# Bits of the prompt or of the output schema echoed back by the model
_PROMPT_LEFTOVER = re.compile(
    r"\[/?chunk\b|</?(?:context|task|teacher_review)>|\b(?:question|option)\s+text\b",
    re.IGNORECASE,
)


def check_format(question: GeneratedMCQ) -> list[str]:
    """Returns one message per problem found."""
    problems: list[str] = []
    stem = question.stem.strip()

    if len(stem) < _MIN_STEM_LENGTH or not re.search(r"\w{3,}", stem):
        problems.append("the question stem is too short to be a question")
    if _REFERS_TO_SOURCE.search(stem):
        problems.append("the stem refers to the text/context, which the students never see")

    texts = [stem, question.explanation, *(option.content for option in question.options)]
    if any(_PROMPT_LEFTOVER.search(text) for text in texts):
        problems.append("the reply contains prompt markup or schema placeholders")

    for index, option in enumerate(question.options):
        if _CATCH_ALL_OPTION.search(option.content):
            problems.append(f'option {index + 1} is an "all/none of the above" option')
        if _ENUMERATED_OPTION.match(option.content):
            problems.append(f"option {index + 1} starts with its own letter or number")

    return problems
