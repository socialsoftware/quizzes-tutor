import pytest
from fastapi.testclient import TestClient

from app.api.main import create_app
from app.chunking.outline_edit import (
    OutlineError,
    OutlineSection,
    apply,
    chunks_from_sections,
    merge_into_previous,
    outline_entries,
    paragraphs_of,
    rename,
    sections_from_chunks,
    shift_level,
    split,
    split_paragraphs,
    to_markdown,
)
from app.chunking.splitter import split_markdown
from app.config import Settings
from app.ingestion.adapters import ParsedDocument, Parsers
from tests.conftest import CLEAN_DISTRACTORS, FakeLLM, good_question
from tests.test_ingestion import upload, wait_for

DOC = """# Networks

Intro one.

Intro two.

## HTTP

HTTP request and response.

Status codes: 404 means not found.

Cookies keep state.

## DNS

DNS translates names.

### Records

A and AAAA records.

# Databases

The relational model.
"""


def sections(markdown: str = DOC) -> list[OutlineSection]:
    return sections_from_chunks(split_markdown(markdown, "m1", min_chars=0))


def paths(items: list[OutlineSection]) -> list[str]:
    return [section.path for section in items]


def test_a_document_is_rebuilt_from_its_chunks_as_sections_of_paragraphs():
    result = sections()

    assert paths(result) == ["Networks", "Networks > HTTP", "Networks > DNS", "Networks > DNS > Records", "Databases"]
    assert result[0].paragraphs == ["Intro one.", "Intro two."]
    assert len(result[1].paragraphs) == 3


def test_paragraphs_inside_a_code_block_are_not_cut():
    text = "Before.\n\n```\nline one\n\nline two\n```\n\nAfter."

    assert split_paragraphs(text) == ["Before.", "```\nline one\n\nline two\n```", "After."]


def test_cutting_the_sections_into_chunks_again_gives_the_same_chunks():
    original = split_markdown(DOC, "m1", min_chars=0)

    rebuilt = chunks_from_sections(sections_from_chunks(original), "m1")

    assert [(c.source, c.text) for c in rebuilt] == [(c.source, c.text) for c in original]


def test_the_outline_lists_every_heading_including_those_without_text():
    entries = outline_entries(sections("# Part\n\n## Chapter\n\nText of the chapter.\n\nMore text."))

    assert [(e.path, e.depth, e.has_text) for e in entries] == [("Part", 1, False), ("Part > Chapter", 2, True)]
    assert entries[1].paragraph_count == 2
    assert entries[1].preview == "Text of the chapter."


def test_reading_the_outline_leaves_the_sections_alone():
    original = sections()
    before = [(s.parts, list(s.paragraphs)) for s in original]

    outline_entries(original)

    assert [(s.parts, s.paragraphs) for s in original] == before


def test_rename_changes_the_section_and_everything_below_it():
    result, path_map = rename(sections(), "Networks > DNS", "Name service")

    assert paths(result) == [
        "Networks", "Networks > HTTP", "Networks > Name service", "Networks > Name service > Records", "Databases",
    ]
    assert path_map == {
        "Networks > DNS": ["Networks > Name service"],
        "Networks > DNS > Records": ["Networks > Name service > Records"],
    }


def test_rename_does_not_touch_the_sections_it_was_given():
    original = sections()

    rename(original, "Databases", "Data")

    assert paths(original)[-1] == "Databases"


@pytest.mark.parametrize(
    "path, title, message",
    [
        ("Nowhere", "x", "no longer exists"),
        ("Databases", "   ", "needs a name"),
        ("Databases", "a > b", "cannot contain"),
        ("Databases", "Networks", "already exists"),
        ("Networks > DNS", "HTTP", "already exists"),
    ],
)
def test_rename_refuses_what_would_break_the_paths(path, title, message):
    with pytest.raises(OutlineError, match=message):
        rename(sections(), path, title)


def test_rename_strips_leading_hashes_and_extra_spaces():
    result, _ = rename(sections(), "Databases", "  ##  The   data ")

    assert result[-1].parts == ("The data",)


def test_merge_joins_the_text_with_the_section_before_and_raises_its_subsections():
    result, path_map = merge_into_previous(sections(), "Networks > DNS")

    assert paths(result) == ["Networks", "Networks > HTTP", "Networks > Records", "Databases"]
    assert result[1].paragraphs[-1] == "DNS translates names."
    assert path_map == {
        "Networks > DNS": ["Networks > HTTP"],
        "Networks > DNS > Records": ["Networks > Records"],
    }


def test_merge_of_a_heading_without_text_only_raises_what_is_below_it():
    result, path_map = merge_into_previous(sections("# A\n\nText a.\n\n# Part\n\n## B\n\nText b."), "Part")

    assert paths(result) == ["A", "B"]
    assert path_map == {"Part > B": ["B"]}


