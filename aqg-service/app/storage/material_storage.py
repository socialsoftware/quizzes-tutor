from pathlib import Path
from typing import Protocol

# Where versions before the Markdown moved to the database kept it, next to the original
LEGACY_MARKDOWN = "parsed.md"


class MaterialStorage(Protocol):
    def save(self, course_id: int, material_id: str, filename: str, content: bytes) -> Path: ...

    def original(self, course_id: int, material_id: str) -> Path | None: ...

    def legacy_markdown(self, course_id: int, material_id: str) -> str | None: ...


class LocalMaterialStorage:
    """Original uploads on a local volume: <root>/<course_id>/<material_id>/<filename>. Only the
    binary originals live here; the parsed text is in the database (MaterialRow.markdown)."""

    def __init__(self, root: Path):
        self.root = root

    def _directory(self, course_id: int, material_id: str) -> Path:
        return self.root / str(course_id) / material_id

    def save(self, course_id: int, material_id: str, filename: str, content: bytes) -> Path:
        directory = self._directory(course_id, material_id)
        directory.mkdir(parents=True, exist_ok=True)
        # Only the last path component is kept so an upload cannot write outside the directory
        target = directory / Path(filename.replace("\\", "/")).name
        target.write_bytes(content)
        return target

    def original(self, course_id: int, material_id: str) -> Path | None:
        directory = self._directory(course_id, material_id)
        if not directory.is_dir():
            return None
        files = [path for path in directory.iterdir() if path.is_file() and path.name != LEGACY_MARKDOWN]
        return files[0] if files else None

    def legacy_markdown(self, course_id: int, material_id: str) -> str | None:
        path = self._directory(course_id, material_id) / LEGACY_MARKDOWN
        return path.read_text(encoding="utf-8") if path.is_file() else None
