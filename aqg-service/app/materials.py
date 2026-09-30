import time
from pathlib import Path

from sqlalchemy import select
from sqlalchemy.orm import sessionmaker

from app.chunking.splitter import split_markdown
from app.db import ChunkRow, MaterialRow
from app.ingestion.adapters import Parsers, adapter_for
from app.schema import Chunk, Material, MaterialStatus
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

    def set_ready(self, material_id: str, chunks: list[Chunk], parser: str, parse_seconds: float) -> None:
        with self._session.begin() as session:
            session.add_all(
                ChunkRow(id=chunk.id, material_id=material_id, position=position, text=chunk.text, source=chunk.source)
                for position, chunk in enumerate(chunks)
            )
            row = session.get(MaterialRow, material_id)
            row.status = MaterialStatus.READY.value
            row.chunk_count = len(chunks)
            row.parser = parser
            row.parse_seconds = round(parse_seconds, 3)

    def set_failed(self, material_id: str, error: str) -> None:
        with self._session.begin() as session:
            row = session.get(MaterialRow, material_id)
            row.status = MaterialStatus.FAILED.value
            row.error = error

    def chunks_for(self, course_id: int, material_ids: list[str]) -> list[Chunk]:
        """Chunks of the requested materials; ids of other courses or unfinished uploads are
        rejected instead of silently ignored."""
        chunks: list[Chunk] = []
        with self._session() as session:
            for material_id in material_ids:
                row = session.get(MaterialRow, material_id)
                if row is None or row.course_id != course_id:
                    raise LookupError(f"material {material_id} does not exist in course {course_id}")
                if row.status != MaterialStatus.READY.value:
                    raise LookupError(f"material {material_id} is {row.status}")
                rows = session.scalars(select(ChunkRow).where(ChunkRow.material_id == material_id).order_by(ChunkRow.position))
                chunks.extend(Chunk(id=r.id, text=r.text, source=r.source) for r in rows)
        return chunks


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
        chunks = split_markdown(document.markdown, material_id)
        if not chunks:
            raise ValueError("no text could be extracted from the file")
        material = store.get(material_id)
        storage.save_markdown(material.course_id, material_id, document.markdown)
        store.set_ready(material_id, chunks, document.parser, parse_seconds)
        if not keep_original:
            path.unlink(missing_ok=True)
    except Exception as error:  # any extraction failure must be visible on the material
        store.set_failed(material_id, f"{type(error).__name__}: {error}")
