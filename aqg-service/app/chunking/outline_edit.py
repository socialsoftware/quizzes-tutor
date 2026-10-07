"""Editing the sections of a document by hand.

The sections come from the parser and the heading cleanup, and they are sometimes wrong: a
chapter split in two, a heading that is not one, a long section a teacher wants to cut in two
topics. The document is rebuilt from its chunks as a list of sections (a heading path and the
paragraphs under it), edited, and cut into chunks again; the parser does not run a second time.

Every operation returns the new sections and a `path_map` (old section path -> new path or paths),
so whatever pointed at a section by its path can follow it.
"""
import re
from dataclasses import dataclass, field

from app.chunking.splitter import split_markdown
from app.schema import Chunk

SEPARATOR = " > "
MAX_DEPTH = 6  # Markdown has no headings below level 6
PREVIEW_CHARS = 160

_FENCE = re.compile(r"^\s*```")
_LEADING_HASHES = re.compile(r"^#+\s*")


class OutlineError(ValueError):
    """The edit cannot be done; the message says why, for the teacher."""


@dataclass
class OutlineSection:
    parts: tuple[str, ...]  # the heading path; () for text before the first heading
    paragraphs: list[str] = field(default_factory=list)

    @property
    def path(self) -> str:
        return SEPARATOR.join(self.parts)


@dataclass
class OutlineEntry:
    path: str
    title: str
    depth: int
    has_text: bool
    paragraph_count: int
    preview: str


def split_paragraphs(text: str) -> list[str]:
    """Paragraphs are separated by blank lines, except inside a fenced code block."""
    paragraphs: list[str] = []
    current: list[str] = []
    in_fence = False
    for line in text.splitlines():
        if _FENCE.match(line):
            in_fence = not in_fence
        if line.strip() == "" and not in_fence:
            if current:
                paragraphs.append("\n".join(current))
                current = []
        else:
            current.append(line)
    if current:
        paragraphs.append("\n".join(current))
    return paragraphs


def sections_from_chunks(chunks: list[Chunk]) -> list[OutlineSection]:
    sections: list[OutlineSection] = []
    for chunk in chunks:
        parts = tuple(chunk.source.split(SEPARATOR)) if chunk.source else ()
        if sections and sections[-1].parts == parts:
            sections[-1].paragraphs.extend(split_paragraphs(chunk.text))
        else:
            sections.append(OutlineSection(parts, split_paragraphs(chunk.text)))
    return sections


def to_markdown(sections: list[OutlineSection]) -> str:
    lines: list[str] = []
    open_parts: tuple[str, ...] = ()
    for section in sections:
        parts = section.parts
        if len(parts) > MAX_DEPTH:
            raise OutlineError(f"sections can only be nested {MAX_DEPTH} levels deep")
        if parts and parts != open_parts:
            common = 0
            while common < len(open_parts) and common < len(parts) and open_parts[common] == parts[common]:
                common += 1
            # Coming back up to a section that is already open still needs its own heading,
            # or its text would be read as part of the section below
            for depth in range(min(common, len(parts) - 1), len(parts)):
                lines.append(f"{'#' * (depth + 1)} {parts[depth]}")
            open_parts = parts
        if section.paragraphs:
            lines.append("\n\n".join(section.paragraphs))
    return "\n\n".join(lines)


def chunks_from_sections(sections: list[OutlineSection], material_id: str) -> list[Chunk]:
    return split_markdown(to_markdown(sections), material_id)


def outline_entries(sections: list[OutlineSection]) -> list[OutlineEntry]:
    """Every heading in reading order, including those with no text of their own."""
    # A heading can come up more than once (after moving sections around): its text is all of it
    own: dict[tuple[str, ...], list[str]] = {}
    for section in sections:
        if section.parts:
            own.setdefault(section.parts, []).extend(section.paragraphs)

    seen: set[tuple[str, ...]] = set()
    entries: list[OutlineEntry] = []
    for section in sections:
        for depth in range(1, len(section.parts) + 1):
            parts = section.parts[:depth]
            if parts in seen:
                continue
            seen.add(parts)
            paragraphs = own.get(parts, [])
            entries.append(
                OutlineEntry(
                    path=SEPARATOR.join(parts),
                    title=parts[-1],
                    depth=depth,
                    has_text=bool(paragraphs),
                    paragraph_count=len(paragraphs),
                    preview=(paragraphs[0][:PREVIEW_CHARS] if paragraphs else ""),
                )
            )
    return entries


def apply(
    sections: list[OutlineSection],
    op: str,
    path: str,
    title: str | None = None,
    delta: int | None = None,
    paragraph: int | None = None,
) -> tuple[list[OutlineSection], dict[str, list[str]]]:
    if op == "rename":
        return rename(sections, path, title or "")
    if op == "merge":
        return merge_into_previous(sections, path)
    if op == "shift":
        return shift_level(sections, path, delta or 0)
    if op == "split":
        if paragraph is None:
            raise OutlineError("choose the paragraph where the new section starts")
        return split(sections, path, paragraph, title or "")
    raise OutlineError(f"unknown edit {op!r}")


def paragraphs_of(sections: list[OutlineSection], path: str) -> list[str]:
    """The text of a section itself (not its subsections), by paragraph."""
    target = _target(path)
    return [paragraph for section in sections if target and section.parts == target for paragraph in section.paragraphs]


