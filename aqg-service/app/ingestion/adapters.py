from pathlib import Path
from typing import Protocol


class UnsupportedFormat(Exception):
    pass


class ContentAdapter(Protocol):
    def extract(self, path: Path) -> str:
        """Return the document as Markdown."""
        ...


class MarkdownAdapter:
    def extract(self, path: Path) -> str:
        return path.read_text(encoding="utf-8")


class MarkerAdapter:
    """PDF, PPTX and DOCX to Markdown through marker. The converter loads several
    models, so it is built on first use and reused afterwards."""

    def __init__(self) -> None:
        self._converter = None

    def extract(self, path: Path) -> str:
        if self._converter is None:
            from marker.converters.pdf import PdfConverter
            from marker.models import create_model_dict

            self._converter = PdfConverter(artifact_dict=create_model_dict())
        from marker.output import text_from_rendered

        text, _, _ = text_from_rendered(self._converter(str(path)))
        return text


MARKDOWN_SUFFIXES = {".md", ".markdown", ".txt"}
MARKER_SUFFIXES = {".pdf", ".pptx", ".docx"}


def adapter_for(filename: str, marker: ContentAdapter) -> ContentAdapter:
    suffix = Path(filename).suffix.lower()
    if suffix in MARKDOWN_SUFFIXES:
        return MarkdownAdapter()
    if suffix in MARKER_SUFFIXES:
        return marker
    raise UnsupportedFormat(f"unsupported file type {suffix or '(none)'}")
