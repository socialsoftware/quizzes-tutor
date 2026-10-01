import json

from app.schema import Chunk, Difficulty, GenerationRequest, GroundingMode

PROMPT_VERSION = "mcq-v2"

DIFFICULTY_RUBRIC = {
    Difficulty.EASY: (
        "Recall: the correct answer is a fact or definition stated directly in the context, "
        "with no need to combine information."
    ),
    Difficulty.MEDIUM: (
        "Understand/apply: answering requires relating two concepts from the context, "
        "or applying a definition to a simple example."
    ),
    Difficulty.HARD: (
        "Analyse/apply to a new case: answering requires applying a concept to a scenario the "
        "context does not spell out, or telling apart similar concepts the context also discusses."
    ),
}

GROUNDING_INSTRUCTION = {
    GroundingMode.STRICT: (
        "Use only facts stated in <context>. If the context is not enough for a valid question "
        'at this difficulty, answer only {"insufficient_context": true, "reason": "..."}.'
    ),
    GroundingMode.ENRICHED: (
        "You may add general knowledge, but the correct answer and at least 2 of the distractors "
        "must stay anchored in <context>."
    ),
}

SYSTEM_PROMPT = (
    "You are an experienced university instructor writing multiple-choice exam questions. "
    "Each question is clear and unambiguous, has exactly four options and exactly one "
    "indisputably correct option. Distractors are plausible but clearly wrong. Never refer to "
    "'the text' or 'the context' inside the question, never use 'all/none of the above' "
    "options and never letter or number the options. {language} "
    "Reply with a single JSON object and nothing else."
)


def system_prompt(language: str | None) -> str:
    instruction = (
        f"Write the question, the options and the explanation in {language}, even if the "
        "context is in another language."
        if language
        else "Write in the language of the context."
    )
    return SYSTEM_PROMPT.format(language=instruction)

OUTPUT_SCHEMA = {
    "stem": "question text",
    "options": [{"content": "option text", "correct": True}],
    "explanation": "why the correct option is right",
}


def format_context(chunks: list[Chunk]) -> str:
    return "\n\n".join(f'[chunk id="{c.id}"]\n{c.text}\n[/chunk]' for c in chunks)


def build_generation_prompt(request: GenerationRequest, feedback: str | None = None) -> str:
    parts = [f"<context>\n{format_context(request.chunks)}\n</context>"]
    if request.style_examples:
        examples = "\n\n".join(request.style_examples)
        parts.append(f"<style_examples>\n{examples}\n</style_examples>")
    parts.append(
        "<task>\n"
        f"Topic: {request.topic}\n"
        f"Difficulty: {request.difficulty.value} - {DIFFICULTY_RUBRIC[request.difficulty]}\n"
        f"Grounding: {GROUNDING_INSTRUCTION[request.grounding_mode]}\n\n"
        "Write exactly one multiple-choice question with 4 options (1 correct, 3 distractors) "
        f"as JSON with this shape: {json.dumps(OUTPUT_SCHEMA)}\n"
        "</task>"
    )
    if request.revision:
        previous = request.revision.previous.model_dump(include={"stem", "options", "explanation"})
        parts.append(
            "<teacher_review>\n"
            f"Previous version of the question: {json.dumps(previous, ensure_ascii=False)}\n"
            f"Teacher's review: {request.revision.review}\n"
            "Rewrite this question so that it addresses every point of the review. Keep what "
            "the review does not ask to change.\n"
            "</teacher_review>"
        )
    if feedback:
        parts.append(
            "<previous_attempt_problems>\n"
            f"{feedback}\nFix these problems in the new question.\n"
            "</previous_attempt_problems>"
        )
    return "\n\n".join(parts)


GROUNDING_CHECK_SYSTEM = (
    "You audit multiple-choice questions against source material. "
    "Reply with a single JSON object and nothing else."
)


def build_grounding_check_prompt(chunks: list[Chunk], question_json: str) -> str:
    return (
        f"<context>\n{format_context(chunks)}\n</context>\n\n"
        f"<question>\n{question_json}\n</question>\n\n"
        "Check the question against the context. Reply as JSON: "
        '{"correct_supported": true|false, "correct_chunk_ids": ["chunk ids that support the '
        'option marked correct"], "distractors_anchored": <number of wrong options built on '
        'concepts present in the context>, "unsupported_claims": ["claims in the question or '
        'options that the context does not support"]}'
    )


DISTRACTOR_CHECK_SYSTEM = (
    "You review the distractors of multiple-choice questions. "
    "Reply with a single JSON object and nothing else."
)


def build_distractor_check_prompt(question_json: str) -> str:
    return (
        f"<question>\n{question_json}\n</question>\n\n"
        "The option marked correct is the only intended answer. Reply as JSON: "
        '{"also_correct": [indexes (0-based) of options NOT marked correct that are in fact '
        'also correct], "implausible": [indexes of distractors so absurd that nobody would '
        'pick them]}'
    )
