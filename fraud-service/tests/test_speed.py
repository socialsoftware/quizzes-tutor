import os
import sys

import pytest

sys.path.insert(0, os.path.join(os.path.dirname(__file__), ".."))

from fraud_times.speed import AnswerRow, across_quizzes, analyse, reading_seconds  # noqa: E402

LONG = 800  # characters of a question with its options: at least 20 s to read


def student(user, seconds, correct=True, chars=LONG, n=8):
    """n answers of the given times (a number repeats it, a list is used as it is)."""
    times = seconds if isinstance(seconds, list) else [seconds] * n
    return [AnswerRow(user, t, chars, correct) for t in times]


def report_of(rows, user):
    return next(r for r in analyse(rows) if r.user_id == user)


def test_reading_time_grows_with_the_text_and_has_a_floor():
    assert reading_seconds(0) == 1.5 and reading_seconds(30) == 1.5
    assert reading_seconds(800) == pytest.approx(20.0)
    assert reading_seconds(1600) == pytest.approx(40.0)


def test_a_student_who_takes_a_normal_time_is_not_flagged():
    rows = student(1, [25, 40, 31, 52, 28, 61, 35, 44])

    report = report_of(rows, 1)

    assert (report.level, report.points, report.reasons, report.too_fast) == ("NONE", 0, [], 0)


def test_answering_most_questions_faster_than_they_can_be_read_is_flagged():
    rows = student(1, [3, 2, 4, 3, 2, 5, 3, 30])

    report = report_of(rows, 1)

    assert report.too_fast == 7
    assert report.points >= 2 and report.level in ("MEDIUM", "HIGH")
    assert any("7 of 8 answers came in less time" in reason for reason in report.reasons)


def test_a_few_fast_answers_are_one_point_not_a_verdict():
    rows = student(1, [3, 4, 30, 45, 33, 41, 38, 29, 52, 36])  # 2 of 10 too fast = 20%

    assert report_of(rows, 1).points == 0
    rows = student(2, [3, 4, 5, 45, 33, 41, 38, 29, 52, 36])  # 3 of 10 = 30%

    report = report_of(rows, 2)
    assert report.points == 1 and report.level == "LOW"


def test_the_same_time_on_every_question_is_flagged_even_when_it_is_not_fast():
    # about 28 s on each, texts of very different lengths: a person's time follows the text
    rows = [AnswerRow(1, t, c, True) for t, c in zip([28, 29, 27, 28, 30, 28, 29], [200, 900, 400, 1000, 300, 800, 500])]

    report = report_of(rows, 1)

    assert any("almost the same time on every question" in r for r in report.reasons)
    assert report.too_fast == 0


def test_a_slow_even_pace_is_not_flagged_as_uniform():
    rows = student(1, [90, 91, 89, 90, 92, 90, 91, 90], chars=3000)

    assert report_of(rows, 1).reasons == []


def test_fast_and_right_adds_a_point():
    fast_right = report_of(student(1, [3, 2, 4, 3, 2, 5, 3, 2], correct=True), 1)
    fast_wrong = report_of(student(2, [3, 2, 4, 3, 2, 5, 3, 2], correct=False), 2)

    assert fast_right.points == fast_wrong.points + 1
    assert any("both too fast to be read and correct" in r for r in fast_right.reasons)


def test_being_far_faster_than_the_class_is_flagged_when_there_are_enough_students():
    rows = []
    for user in range(2, 12):  # ten ordinary students, 20-60 s per answer of 800 characters
        rows += student(user, [20, 35, 28, 60, 41, 30, 25, 50])
    rows += student(1, [6, 7, 5, 8, 6, 9, 7, 6])  # a third of the class's pace per character or less

    report = report_of(rows, 1)

    assert any("times faster per character than the median student" in r for r in report.reasons)


def test_the_class_comparison_needs_enough_students():
    rows = []
    for user in range(2, 6):
        rows += student(user, [20, 35, 28, 60, 41, 30, 25, 50])
    rows += student(1, [6, 7, 5, 8, 6, 9, 7, 6])

    assert not any("median student" in r for r in report_of(rows, 1).reasons)


def test_too_few_answers_are_never_flagged():
    report = report_of(student(1, [1, 1, 1, 1], n=4), 1)

    assert (report.answered, report.points, report.level) == (4, 0, "NONE")


def test_unanswered_questions_and_missing_times_are_ignored():
    rows = student(1, [3, 2, 4, 3, 2, 5, 3, 2])
    rows += [AnswerRow(1, 0, LONG, False), AnswerRow(1, 2, LONG, False, answered=False)]

    assert report_of(rows, 1).answered == 8


def test_the_most_suspicious_student_comes_first_and_the_report_is_plain_data():
    rows = student(1, [30, 45, 38, 52, 33, 41, 38, 29]) + student(2, [3, 2, 4, 3, 2, 5, 3, 2])

    reports = analyse(rows)

    assert [r.user_id for r in reports] == [2, 1]
    data = reports[0].to_dict()
    assert set(data) == {"answered", "tooFast", "meanSeconds", "points", "level", "reasons"}
    assert isinstance(data["reasons"], list) and data["meanSeconds"] == pytest.approx(3.0)


