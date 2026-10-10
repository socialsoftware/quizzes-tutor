from app.generation.context_plan import plan_contexts
from app.schema import Chunk


def pieces(*sizes):
    return [Chunk(id=f"c{index}", text="x" * size) for index, size in enumerate(sizes)]


def ids(contexts):
    return [[chunk.id for chunk in context] for context in contexts]


def test_text_that_fits_goes_whole_to_every_question():
    assert ids(plan_contexts(pieces(10, 10, 10), 3, budget_chars=100)) == [["c0", "c1", "c2"]] * 3


def test_too_much_text_is_spread_over_the_whole_topic():
    # Ten windows of one piece, three questions: one from each third of the topic
    assert ids(plan_contexts(pieces(*[10] * 10), 3, budget_chars=10)) == [["c1"], ["c5"], ["c8"]]


def test_windows_hold_as_many_pieces_as_fit_in_order():
    assert ids(plan_contexts(pieces(4, 4, 4, 4), 2, budget_chars=8)) == [["c0", "c1"], ["c2", "c3"]]


def test_more_questions_than_windows_use_them_again_in_turn():
    assert ids(plan_contexts(pieces(10, 10), 3, budget_chars=10)) == [["c0"], ["c1"], ["c0"]]


def test_ranked_text_gives_the_first_windows_first():
    assert ids(plan_contexts(pieces(10, 10, 10, 10), 2, budget_chars=10, ranked=True)) == [["c0"], ["c1"]]


def test_a_piece_larger_than_the_budget_still_goes_alone():
    assert ids(plan_contexts(pieces(50, 5), 2, budget_chars=10)) == [["c0"], ["c1"]]


def test_no_text_gives_empty_contexts():
    assert plan_contexts([], 2, budget_chars=10) == [[], []]