def _target(path: str) -> tuple[str, ...]:
    return tuple(path.split(SEPARATOR)) if path else ()


def _exists(sections: list[OutlineSection], target: tuple[str, ...]) -> bool:
    return bool(target) and any(section.parts[: len(target)] == target for section in sections)


def _clean_title(title: str | None) -> str:
    cleaned = _LEADING_HASHES.sub("", " ".join((title or "").split()))
    if cleaned == "":
        raise OutlineError("the section needs a name")
    if SEPARATOR in cleaned:
        raise OutlineError(f'the name cannot contain "{SEPARATOR.strip()}"')
    return cleaned


def _path_map(before: list[OutlineSection], after_parts: list[tuple[str, ...]]) -> dict[str, list[str]]:
    mapping: dict[str, list[str]] = {}
    for section, parts in zip(before, after_parts):
        if section.parts and section.parts != parts:
            mapping.setdefault(section.path, []).append(SEPARATOR.join(parts))
    return mapping


def _copy(sections: list[OutlineSection]) -> list[OutlineSection]:
    return [OutlineSection(section.parts, list(section.paragraphs)) for section in sections]


def rename(sections: list[OutlineSection], path: str, title: str) -> tuple[list[OutlineSection], dict[str, list[str]]]:
    target = _target(path)
    if not _exists(sections, target):
        raise OutlineError("that section no longer exists")
    title = _clean_title(title)

    renamed = target[:-1] + (title,)
    if renamed != target and _exists(sections, renamed):
        raise OutlineError("a section with that name already exists here")

    after = [
        renamed + section.parts[len(target):] if section.parts[: len(target)] == target else section.parts
        for section in sections
    ]
    result = _copy(sections)
    for section, parts in zip(result, after):
        section.parts = parts
    return result, _path_map(sections, after)


def merge_into_previous(sections: list[OutlineSection], path: str) -> tuple[list[OutlineSection], dict[str, list[str]]]:
    """Removes a heading: its text joins the section just before it, and its subsections move up one level."""
    target = _target(path)
    if not _exists(sections, target):
        raise OutlineError("that section no longer exists")
    first = next(i for i, section in enumerate(sections) if section.parts[: len(target)] == target)
    if first == 0:
        raise OutlineError("there is no section before it to join")

    previous = sections[first - 1]
    after: list[tuple[str, ...]] = []
    result = _copy(sections)
    for index, section in enumerate(sections):
        if section.parts[: len(target)] != target:
            after.append(section.parts)
        elif section.parts == target:
            # the text of the removed heading becomes part of the section before it
            after.append(previous.parts)
        else:
            after.append(target[:-1] + section.parts[len(target):])
        result[index].parts = after[index]

    return _join_neighbours(result), _path_map(sections, after)


def _join_neighbours(sections: list[OutlineSection]) -> list[OutlineSection]:
    joined: list[OutlineSection] = []
    for section in sections:
        if joined and joined[-1].parts == section.parts:
            joined[-1].paragraphs.extend(section.paragraphs)
        else:
            joined.append(section)
    return joined


def shift_level(sections: list[OutlineSection], path: str, delta: int) -> tuple[list[OutlineSection], dict[str, list[str]]]:
    """Moves a section, with its subsections, one level up (-1) or down (+1)."""
    target = _target(path)
    if not _exists(sections, target):
        raise OutlineError("that section no longer exists")
    if delta not in (-1, 1):
        raise OutlineError("a section moves one level at a time")

    depth = len(target)
    if delta == -1:
        if depth < 2:
            raise OutlineError("it is already at the top level")
        moved = target[: depth - 2] + (target[-1],)
    else:
        first = next(i for i, section in enumerate(sections) if section.parts[:depth] == target)
        sibling = next(
            (
                section.parts[:depth]
                for section in reversed(sections[:first])
                if len(section.parts) >= depth and section.parts[: depth - 1] == target[:-1] and section.parts[:depth] != target
            ),
            None,
        )
        if sibling is None:
            raise OutlineError("there is no section above it to move it under")
        moved = sibling + (target[-1],)

    after = [moved + section.parts[depth:] if section.parts[:depth] == target else section.parts for section in sections]
    if any(len(parts) > MAX_DEPTH for parts in after):
        raise OutlineError(f"sections can only be nested {MAX_DEPTH} levels deep")
    result = _copy(sections)
    for section, parts in zip(result, after):
        section.parts = parts
    return _join_neighbours(result), _path_map(sections, after)


def split(sections: list[OutlineSection], path: str, paragraph: int, title: str) -> tuple[list[OutlineSection], dict[str, list[str]]]:
    """Cuts a section in two before a paragraph; the second half becomes a new section next to it."""
    target = _target(path)
    index = next((i for i, section in enumerate(sections) if section.parts == target and target), None)
    if index is None:
        raise OutlineError("that section has no text of its own to cut")
    own = sections[index]
    if not 1 <= paragraph < len(own.paragraphs):
        raise OutlineError("choose a paragraph after the first one to start the new section")

    title = _clean_title(title)
    new_parts = target[:-1] + (title,)
    if _exists(sections, new_parts):
        raise OutlineError("a section with that name already exists here")

    result = _copy(sections)
    result[index].paragraphs = own.paragraphs[:paragraph]
    result.insert(index + 1, OutlineSection(new_parts, own.paragraphs[paragraph:]))
    return result, {own.path: [own.path, SEPARATOR.join(new_parts)]}
