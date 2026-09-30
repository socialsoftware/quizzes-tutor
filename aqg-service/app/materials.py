import threading
from pathlib import Path

from app.chunking.splitter import split_markdown
from app.ingestion.adapters import ContentAdapter, adapter_for
from app.schema import Chunk, Material, MaterialStatus


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

    def set_ready(self, material_id: str, chunks: list[Chunk]) -> None:
        with self._lock:
            self._chunks[material_id] = chunks
            material = self._materials[material_id]
            material.status = MaterialStatus.READY
            material.chunk_count = len(chunks)

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


def process_material(store: MaterialStore, material_id: str, path: Path, marker: ContentAdapter) -> None:
    try:
        markdown = adapter_for(path.name, marker).extract(path)
        chunks = split_markdown(markdown, material_id)
        if not chunks:
            raise ValueError("no text could be extracted from the file")
        store.set_ready(material_id, chunks)
    except Exception as error:  # any extraction failure must be visible on the material
        store.set_failed(material_id, f"{type(error).__name__}: {error}")
