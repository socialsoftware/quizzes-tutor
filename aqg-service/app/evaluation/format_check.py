import re

from app.schema import GeneratedMCQ

# Regex checks of the question's form, no LLM involved. Patterns cover the course
# languages (English, Portuguese, Spanish).

_MIN_STEM_LENGTH = 10

# A correct option much longer than the distractors gives itself away (Tian et al., 2026): it is
# flagged when it is this many times the average distractor and longer by at least this many characters
_LENGTH_RATIO = 1.6
_LENGTH_MARGIN = 20

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

# "A) ...", "b. ...", "C: ...", "A - text...", "3 - text...": the Tutor letters the options itself.
# A dash only counts when text follows (a word of 3 letters) and the marker is a capital or a digit:
# "a - b", "-a - b", "3 - 1" and "A - B" are answers of a maths question, not enumerations
_ENUMERATED_OPTION = re.compile(
    r"^\s*(?:(?:[A-Da-d]|[1-4])\s*[).:]\s+|(?:[A-D]|[1-4])\s+-\s+(?=.*[A-Za-z]{3}))"
)

# "Option A is wrong", "matches option 1", "the second option": the options are shuffled after the
# draft is written and shown without letters, so a position in the explanation points at another
# option or at none
_OPTION_BY_POSITION = re.compile(
    r"\b(?i:option|alternative|choice|opção|alternativa|opción)\s*\(?(?:[A-D]|[1-4])\b"
    r"|(?i:\b(?:first|second|third|fourth|last)\s+(?:option|alternative|choice)\b)"
    r"|(?i:\b(?:primeira|segunda|terceira|quarta|última)\s+(?:opção|alternativa)\b)"
    r"|(?i:\b(?:primera|segunda|tercera|cuarta|última)\s+(?:opción|alternativa)\b)"
)

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

    if _OPTION_BY_POSITION.search(question.explanation):
        problems.append(
            "the explanation names options by position or letter (\"option A\", \"the second option\"); "
            "the options are shown in another order and without letters, so name them by what they say"
        )

    texts = [stem, question.explanation, *(option.content for option in question.options)]
    if any(_PROMPT_LEFTOVER.search(text) for text in texts):
        problems.append("the reply contains prompt markup or schema placeholders")

    for index, option in enumerate(question.options):
        if _CATCH_ALL_OPTION.search(option.content):
            problems.append(f'option {index + 1} is an "all/none of the above" option')
        if _ENUMERATED_OPTION.match(option.content):
            problems.append(f"option {index + 1} starts with its own letter or number")

    problems.extend(_option_length_problems(question))
    return problems


def _option_length_problems(question: GeneratedMCQ) -> list[str]:
    correct = [option for option in question.options if option.correct]
    others = [len(option.content.strip()) for option in question.options if not option.correct]
    if len(correct) != 1 or len(others) < 2:
        return []  # a malformed draft is reported by the structure check
    length = len(correct[0].content.strip())
    average = sum(others) / len(others)
    if average > 0 and length >= _LENGTH_RATIO * average and length - average >= _LENGTH_MARGIN:
        return [
            f"the correct option ({length} characters) is much longer than the distractors "
            f"(about {average:.0f}): make the options similar in length so length does not give the answer away"
        ]
    return []
