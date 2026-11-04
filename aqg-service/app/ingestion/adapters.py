import zipfile
from dataclasses import dataclass
from pathlib import Path
from typing import Protocol


class UnsupportedFormat(Exception):
    pass


class NoReadableText(Exception):
    """The file has no text to read: a scanned PDF is only images, and this service does no OCR."""


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


# A PDF with less text than this has none worth reading: its pages are images
MIN_PDF_CHARS = 50


class PymupdfAdapter:
    """PDFs that carry a text layer. CPU only, no models; a scanned PDF (images of pages) is refused."""

    def extract(self, path: Path) -> ParsedDocument:
        import pymupdf
        import pymupdf4llm

        with pymupdf.open(str(path)) as document:
            toc = tuple((level, title) for level, title, _page in document.get_toc(simple=True))
        markdown = pymupdf4llm.to_markdown(str(path))
        if len(markdown.strip()) < MIN_PDF_CHARS:
            raise NoReadableText(
                "the PDF has no text to read (is it a scan?). Scanned documents are not supported: "
                "use a PDF with selectable text"
            )
        return ParsedDocument(markdown, "pymupdf4llm", toc)


class MarkItDownAdapter:
    """PPTX and DOCX already hold their text, so it is read straight from the file."""

    def __init__(self) -> None:
        self._converter = None

    def extract(self, path: Path) -> ParsedDocument:
        if self._converter is None:
            from markitdown import MarkItDown

            self._converter = MarkItDown()
        return ParsedDocument(self._converter.convert(str(path)).text_content, "markitdown")


@dataclass
class Parsers:
    pdf: ContentAdapter
    office: ContentAdapter


def build_parsers() -> Parsers:
    return Parsers(pdf=PymupdfAdapter(), office=MarkItDownAdapter())


MARKDOWN_SUFFIXES = {".md", ".markdown", ".txt"}
PDF_SUFFIXES = {".pdf"}
OFFICE_SUFFIXES = {".pptx", ".docx"}


# A .pptx or .docx is a zip: a small upload can unpack to many gigabytes and take the service
# down while it is read. Real documents unpack to a few times their size (their images are
# already compressed), far below these limits.
MAX_UNPACKED_BYTES = 1024 * 1024 * 1024
MAX_ARCHIVE_ENTRIES = 20000


class UnsafeDocument(Exception):
    pass


def check_office_archive(path: Path) -> None:
    """Refuses an Office file that is not a zip or would unpack to too much, before any parser opens it."""
    try:
        with zipfile.ZipFile(path) as archive:
            entries = archive.infolist()
    except zipfile.BadZipFile as error:
        raise UnsafeDocument("the file is not a valid Office document") from error
    if len(entries) > MAX_ARCHIVE_ENTRIES:
        raise UnsafeDocument(f"the document holds more than {MAX_ARCHIVE_ENTRIES} parts")
    if sum(entry.file_size for entry in entries) > MAX_UNPACKED_BYTES:
        raise UnsafeDocument("the document unpacks to more than 1 GB")


def adapter_for(filename: str, parsers: Parsers) -> ContentAdapter:
    suffix = Path(filename).suffix.lower()
    if suffix in MARKDOWN_SUFFIXES:
        return MarkdownAdapter()
    if suffix in PDF_SUFFIXES:
        return parsers.pdf
    if suffix in OFFICE_SUFFIXES:
        return parsers.office
    raise UnsupportedFormat(f"unsupported file type {suffix or '(none)'}")
