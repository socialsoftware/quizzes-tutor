from dataclasses import dataclass
from pathlib import Path
from typing import Protocol


class UnsupportedFormat(Exception):
    pass


@dataclass(frozen=True)
class ParsedDocument:
    markdown: str
    parser: str
    # The document's own table of contents as (level, title), when it has one (PDF bookmarks)
    toc: tuple[tuple[int, str], ...] = ()


class ContentAdapter(Protocol):
    def extract(self, path: Path) -> ParsedDocument: ...


class MarkdownAdapter:
    def extract(self, path: Path) -> ParsedDocument:
        return ParsedDocument(path.read_text(encoding="utf-8"), "plain")


class PymupdfAdapter:
    """PDFs that carry a text layer. CPU only, no models."""

    def extract(self, path: Path) -> ParsedDocument:
        import pymupdf
        import pymupdf4llm

        with pymupdf.open(str(path)) as document:
            toc = tuple((level, title) for level, title, _page in document.get_toc(simple=True))
        return ParsedDocument(pymupdf4llm.to_markdown(str(path)), "pymupdf4llm", toc)


class MarkItDownAdapter:
    """PPTX and DOCX already hold their text, so it is read straight from the file."""

    def __init__(self) -> None:
        self._converter = None

    def extract(self, path: Path) -> ParsedDocument:
        if self._converter is None:
            from markitdown import MarkItDown

            self._converter = MarkItDown()
        return ParsedDocument(self._converter.convert(str(path)).text_content, "markitdown")


class MarkerAdapter:
    """Layout and OCR models: slow, but it reads scanned or complex PDFs. The converter loads
    several models, so it is built on first use and reused afterwards."""

    def __init__(self) -> None:
        self._converter = None

    def extract(self, path: Path) -> ParsedDocument:
        if self._converter is None:
            from marker.converters.pdf import PdfConverter
            from marker.models import create_model_dict

            self._converter = PdfConverter(artifact_dict=create_model_dict())
        from marker.output import text_from_rendered

        text, _, _ = text_from_rendered(self._converter(str(path)))
        return ParsedDocument(text, "marker")


class FallbackAdapter:
    """Use the fast parser, and fall back to the heavy one when it finds almost no text
    (a scanned PDF has no text layer for the fast parser to read)."""

    def __init__(self, primary: ContentAdapter, fallback: ContentAdapter, min_chars: int = 50):
        self.primary = primary
        self.fallback = fallback
        self.min_chars = min_chars

    def extract(self, path: Path) -> ParsedDocument:
        document = self.primary.extract(path)
        if len(document.markdown.strip()) >= self.min_chars:
            return document
        return self.fallback.extract(path)


@dataclass
class Parsers:
    pdf: ContentAdapter
    office: ContentAdapter


def build_parsers(pdf_parser: str = "pymupdf", office_parser: str = "markitdown") -> Parsers:
    marker = MarkerAdapter()
    pdf: ContentAdapter = marker if pdf_parser == "marker" else FallbackAdapter(PymupdfAdapter(), marker)
    office: ContentAdapter = marker if office_parser == "marker" else MarkItDownAdapter()
    return Parsers(pdf=pdf, office=office)


MARKDOWN_SUFFIXES = {".md", ".markdown", ".txt"}
PDF_SUFFIXES = {".pdf"}
OFFICE_SUFFIXES = {".pptx", ".docx"}


def adapter_for(filename: str, parsers: Parsers) -> ContentAdapter:
    suffix = Path(filename).suffix.lower()
    if suffix in MARKDOWN_SUFFIXES:
        return MarkdownAdapter()
    if suffix in PDF_SUFFIXES:
        return parsers.pdf
    if suffix in OFFICE_SUFFIXES:
        return parsers.office
    raise UnsupportedFormat(f"unsupported file type {suffix or '(none)'}")
