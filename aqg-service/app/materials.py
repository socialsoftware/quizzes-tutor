import time
from pathlib import Path

from sqlalchemy import delete, select
from sqlalchemy.orm import sessionmaker

from app.chunking.outline import clean_headings
from app.chunking.outline_edit import OutlineError, OutlineSection, chunks_from_sections, sections_from_chunks, to_markdown
from app.chunking.splitter import split_markdown
from app.db import ChunkRow, MaterialRow
from app.ingestion.adapters import Parsers, adapter_for
from app.schema import Chunk, Material, MaterialStatus, Section
from app.storage.material_storage import MaterialStorage


class MaterialStore:
    """Materials and their chunks, persisted in the aqg schema."""

    def __init__(self, session_factory: sessionmaker) -> None:
        self._session = session_factory

    def add(self, material: Material) -> None:
        with self._session.begin() as session:
            session.add(MaterialRow(**material.model_dump(mode="json")))

    def get(self, material_id: str) -> Material | None:
        with self._session() as session:
            row = session.get(MaterialRow, material_id)
            return _to_material(row) if row else None

    def list_for_course(self, course_id: int) -> list[Material]:
        with self._session() as session:
            rows = session.scalars(select(MaterialRow).where(MaterialRow.course_id == course_id).order_by(MaterialRow.id))
            return [_to_material(row) for row in rows]

    def set_processing(self, material_id: str) -> None:
        with self._session.begin() as session:
            row = session.get(MaterialRow, material_id)
            row.status = MaterialStatus.PROCESSING.value
            row.error = None

    def set_ready(
        self, material_id: str, chunks: list[Chunk], parser: str, parse_seconds: float, markdown: str
    ) -> None:
        with self._session.begin() as session:
            # A reprocessed material replaces its previous chunks
            session.execute(delete(ChunkRow).where(ChunkRow.material_id == material_id))
            session.add_all(
                ChunkRow(id=chunk.id, material_id=material_id, position=position, text=chunk.text, source=chunk.source)
                for position, chunk in enumerate(chunks)
            )
            row = session.get(MaterialRow, material_id)
            row.status = MaterialStatus.READY.value
            row.chunk_count = len(chunks)
            row.parser = parser
            row.parse_seconds = round(parse_seconds, 3)
            row.markdown = markdown
            row.error = None

    def markdown(self, material_id: str) -> str | None:
        with self._session() as session:
            row = session.get(MaterialRow, material_id)
            return row.markdown if row else None

    def sections(self, material_id: str) -> list[Section]:
        """Heading paths in reading order, with how many chunks each one holds."""
        sections: dict[str, int] = {}
        with self._session() as session:
            rows = session.scalars(
                select(ChunkRow).where(ChunkRow.material_id == material_id).order_by(ChunkRow.position)
            )
            for row in rows:
                if row.source:
                    sections[row.source] = sections.get(row.source, 0) + 1
        return [
            Section(path=path, title=path.split(SECTION_SEPARATOR)[-1], chunk_count=count)
            for path, count in sections.items()
        ]

    def outline_sections(self, material_id: str) -> list[OutlineSection]:
        """The document as sections of paragraphs, rebuilt from its chunks."""
        with self._session() as session:
            rows = session.scalars(
                select(ChunkRow).where(ChunkRow.material_id == material_id).order_by(ChunkRow.position)
            )
            return sections_from_chunks([Chunk(id=r.id, text=r.text, source=r.source) for r in rows])

    def save_sections(self, material_id: str, sections: list[OutlineSection]) -> None:
        """Cuts edited sections into chunks again, replacing the old ones. The text itself is not
        read from the original file again, so the edit survives; reading it again discards it."""
        chunks = chunks_from_sections(sections, material_id)
        if not chunks:
            raise OutlineError("the document would be left without text")
        with self._session.begin() as session:
            session.execute(delete(ChunkRow).where(ChunkRow.material_id == material_id))
            session.add_all(
                ChunkRow(id=chunk.id, material_id=material_id, position=position, text=chunk.text, source=chunk.source)
                for position, chunk in enumerate(chunks)
            )
            row = session.get(MaterialRow, material_id)
            row.chunk_count = len(chunks)
            row.markdown = to_markdown(sections)

    def set_failed(self, material_id: str, error: str) -> None:
        with self._session.begin() as session:
            row = session.get(MaterialRow, material_id)
            row.status = MaterialStatus.FAILED.value
            row.error = error

    def chunks_for(
        self,
        course_id: int,
        material_ids: list[str],
        sections: list[str] | None = None,
        material_sections: dict[str, list[str]] | None = None,
    ) -> list[Chunk]:
        """Chunks of the requested materials, optionally only those under the given heading
        paths. A material with an entry in `material_sections` is read at exactly those paths
        instead; ids of other courses or unfinished uploads are rejected instead of silently
        ignored."""
        chunks: list[Chunk] = []
        with self._session() as session:
            for material_id in material_ids:
                row = session.get(MaterialRow, material_id)
                if row is None or row.course_id != course_id:
                    raise LookupError(f"material {material_id} does not exist in course {course_id}")
                if row.status != MaterialStatus.READY.value:
                    raise LookupError(f"material {material_id} is {row.status}")
                rows = session.scalars(select(ChunkRow).where(ChunkRow.material_id == material_id).order_by(ChunkRow.position))
                chunks.extend(
                    Chunk(id=r.id, text=r.text, source=r.source) for r in rows if _wanted(r.source, material_id, sections, material_sections)
                )
        return chunks


