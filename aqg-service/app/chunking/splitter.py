import re

from app.schema import Chunk

# About 4 characters per token: 3200 is roughly 800 tokens
MAX_CHUNK_CHARS = 3200
MIN_CHUNK_CHARS = 200

_HEADING = re.compile(r"^(#{1,6})\s+(.*\S)\s*$")


def _sections(markdown: str) -> list[tuple[str, str]]:
    """Split on Markdown headings, keeping the heading path (e.g. "Networks > HTTP")."""
    sections: list[tuple[str, str]] = []
    path: list[tuple[int, str]] = []
    body: list[str] = []
    in_fence = False

    def flush() -> None:
        text = "\n".join(body).strip()
        if text:
            sections.append((" > ".join(title for _, title in path), text))
        body.clear()

    for line in markdown.splitlines():
        if line.lstrip().startswith("```"):
            in_fence = not in_fence
        heading = None if in_fence else _HEADING.match(line)
        if heading:
            flush()
            level = len(heading.group(1))
            while path and path[-1][0] >= level:
                path.pop()
            path.append((level, heading.group(2)))
        else:
            body.append(line)
    flush()
    return sections


def _pack(text: str, limit: int) -> list[str]:
    """Pack whole paragraphs up to the limit; a single oversized paragraph is cut by lines."""
    pieces: list[str] = []
    current = ""
    for paragraph in re.split(r"\n\s*\n", text):
        for part in _cut(paragraph, limit):
            if current and len(current) + len(part) + 2 > limit:
                pieces.append(current)
                current = part
            else:
                current = f"{current}\n\n{part}" if current else part
    if current:
        pieces.append(current)
    return pieces


def _cut(paragraph: str, limit: int) -> list[str]:
    if len(paragraph) <= limit:
        return [paragraph]
    parts: list[str] = []
    current = ""
    for line in paragraph.splitlines():
        while len(line) > limit:
            if current:
                parts.append(current)
                current = ""
            parts.append(line[:limit])
            line = line[limit:]
        if current and len(current) + len(line) + 1 > limit:
            parts.append(current)
            current = line
        else:
            current = f"{current}\n{line}" if current else line
    if current:
        parts.append(current)
    return parts


def split_markdown(
    markdown: str, material_id: str, max_chars: int = MAX_CHUNK_CHARS, min_chars: int = MIN_CHUNK_CHARS
) -> list[Chunk]:
    """Cut by section first so a concept is not split across chunks, then by size inside long
    sections. Tiny sections are merged into the previous chunk of the same document."""
    pieces: list[tuple[str, str]] = []
    for heading_path, text in _sections(markdown):
        for piece in _pack(text, max_chars):
            if pieces and len(piece) < min_chars and len(pieces[-1][1]) + len(piece) + 2 <= max_chars:
                previous_path, previous_text = pieces[-1]
                label = f"{heading_path}\n" if heading_path and heading_path != previous_path else ""
                pieces[-1] = (previous_path, f"{previous_text}\n\n{label}{piece}")
            else:
                pieces.append((heading_path, piece))

    return [
        Chunk(id=f"{material_id}:{index}", text=text, source=heading_path or None)
        for index, (heading_path, text) in enumerate(pieces)
    ]
