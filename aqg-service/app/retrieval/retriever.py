import math
import re
from collections import Counter
from typing import Protocol

from app.schema import Chunk


class Retriever(Protocol):
    def top_k(self, query: str, chunks: list[Chunk], k: int) -> list[Chunk]: ...


_TOKEN = re.compile(r"\w+", re.UNICODE)


def _tokens(text: str) -> list[str]:
    return [token.lower() for token in _TOKEN.findall(text) if len(token) > 2]


class LexicalRetriever:
    """BM25 over the chunks of the selected materials. It needs no model download, so the
    pipeline works end to end today; an embedding retriever (pgvector) can replace it behind
    the same interface."""

    def __init__(self, k1: float = 1.5, b: float = 0.75):
        self.k1 = k1
        self.b = b

    def top_k(self, query: str, chunks: list[Chunk], k: int) -> list[Chunk]:
        if not chunks:
            return []
        documents = [_tokens(f"{chunk.source or ''} {chunk.text}") for chunk in chunks]
        average_length = sum(map(len, documents)) / len(documents) or 1
        document_frequency: Counter[str] = Counter()
        for document in documents:
            document_frequency.update(set(document))

        query_terms = set(_tokens(query))
        scored: list[tuple[float, int]] = []
        for index, document in enumerate(documents):
            frequencies = Counter(document)
            score = 0.0
            for term in query_terms:
                if term not in frequencies:
                    continue
                idf = math.log(1 + (len(documents) - document_frequency[term] + 0.5) / (document_frequency[term] + 0.5))
                tf = frequencies[term]
                norm = tf + self.k1 * (1 - self.b + self.b * len(document) / average_length)
                score += idf * tf * (self.k1 + 1) / norm
            scored.append((score, index))

        scored.sort(key=lambda pair: (-pair[0], pair[1]))
        return [chunks[index] for score, index in scored[:k] if score > 0]
