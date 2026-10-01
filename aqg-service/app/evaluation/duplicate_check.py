import re
import unicodedata

# Near-identical stems count as duplicates: same words in a slightly different order or
# with a word or two changed. Lexical only (regex tokens), no embeddings yet.
SIMILARITY_THRESHOLD = 0.85

_NON_WORD = re.compile(r"[^\w\s]")
_SPACES = re.compile(r"\s+")
_TOKEN = re.compile(r"\w+")


def normalise(text: str) -> str:
    """Lower case, no accents, no punctuation, single spaces."""
    without_accents = "".join(
        char for char in unicodedata.normalize("NFKD", text) if not unicodedata.combining(char)
    )
    return _SPACES.sub(" ", _NON_WORD.sub(" ", without_accents.lower())).strip()


def _tokens(text: str) -> set[str]:
    return set(_TOKEN.findall(normalise(text)))


def similarity(first: str, second: str) -> float:
    a, b = _tokens(first), _tokens(second)
    if not a or not b:
        return 0.0
    return len(a & b) / len(a | b)


def find_duplicate(stem: str, others: list[str]) -> str | None:
    """The first of `others` that `stem` duplicates, if any."""
    target = normalise(stem)
    for other in others:
        if normalise(other) == target or similarity(stem, other) >= SIMILARITY_THRESHOLD:
            return other
    return None
