from pathlib import Path
from typing import Protocol


class MaterialStorage(Protocol):
    def save(self, course_id: int, material_id: str, filename: str, content: bytes) -> Path: ...

    def save_markdown(self, course_id: int, material_id: str, markdown: str) -> Path: ...


class LocalMaterialStorage:
    """Original uploads on a local volume: <root>/<course_id>/<material_id>/<filename>."""

    def __init__(self, root: Path):
        self.root = root

    def save(self, course_id: int, material_id: str, filename: str, content: bytes) -> Path:
        directory = self.root / str(course_id) / material_id
        directory.mkdir(parents=True, exist_ok=True)
        # Only the last path component is kept so an upload cannot write outside the directory
        target = directory / Path(filename.replace("\\", "/")).name
        target.write_bytes(content)
        return target

    def save_markdown(self, course_id: int, material_id: str, markdown: str) -> Path:
        """The parsed text is the source of truth: chunking can be redone from it without
        running the slow parsers again."""
        target = self.root / str(course_id) / material_id / "parsed.md"
        target.parent.mkdir(parents=True, exist_ok=True)
        target.write_text(markdown, encoding="utf-8")
        return target
