import threading
import time
from pathlib import Path

from app.chunking.splitter import split_markdown
from app.ingestion.adapters import Parsers, adapter_for
from app.schema import Chunk, Material, MaterialStatus
from app.storage.material_storage import MaterialStorage


class MaterialStore:
    """In-memory registry of materials and their chunks; a database replaces it later."""

    def __init__(self) -> None:
        self._materials: dict[str, Material] = {}
        self._chunks: dict[str, list[Chunk]] = {}
        self._lock = threading.Lock()

    def add(self, material: Material) -> None:
        with self._lock:
            self._materials[material.id] = material

    def get(self, material_id: str) -> Material | None:
        with self._lock:
            material = self._materials.get(material_id)
            return material.model_copy() if material else None

    def list_for_course(self, course_id: int) -> list[Material]:
        with self._lock:
            return [m.model_copy() for m in self._materials.values() if m.course_id == course_id]

    def set_ready(self, material_id: str, chunks: list[Chunk], parser: str, parse_seconds: float) -> None:
        with self._lock:
            self._chunks[material_id] = chunks
            material = self._materials[material_id]
            material.status = MaterialStatus.READY
            material.chunk_count = len(chunks)
            material.parser = parser
            material.parse_seconds = round(parse_seconds, 3)

    def set_failed(self, material_id: str, error: str) -> None:
        with self._lock:
            material = self._materials[material_id]
            material.status = MaterialStatus.FAILED
            material.error = error

    def chunks_for(self, course_id: int, material_ids: list[str]) -> list[Chunk]:
        """Chunks of the requested materials; ids of other courses or unfinished uploads are
        rejected instead of silently ignored."""
        chunks: list[Chunk] = []
        with self._lock:
            for material_id in material_ids:
                material = self._materials.get(material_id)
                if material is None or material.course_id != course_id:
                    raise LookupError(f"material {material_id} does not exist in course {course_id}")
                if material.status is not MaterialStatus.READY:
                    raise LookupError(f"material {material_id} is {material.status.value}")
                chunks.extend(self._chunks[material_id])
        return chunks


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