def test_the_first_section_has_nothing_to_join():
    with pytest.raises(OutlineError, match="no section before"):
        merge_into_previous(sections(), "Networks")


def test_shift_up_makes_a_section_a_sibling_of_its_parent():
    result, path_map = shift_level(sections(), "Networks > DNS", -1)

    assert paths(result) == ["Networks", "Networks > HTTP", "DNS", "DNS > Records", "Databases"]
    assert path_map == {"Networks > DNS": ["DNS"], "Networks > DNS > Records": ["DNS > Records"]}


def test_shift_down_puts_a_section_under_the_one_above_it():
    result, path_map = shift_level(sections(), "Networks > DNS", 1)

    assert paths(result)[2:4] == ["Networks > HTTP > DNS", "Networks > HTTP > DNS > Records"]
    assert path_map["Networks > DNS"] == ["Networks > HTTP > DNS"]


def test_a_top_level_section_can_go_under_the_top_level_section_above():
    result, _ = shift_level(sections(), "Databases", 1)

    assert paths(result)[-1] == "Networks > Databases"


@pytest.mark.parametrize(
    "path, delta, message",
    [
        ("Networks", -1, "already at the top"),
        ("Networks", 1, "no section above"),
        ("Networks > HTTP", 1, "no section above"),
        ("Databases", 2, "one level at a time"),
        ("Nowhere", 1, "no longer exists"),
    ],
)
def test_shift_refuses_what_cannot_be_done(path, delta, message):
    with pytest.raises(OutlineError, match=message):
        shift_level(sections(), path, delta)


def test_shift_down_refuses_to_go_deeper_than_markdown_allows():
    deep = "# A\n\ntext a\n\n# G\n\n## H\n\n### I\n\n#### J\n\n##### K\n\n###### L\n\ntext l"
    document = sections(deep)

    with pytest.raises(OutlineError, match="nested 6 levels"):
        shift_level(document, "G", 1)


def test_split_cuts_a_section_in_two_before_a_paragraph():
    result, path_map = split(sections(), "Networks > HTTP", 2, "Cookies")

    assert paths(result) == [
        "Networks", "Networks > HTTP", "Networks > Cookies", "Networks > DNS", "Networks > DNS > Records", "Databases",
    ]
    assert result[1].paragraphs == ["HTTP request and response.", "Status codes: 404 means not found."]
    assert result[2].paragraphs == ["Cookies keep state."]
    assert path_map == {"Networks > HTTP": ["Networks > HTTP", "Networks > Cookies"]}


def test_a_split_survives_being_cut_into_chunks_again():
    result, _ = split(sections(), "Networks > HTTP", 2, "Cookies")

    chunks = chunks_from_sections(result, "m1")

    cookies = next(c for c in chunks if c.source == "Networks > Cookies")
    assert cookies.text == "Cookies keep state."
    dns = next(c for c in chunks if c.source == "Networks > DNS")
    assert "DNS translates names." in dns.text


@pytest.mark.parametrize(
    "path, paragraph, title, message",
    [
        ("Networks > HTTP", 0, "x", "after the first one"),
        ("Networks > HTTP", 3, "x", "after the first one"),
        ("Networks > HTTP", 1, "DNS", "already exists"),
        ("Networks > HTTP", 1, " ", "needs a name"),
        ("Networks > DNS", 1, "x", "after the first one"),
        ("Nowhere", 1, "x", "no text of its own"),
    ],
)
def test_split_refuses_what_cannot_be_cut(path, paragraph, title, message):
    with pytest.raises(OutlineError, match=message):
        split(sections(), path, paragraph, title)


def test_text_after_a_subsection_goes_back_to_its_own_section():
    # DNS text comes after Records is open in the list: the heading has to be written again
    document = [
        OutlineSection(("A", "B"), ["inside b"]),
        OutlineSection(("A", "B", "C"), ["inside c"]),
        OutlineSection(("A", "B"), ["back in b"]),
    ]

    chunks = split_markdown(to_markdown(document), "m1", min_chars=0)

    assert [(c.source, c.text) for c in chunks] == [
        ("A > B", "inside b"), ("A > B > C", "inside c"), ("A > B", "back in b"),
    ]


def test_the_paragraphs_of_a_section_are_only_its_own():
    assert paragraphs_of(sections(), "Networks > DNS") == ["DNS translates names."]
    assert paragraphs_of(sections(), "Networks > DNS > Nope") == []


def test_apply_needs_what_each_edit_needs():
    with pytest.raises(OutlineError, match="choose the paragraph"):
        apply(sections(), "split", "Networks > HTTP", title="x")
    with pytest.raises(OutlineError, match="unknown edit"):
        apply(sections(), "explode", "Networks")


