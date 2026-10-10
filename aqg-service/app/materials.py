import time
from pathlib import Path

from sqlalchemy import delete, select
from sqlalchemy.orm import sessionmaker

from app.chunking.outline import clean_headings
from app.chunking.splitter import split_markdown
from app.db import ChunkRow, MaterialRow
from app.ingestion.adapters import Parsers, adapter_for
from app.schema import Chunk, Material, MaterialChunk, MaterialStatus
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

    def chunks(self, material_id: str) -> list[MaterialChunk]:
        """The pieces of a material in reading order, to put them under topics."""
        with self._session() as session:
            rows = session.scalars(
                select(ChunkRow).where(ChunkRow.material_id == material_id).order_by(ChunkRow.position)
            )
            return [MaterialChunk(id=r.id, position=r.position, heading=r.source, text=r.text) for r in rows]

    def set_failed(self, material_id: str, error: str) -> None:
        with self._session.begin() as session:
            row = session.get(MaterialRow, material_id)
            row.status = MaterialStatus.FAILED.value
            row.error = error

    def chunks_by_ids(self, course_id: int, chunk_ids: list[str]) -> list[Chunk]:
        """The given chunks in reading order: documents in the order they first appear in
        `chunk_ids`, pieces in document order. Chunks of another course, of an unfinished upload
        or that no longer exist are refused instead of silently left out."""
        wanted = list(dict.fromkeys(chunk_ids))
        if not wanted:
            return []
        with self._session() as session:
            rows = {row.id: row for row in session.scalars(select(ChunkRow).where(ChunkRow.id.in_(wanted)))}
            missing = [chunk_id for chunk_id in wanted if chunk_id not in rows]
            if missing:
                raise LookupError(f"{len(missing)} piece(s) of text no longer exist (e.g. {missing[0]})")
            materials = {
                material_id: session.get(MaterialRow, material_id)
                for material_id in dict.fromkeys(rows[chunk_id].material_id for chunk_id in wanted)
            }
        for material_id, material in materials.items():
            if material is None or material.course_id != course_id:
                raise LookupError(f"material {material_id} does not exist in course {course_id}")
            if material.status != MaterialStatus.READY.value:
                raise LookupError(f"material {material_id} is {material.status}")

        order = {material_id: index for index, material_id in enumerate(materials)}
        ordered = sorted(rows.values(), key=lambda row: (order[row.material_id], row.position))
        return [Chunk(id=row.id, text=row.text, source=row.source) for row in ordered]


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