def test_nothing_in_nothing_out():
    assert analyse([]) == []


def test_most_answers_too_fast_is_worth_two_points_and_some_one_point_on_that_rule_alone():
    # wrong answers at uneven times, so only the "faster than reading" rule applies
    most = report_of(student(1, [3, 9, 4, 12, 5, 10, 6, 11], correct=False), 1)  # 8 of 8
    some = report_of(student(2, [3, 9, 4, 12, 45, 50, 38, 47, 41, 55], correct=False), 2)  # 4 of 10

    assert (most.points, some.points) == (2, 1)


def test_an_ordinary_student_is_not_called_faster_than_the_class():
    rows = []
    for user in range(2, 12):
        rows += student(user, [20, 35, 28, 60, 41, 30, 25, 50])
    rows += student(1, [6, 7, 5, 8, 6, 9, 7, 6])

    ordinary = report_of(rows, 2)

    assert not any("median student" in r for r in ordinary.reasons)


# ---------------------------------------------------------------- quizzes of five questions


def test_five_answers_with_almost_the_same_time_are_flagged_as_even():
    # 23, 21, 20, 21 and 21 seconds on five different questions, as the server recorded them
    report = report_of(student(1, [23, 21, 20, 21, 21], chars=700), 1)

    assert report.points >= 1
    assert any("almost the same time on every question" in reason for reason in report.reasons)


def test_five_answers_with_the_usual_spread_are_not():
    report = report_of(student(1, [25, 40, 31, 52, 28], chars=700), 1)

    assert report.reasons == []


def test_one_long_wait_followed_by_quick_answers_is_flagged():
    # one question open for nearly two minutes, then the others answered in 7, 9 and 1 seconds
    report = report_of(student(1, [109, 7, 9, 1, 25], chars=500), 1)

    assert any("one question took 109 s and then 3 others took 20 s or less" in reason for reason in report.reasons)
    assert report.points >= 1


@pytest.mark.parametrize(
    "times",
    [
        [70, 45, 50, 38, 60],  # slow everywhere
        [90, 30, 40, 35, 28],  # one slow one, the rest at a normal pace
        [90, 7, 9, 40, 35],  # only two quick ones
        [50, 5, 6, 7, 8],  # the slow one is not slow enough
    ],
)
def test_a_slow_question_alone_is_not_a_burst(times):
    report = report_of(student(1, times, chars=500), 1)

    assert not any("others took" in reason for reason in report.reasons)


# ---------------------------------------------------------------- over the quizzes of a course


def quiz_rows(*students):
    rows = []
    for user, times in students:
        rows += student(user, times, chars=700)
    return rows


def test_a_pattern_that_repeats_over_quizzes_comes_first_and_clean_students_are_left_out():
    even = [23, 21, 20, 21, 21]
    normal = [25, 40, 31, 52, 28]
    by_quiz = {
        1: ("Quiz 1", quiz_rows((1, [3, 4, 2, 5, 3]), (2, even), (3, normal))),
        2: ("Quiz 2", quiz_rows((1, [4, 3, 3, 2, 6]), (2, normal), (3, normal))),
        3: ("Quiz 3", quiz_rows((1, even), (2, normal), (3, normal))),
    }

    users = across_quizzes(by_quiz)

    assert [u.user_id for u in users] == [1, 2]
    first = users[0]
    assert first.quizzes_analysed == 3
    assert first.flagged_quizzes >= 2
    assert first.quizzes[0].points >= first.quizzes[-1].points
    assert {q.title for q in first.quizzes} == {"Quiz 1", "Quiz 2", "Quiz 3"}
    assert users[1].flagged_quizzes <= 1


def test_trying_to_reopen_a_quiz_that_opens_once_makes_a_student_worth_a_look_on_its_own():
    by_quiz = {1: ("Quiz 1", quiz_rows((1, [25, 40, 31, 52, 28])))}

    users = across_quizzes(by_quiz, {1: 3, 2: 0})

    assert [(u.user_id, u.blocked_openings) for u in users] == [(1, 3)]


def test_no_quizzes_no_students():
    assert across_quizzes({}) == []
    assert across_quizzes({}, {5: 2})[0].blocked_openings == 2


def test_the_history_is_what_the_service_answers_in_json():
    users = across_quizzes({1: ("Quiz 1", quiz_rows((1, [3, 4, 2, 5, 3])))})

    data = users[0].to_dict()

    assert data["quizzesAnalysed"] == 1 and data["flaggedQuizzes"] == 1 and data["blockedOpenings"] == 0
    assert data["quizzes"][0]["title"] == "Quiz 1" and data["quizzes"][0]["level"] in ("MEDIUM", "HIGH")

