import re
import unicodedata
from collections import Counter

# PDF parsers turn every bold line into a heading, so the section paths of a textbook end up
# full of "Example", "Exercises" or "Proof" at the level of the chapters. This module keeps
# only the headings that structure the document, so the headings shown next to each piece of
# text read cleanly when the teacher puts the document under topics.

_HEADING = re.compile(r"^(#{1,6})\s+(.*\S)\s*$")
_FENCE = re.compile(r"^\s*```")
_EMPHASIS = re.compile(r"(\*\*|__|\*|_)(.+?)\1")

# A heading that starts with one of these labels a piece of a section, not a section
_GENERIC_STARTS = re.compile(
    r"^(?:examples?|exercises?|solutions?|proofs?|remarks?|notes?|answers?|problems?|figures?|tables?"
    r"|exemplos?|exerc[ií]cios?|solu[cç](?:ão|ões)|demonstra[cç](?:ão|ões)|observa[cç](?:ão|ões)|notas?"
    r"|respostas?|problemas?|figuras?|tabelas?"
    r"|ejemplos?|ejercicios?|soluci[oó]n(?:es)?|demostraci[oó]n(?:es)?|observaci[oó]n(?:es)?"
    r"|then|thus|hence|so|now|here|então|assim|logo|entonces|así)\b",
    re.IGNORECASE,
)

# Sentence pieces the parser printed in bold: they start in lower case or end on a word that
# needs a continuation ("which amounts to", "Observe that")
_ENDS_MID_SENTENCE = re.compile(
    r"\b(?:that|by|as|is|are|of|the|to|and|or|with|for|where|have|has|que|de|do|da|com|por|e|ou|el|la|los|las|y)$",
    re.IGNORECASE,
)

# Formulas (a = b, x < y, "(5)") are not section titles
_FORMULA = re.compile(r"[=<>≤≥{}]")

# A numbered exercise statement ("4. Prove the following relations")
_EXERCISE_ITEM = re.compile(
    r"^\d+\.\s+(?:prove|show|write|find|compute|solve|let|determine|give|express|verify|draw"
    r"|prove|mostre|escreva|calcule|resolva|determine|demuestre|escriba|calcule|resuelva)\b",
    re.IGNORECASE,
)

# A title has at least one word of 3+ letters; "R 3" or "V, VG2" are symbols
_REAL_WORD = re.compile(r"[^\W\d_]{3,}")
MIN_LETTER_RATIO = 0.5

# Headings repeated this often (e.g. "Exercises" after every section) are labels too
MAX_REPEATS = 3
# Longer than this is a sentence the parser printed in bold, not a title
MAX_TITLE_CHARS = 120


def plain_title(title: str) -> str:
    """The heading text without Markdown emphasis or surrounding punctuation."""
    previous = None
    while previous != title:
        previous, title = title, _EMPHASIS.sub(r"\2", title)
    return title.strip(" *_#:.").strip()


def _key(title: str) -> str:
    text = unicodedata.normalize("NFKD", plain_title(title).lower())
    text = "".join(char for char in text if not unicodedata.combining(char))
    return " ".join(re.findall(r"\w+", text))


def clean_headings(markdown: str, toc: list[tuple[int, str]] | None = None) -> str:
    """Rewrite the headings of `markdown`.

    With the document's own table of contents (PDF bookmarks), the headings found in it get
    its level and every other heading becomes plain bold text. Without one, generic labels,
    headings repeated more than MAX_REPEATS times and sentence-long headings become bold text.
    Either way the kept titles lose their emphasis markers, so section paths read cleanly.
    """
    lines = markdown.splitlines()
    toc_levels = {_key(title): level for level, title in toc or [] if _key(title)}

    counts: Counter[str] = Counter()
    in_fence = False
    for line in lines:
        if _FENCE.match(line):
            in_fence = not in_fence
            continue
        heading = None if in_fence else _HEADING.match(line)
        if heading:
            counts[_key(heading.group(2))] += 1

    def is_structural(title: str) -> bool:
        key = _key(title)
        if not key:
            return False
        if toc_levels:
            return key in toc_levels
        plain = plain_title(title)
        letters = sum(char.isalpha() for char in plain)
        return (
            not _GENERIC_STARTS.match(plain)
            and counts[key] <= MAX_REPEATS
            and len(plain) <= MAX_TITLE_CHARS
            and not plain[:1].islower()
            and not _ENDS_MID_SENTENCE.search(plain)
            and not _FORMULA.search(plain)
            and letters >= MIN_LETTER_RATIO * len(plain.replace(" ", ""))
            and _REAL_WORD.search(plain) is not None
            and not _EXERCISE_ITEM.match(plain)
        )

    result = []
    in_fence = False
    for line in lines:
        if _FENCE.match(line):
            in_fence = not in_fence
            result.append(line)
            continue
        heading = None if in_fence else _HEADING.match(line)
        if not heading:
            result.append(line)
            continue
        title = heading.group(2)
        if is_structural(title):
            level = toc_levels.get(_key(title), len(heading.group(1)))
            result.append(f"{'#' * min(level, 6)} {plain_title(title)}")
        else:
            result.append(f"**{plain_title(title)}**")
    return "\n".join(result)