SECTION_SEPARATOR = " > "


def _wanted(
    source: str | None, material_id: str, sections: list[str] | None, material_sections: dict[str, list[str]] | None
) -> bool:
    if material_sections and material_id in material_sections:
        return source in material_sections[material_id]
    return in_sections(source, sections)


def in_sections(source: str | None, sections: list[str] | None) -> bool:
    """A chunk belongs to a selected section when its heading path is that path or below it."""
    if not sections:
        return True
    if not source:
        return False
    return any(source == section or source.startswith(section + SECTION_SEPARATOR) for section in sections)


def _to_material(row: MaterialRow) -> Material:
    return Material(
        id=row.id,
        course_id=row.course_id,
        filename=row.filename,
        status=MaterialStatus(row.status),
        chunk_count=row.chunk_count,
        parser=row.parser,
        parse_seconds=row.parse_seconds,
        error=row.error,
    )


def process_material(
    store: MaterialStore,
    material_id: str,
    path: Path,
    parsers: Parsers,
    storage: MaterialStorage,
    keep_original: bool = True,
) -> None:
    try:
        started = time.perf_counter()
        document = adapter_for(path.name, parsers).extract(path)
        parse_seconds = time.perf_counter() - started
        _store_chunks(store, material_id, document.markdown, document.parser, parse_seconds, document.toc)
        if not keep_original:
            path.unlink(missing_ok=True)
    except Exception as error:  # any extraction failure must be visible on the material
        store.set_failed(material_id, f"{type(error).__name__}: {error}")


def reprocess_material(store: MaterialStore, material_id: str, parsers: Parsers, storage: MaterialStorage) -> None:
    """Rebuild a material's chunks with the current parsers and heading cleanup: from the
    kept original when there is one, otherwise from the Markdown saved earlier."""
    material = store.get(material_id)
    try:
        original = storage.original(material.course_id, material_id)
        if original is not None:
            process_material(store, material_id, original, parsers, storage, keep_original=True)
            return
        markdown = store.markdown(material_id) or storage.legacy_markdown(material.course_id, material_id)
        if markdown is None:
            raise ValueError("neither the original file nor its text was kept")
        _store_chunks(store, material_id, markdown, material.parser or "stored", material.parse_seconds or 0.0)
    except Exception as error:
        store.set_failed(material_id, f"{type(error).__name__}: {error}")


def _store_chunks(
    store: MaterialStore,
    material_id: str,
    markdown: str,
    parser: str,
    parse_seconds: float,
    toc: tuple[tuple[int, str], ...] = (),
) -> None:
    markdown = clean_headings(markdown, list(toc))
    chunks = split_markdown(markdown, material_id)
    if not chunks:
        raise ValueError("no text could be extracted from the file")
    store.set_ready(material_id, chunks, parser, parse_seconds, markdown)
