from sqlalchemy import create_engine, text

from app.chunking.outline import clean_headings, plain_title
from app.db import create_session_factory


def headings(markdown):
    return [line for line in markdown.splitlines() if line.startswith("#")]


def test_emphasis_is_removed_from_kept_titles():
    assert plain_title("**§3. RULES FOR _MULTIPLICATION_**") == "§3. RULES FOR MULTIPLICATION"
    assert headings(clean_headings("###### **§3. RULES**\n\ntext")) == ["###### §3. RULES"]


def test_labels_sentences_and_formulas_stop_being_headings():
    markdown = "\n\n".join([
        "# Chapter 1 Numbers", "## Example", "## Exercícios", "## which amounts to", "## Observe that",
        "## a = b + c", "## (5)", "## R 3", "## 4. Prove the following relations", "## Then",
        "## Rules for addition",
    ])
    assert headings(clean_headings(markdown)) == ["# Chapter 1 Numbers", "## Rules for addition"]
    assert "**Example**" in clean_headings(markdown)


def test_headings_repeated_many_times_are_labels():
    markdown = "\n\n".join(["# Book"] + ["## Summary\n\ntext"] * 4 + ["## Real section"])
    assert headings(clean_headings(markdown)) == ["# Book", "## Real section"]


def test_the_table_of_contents_decides_headings_and_levels():
    markdown = "###### **1 Numbers**\n\n###### Some bold line\n\n###### §2. RULES"
    toc = [(1, "1 Numbers"), (2, "§2. Rules")]
    assert headings(clean_headings(markdown, toc)) == ["# 1 Numbers", "## §2. RULES"]


def test_hash_lines_in_code_are_left_alone():
    markdown = "# Shell\n\n```\n# Example comment\n```"
    assert clean_headings(markdown) == markdown


def test_columns_added_to_a_model_are_added_to_an_existing_database(tmp_path):
    url = f"sqlite:///{tmp_path / 'old.db'}"
    engine = create_engine(url)
    with engine.begin() as connection:
        connection.execute(text(
            "CREATE TABLE material (id VARCHAR(32) PRIMARY KEY, course_id INTEGER, filename VARCHAR(255), "
            "status VARCHAR(16), chunk_count INTEGER, parser VARCHAR(32), parse_seconds FLOAT, error TEXT)"
        ))

    create_session_factory(url)

    with engine.connect() as connection:
        columns = [row[1] for row in connection.execute(text("PRAGMA table_info(material)"))]
    assert "markdown" in columns
