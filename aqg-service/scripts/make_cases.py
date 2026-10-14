"""Build evaluation cases from a document (PDF, PowerPoint, Word, Markdown).

    python scripts/make_cases.py book.pdf --cases 30 --chunks 3 --language Portuguese --out cases.json

The document is read and cut into pieces exactly as the service does it. Each case takes a few
consecutive pieces from evenly spaced places of the document, as the text of one topic; its topic
is the last heading of the first piece. Difficulties alternate between EASY, MEDIUM and HARD so
that every model is tried at each of them.
"""
import argparse
import json
import sys
from pathlib import Path

sys.path.insert(0, str(Path(__file__).resolve().parents[1]))

from app.chunking.outline import clean_headings  # noqa: E402
from app.chunking.splitter import split_markdown  # noqa: E402
from app.ingestion.adapters import adapter_for, build_parsers  # noqa: E402

MIN_CHUNK_CHARS = 400  # headings with a few lines make poor material for a question
DIFFICULTIES = ("EASY", "MEDIUM", "HARD")


def build_cases(markdown: str, count: int, chunks_per_case: int, language: str | None, grounding: str) -> list[dict]:
    chunks = [c for c in split_markdown(markdown, "doc") if len(c.text) >= MIN_CHUNK_CHARS]
    if not chunks:
        raise SystemExit("the document has no pieces long enough to make cases from")
    # starting points spread over the whole document, so the cases are not all from one chapter
    last_start = max(len(chunks) - chunks_per_case, 0)
    starts = sorted({round(i * last_start / max(count - 1, 1)) for i in range(count)})
    cases = []
    for index, start in enumerate(starts):
        window = chunks[start : start + chunks_per_case]
        topic = (window[0].source or "the document").split(" > ")[-1]
        cases.append({
            "id": f"case-{index + 1:02d}",
            "topic": topic,
            "difficulty": DIFFICULTIES[index % len(DIFFICULTIES)],
            "grounding_mode": grounding,
            "language": language,
            "chunks": [{"id": c.id, "text": c.text, "source": c.source} for c in window],
        })
    return cases


def main() -> None:
    parser = argparse.ArgumentParser(description=__doc__, formatter_class=argparse.RawDescriptionHelpFormatter)
    parser.add_argument("document", type=Path)
    parser.add_argument("--cases", type=int, default=30)
    parser.add_argument("--chunks", type=int, default=3, help="pieces of text per case")
    parser.add_argument("--language", default=None, help="language of the questions, e.g. Portuguese")
    parser.add_argument("--grounding", choices=("STRICT", "ENRICHED"), default="STRICT")
    parser.add_argument("--out", type=Path, default=Path("cases.json"))
    args = parser.parse_args()

    document = adapter_for(args.document.name, build_parsers()).extract(args.document)
    markdown = clean_headings(document.markdown, list(document.toc))
    cases = build_cases(markdown, args.cases, args.chunks, args.language, args.grounding)
    args.out.write_text(json.dumps(cases, ensure_ascii=False, indent=2), encoding="utf-8")
    print(f"{len(cases)} cases written to {args.out}")


if __name__ == "__main__":
    main()
