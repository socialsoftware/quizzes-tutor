import logging

from app.llm.provider import FallbackProvider, LoggingProvider
from tests.conftest import CLEAN_DISTRACTORS, FakeLLM, good_question
from tests.test_ingestion import make_client

GROUNDING = {"correct_supported": True, "correct_chunk_ids": ["c1"], "distractors_anchored": 3}
BODY = {
    "course_id": 1,
    "topic": "HTTP",
    "chunks": [{"id": "c1", "text": "404 means the server cannot find the resource."}],
}


def replies():
    return [good_question(), GROUNDING, CLEAN_DISTRACTORS]


def test_with_the_switch_on_every_prompt_and_reply_of_a_job_is_logged(tmp_path, caplog):
    client = make_client(tmp_path, FakeLLM(replies()), log_prompts=True)

    with caplog.at_level(logging.INFO, logger="aqg.prompts"):
        assert client.post("/generate", json=BODY).status_code == 202

    text = caplog.text
    assert "PROMPT to fake/model" in text
    assert "--- system ---" in text and "--- user ---" in text
    assert "404 means the server cannot find the resource." in text
    assert "What does HTTP status 404 mean?" in text  # the reply of the model
    # the generation prompt and the two checks of the verification chain
    assert text.count("PROMPT to") == 3 and text.count("REPLY from") == 3


def test_by_default_nothing_is_logged(tmp_path, caplog):
    client = make_client(tmp_path, FakeLLM(replies()))

    with caplog.at_level(logging.INFO, logger="aqg.prompts"):
        client.post("/generate", json=BODY)

    assert "PROMPT to" not in caplog.text


def test_what_a_student_wrote_is_never_logged(tmp_path, caplog):
    client = make_client(tmp_path, FakeLLM([{"reply": "ok"}]), log_prompts=True)

    with caplog.at_level(logging.INFO, logger="aqg.prompts"):
        response = client.post("/discussion/suggest", json={
            "course_id": 1,
            "question_stem": "What does 404 mean?",
            "student_message": "my private doubt",
        })

    assert response.status_code == 200
    assert "my private doubt" not in caplog.text and "PROMPT to" not in caplog.text


def test_a_failing_model_is_logged_and_the_error_still_goes_up(caplog):
    class Broken:
        model_id = "broken/model"

        def complete(self, system, user):
            raise TimeoutError("slow")

    with caplog.at_level(logging.INFO, logger="aqg.prompts"):
        try:
            LoggingProvider(Broken()).complete("s", "u")
        except TimeoutError:
            pass
        else:
            raise AssertionError("the error was swallowed")

    assert "FAILED broken/model" in caplog.text and "TimeoutError" in caplog.text


def test_the_log_says_which_model_of_a_fallback_chain_answered(caplog):
    class Broken:
        model_id = "broken/model"

        def complete(self, system, user):
            raise TimeoutError()

    chain = FallbackProvider([LoggingProvider(Broken()), LoggingProvider(FakeLLM(["{}"]))])

    with caplog.at_level(logging.INFO, logger="aqg.prompts"):
        chain.complete("s", "u")

    assert "FAILED broken/model" in caplog.text
    assert "REPLY from fake/model" in caplog.text
