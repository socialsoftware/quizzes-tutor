"""Answers that come too fast, or too evenly, to have been read by a person.

A person needs time to read a question and its options, and that time grows with their length. A
script that asks a language model (or looks the answer up) answers in a time that does not depend on
the text, often within a couple of seconds and with little variation. Everything here uses only the
time the server recorded for each answer and the length of the question, so there is nothing for the
student to switch off.

These are signs for the teacher to look at, never proof: a fast reader, a question seen before or a
lucky guess look like this too. The result lists the reasons in words so the teacher can judge.
"""
import statistics
from dataclasses import dataclass, field

# Even a very fast skimmer reads about this many characters per second (~450 words per minute)
READ_CHARS_PER_SECOND = 40.0
# No answer is credible under this, whatever the text
MIN_SECONDS = 1.5
# Rules need enough answers to mean something
MIN_ANSWERS = 5
MIN_STUDENTS_FOR_CLASS_COMPARISON = 8

TOO_FAST_SOME = 0.3  # share of answers faster than reading time: one point
TOO_FAST_MOST = 0.6  # two points
UNIFORM_MAX_CV = 0.25  # standard deviation / mean of the times
UNIFORM_MAX_MEAN_SECONDS = 30.0
FAST_AND_RIGHT = 0.5  # share of all answers that were both too fast and correct
CLASS_SPEED_FACTOR = 3.0  # this many times faster per character than the median student

LEVELS = ((3, "HIGH"), (2, "MEDIUM"), (1, "LOW"))


@dataclass
class AnswerRow:
    user_id: int
    seconds: float
    chars: int  # length of the question and its options
    correct: bool
    answered: bool = True


@dataclass
class SpeedReport:
    user_id: int
    answered: int = 0
    too_fast: int = 0
    mean_seconds: float | None = None
    points: int = 0
    level: str = "NONE"
    reasons: list[str] = field(default_factory=list)

    def to_dict(self) -> dict:
        return {
            "answered": self.answered,
            "tooFast": self.too_fast,
            "meanSeconds": None if self.mean_seconds is None else round(self.mean_seconds, 1),
            "points": self.points,
            "level": self.level,
            "reasons": self.reasons,
        }


def reading_seconds(chars: int) -> float:
    """The least time in which the question and its options can be read."""
    return max(MIN_SECONDS, chars / READ_CHARS_PER_SECOND)


def analyse(rows: list[AnswerRow]) -> list[SpeedReport]:
    """One report per student with answers, the most suspicious first."""
    by_user: dict[int, list[AnswerRow]] = {}
    for row in rows:
        if row.answered and row.seconds > 0:
            by_user.setdefault(row.user_id, []).append(row)

    # seconds per character, per student: a student who is fast on everything stands out from the class
    speeds = {
        user: statistics.median(r.seconds / max(r.chars, 1) for r in answers)
        for user, answers in by_user.items()
        if len(answers) >= MIN_ANSWERS
    }
    class_median = statistics.median(speeds.values()) if len(speeds) >= MIN_STUDENTS_FOR_CLASS_COMPARISON else None

    reports = [_report(user, answers, speeds.get(user), class_median) for user, answers in by_user.items()]
    return sorted(reports, key=lambda r: (-r.points, -(r.too_fast / r.answered if r.answered else 0), r.user_id))


def _report(user: int, answers: list[AnswerRow], speed: float | None, class_median: float | None) -> SpeedReport:
    times = [a.seconds for a in answers]
    report = SpeedReport(user, answered=len(answers), mean_seconds=statistics.mean(times))
    fast = [a for a in answers if a.seconds < reading_seconds(a.chars)]
    report.too_fast = len(fast)
    if len(answers) < MIN_ANSWERS:
        return report

    share = len(fast) / len(answers)
    if share >= TOO_FAST_SOME:
        report.points += 2 if share >= TOO_FAST_MOST else 1
        report.reasons.append(
            f"{len(fast)} of {len(answers)} answers came in less time than it takes to read the question and its options"
        )

    mean = statistics.mean(times)
    if len(answers) >= MIN_ANSWERS + 1 and mean <= UNIFORM_MAX_MEAN_SECONDS:
        cv = statistics.pstdev(times) / mean
        if cv < UNIFORM_MAX_CV:
            report.points += 1
            report.reasons.append(
                f"almost the same time on every question (about {mean:.1f} s, varying only {cv:.0%}), whatever their length"
            )

    fast_right = sum(1 for a in fast if a.correct)
    if fast_right / len(answers) >= FAST_AND_RIGHT:
        report.points += 1
        report.reasons.append(f"{fast_right} of {len(answers)} answers were both too fast to be read and correct")

    if class_median and speed is not None and speed * CLASS_SPEED_FACTOR < class_median:
        report.points += 1
        report.reasons.append(
            f"about {class_median / speed:.0f} times faster per character than the median student of the quiz"
        )

    report.level = next((name for minimum, name in LEVELS if report.points >= minimum), "NONE")
    return report
