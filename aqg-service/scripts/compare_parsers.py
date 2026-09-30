"""Run every parser that can read a file and compare speed and output size.

    python scripts/compare_parsers.py lecture.pdf lecture.pptx [--save out_dir]

Each parser is timed on its own (marker includes its model loading on the first file).
With --save the Markdown of every parser is written next to the others, so the output
quality can be compared by eye.
"""
import argparse
import sys
import time
from pathlib import Path

sys.path.insert(0, str(Path(__file__).resolve().parents[1]))

from app.ingestion.adapters import MarkerAdapter, MarkItDownAdapter, PymupdfAdapter  # noqa: E402

PARSERS = {
    ".pdf": {"pymupdf4llm": PymupdfAdapter(), "marker": MarkerAdapter()},
    ".pptx": {"markitdown": MarkItDownAdapter(), "marker": MarkerAdapter()},
    ".docx": {"markitdown": MarkItDownAdapter(), "marker": MarkerAdapter()},
}


def main() -> None:
    argument_parser = argparse.ArgumentParser()
    argument_parser.add_argument("files", nargs="+", type=Path)
    argument_parser.add_argument("--save", type=Path)
    args = argument_parser.parse_args()

    print(f"{'file':40} {'parser':12} {'seconds':>8} {'chars':>9}")
    for file in args.files:
        for name, adapter in PARSERS.get(file.suffix.lower(), {}).items():
            started = time.perf_counter()
            try:
                markdown = adapter.extract(file).markdown
            except ImportError:
                print(f"{file.name:40} {name:12} {'not installed':>18}")
                continue
            except Exception as error:
                print(f"{file.name:40} {name:12} failed: {type(error).__name__}: {error}")
                continue
            seconds = time.perf_counter() - started
            print(f"{file.name:40} {name:12} {seconds:8.2f} {len(markdown):9}")
            if args.save:
                args.save.mkdir(parents=True, exist_ok=True)
                (args.save / f"{file.stem}.{name}.md").write_text(markdown, encoding="utf-8")


if __name__ == "__main__":
    main()