# ---------------------------------------------------------------- through the API


class DocParser:
    def extract(self, path):
        return ParsedDocument(DOC, "fake")


def make_client(tmp_path):
    settings = Settings(provider="ollama", model="m", ollama_base_url="http://x", max_retries=1, materials_dir=tmp_path)
    llm = FakeLLM([good_question(), {"correct_supported": True, "correct_chunk_ids": [], "distractors_anchored": 3}, CLEAN_DISTRACTORS])
    parsers = Parsers(pdf=DocParser(), office=DocParser())
    client = TestClient(create_app(settings, provider_factory=lambda config: llm, parsers=parsers))
    client.llm = llm
    return client


def ready_material(client) -> str:
    return wait_for(client, upload(client).json()["id"])["id"]


def edit(client, material_id, **body):
    return client.post(f"/materials/{material_id}/outline/edit", json=body)


def test_the_outline_and_the_text_of_a_section_are_available(tmp_path):
    client = make_client(tmp_path)
    material_id = ready_material(client)

    outline = client.get(f"/materials/{material_id}/outline").json()
    text = client.get(f"/materials/{material_id}/section-text", params={"path": "Networks > HTTP"}).json()

    assert [(n["path"], n["depth"]) for n in outline][:3] == [("Networks", 1), ("Networks > HTTP", 2), ("Networks > DNS", 2)]
    assert [p["index"] for p in text] == [0, 1, 2]
    assert text[1]["text"] == "Status codes: 404 means not found."


def test_a_heading_without_text_has_no_section_text(tmp_path):
    client = make_client(tmp_path)
    material_id = ready_material(client)
    edit(client, material_id, op="merge", path="Networks > DNS")

    response = client.get(f"/materials/{material_id}/section-text", params={"path": "Networks > DNS"})

    assert response.status_code == 404


def test_an_edit_changes_the_sections_the_material_offers(tmp_path):
    client = make_client(tmp_path)
    material_id = ready_material(client)

    response = edit(client, material_id, op="split", path="Networks > HTTP", title="Cookies", paragraph=2)

    assert response.status_code == 200
    assert response.json()["path_map"] == {"Networks > HTTP": ["Networks > HTTP", "Networks > Cookies"]}
    listed = [s["path"] for s in client.get(f"/materials/{material_id}/sections").json()]
    assert "Networks > Cookies" in listed
    assert any(n["path"] == "Networks > Cookies" for n in response.json()["outline"])
    assert client.get(f"/materials/{material_id}").json()["chunk_count"] == len(listed)


def test_questions_can_be_asked_about_a_section_made_by_an_edit(tmp_path):
    client = make_client(tmp_path)
    material_id = ready_material(client)
    edit(client, material_id, op="split", path="Networks > HTTP", title="Cookies", paragraph=2)

    response = client.post("/generate", json={
        "course_id": 1, "topic": "Cookies keep state",
        "material_sections": {material_id: ["Networks > Cookies"]},
    })

    assert response.status_code == 202
    assert "Cookies keep state." in client.llm.prompts[0]
    assert "404 means" not in client.llm.prompts[0]


def test_a_refused_edit_says_why_and_changes_nothing(tmp_path):
    client = make_client(tmp_path)
    material_id = ready_material(client)
    before = client.get(f"/materials/{material_id}/sections").json()

    response = edit(client, material_id, op="rename", path="Databases", title="Networks")

    assert response.status_code == 422
    assert "already exists" in response.json()["detail"]
    assert client.get(f"/materials/{material_id}/sections").json() == before


def test_editing_an_unknown_material_or_one_still_being_read(tmp_path):
    client = make_client(tmp_path)
    assert edit(client, "nope", op="merge", path="Networks").status_code == 404

    material_id = ready_material(client)
    client.app.state.materials.set_processing(material_id)
    assert edit(client, material_id, op="merge", path="Networks > DNS").status_code == 409


def test_an_edit_needs_a_known_operation_and_a_path(tmp_path):
    client = make_client(tmp_path)
    material_id = ready_material(client)

    assert edit(client, material_id, op="explode", path="Networks").status_code == 422
    assert edit(client, material_id, op="merge", path="").status_code == 422


def test_reading_the_file_again_discards_the_edits(tmp_path):
    client = make_client(tmp_path)
    material_id = ready_material(client)
    original = [s["path"] for s in client.get(f"/materials/{material_id}/sections").json()]
    edit(client, material_id, op="rename", path="Databases", title="Data")
    assert "Data" in [s["path"] for s in client.get(f"/materials/{material_id}/sections").json()]

    client.post(f"/materials/{material_id}/reprocess")
    wait_for(client, material_id)

    assert [s["path"] for s in client.get(f"/materials/{material_id}/sections").json()] == original
