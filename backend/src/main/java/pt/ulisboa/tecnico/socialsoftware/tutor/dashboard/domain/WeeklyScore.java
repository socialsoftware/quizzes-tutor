package pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.DomainEntity;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.Visitor;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion;
import pt.ulisboa.tecnico.socialsoftware.tutor.utils.DateHandler;

import javax.persistence.*;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalAdjusters;
import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
public class WeeklyScore implements DomainEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private int numberAnswered;

    private int uniquelyAnswered;

    private int percentageCorrect;

    private LocalDate week;

    @ManyToOne
    private Dashboard dashboard;

    public WeeklyScore() {}

    public WeeklyScore(Dashboard dashboard) {
        TemporalAdjuster weekSunday = TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY);

        setWeek(DateHandler.now().with(weekSunday).toLocalDate());
        setDashboard(dashboard);

        computeStatistics();
    }

    private Set<QuestionAnswer> getWeeklyQuestionAnswers(Dashboard dashboard) {
        return dashboard.getStudent().getQuizAnswers().stream()
                .filter(this::isAnswerWithinWeek)
                .map(QuizAnswer::getQuestionAnswers)
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());
    }

    private boolean isAnswerWithinWeek(QuizAnswer quizAnswer) {
        TemporalAdjuster weekSaturday = TemporalAdjusters.nextOrSame(DayOfWeek.SATURDAY);

        LocalDate answerDate = quizAnswer.getAnswerDate().toLocalDate();
        return quizAnswer.canResultsBePublic(dashboard.getCourseExecution().getId()) &&
                (answerDate.isEqual(this.week) || answerDate.isEqual(this.week.with(weekSaturday)) ||
                        (answerDate.isAfter(this.week) && answerDate.isBefore(this.week.with(weekSaturday))));
    }

    public Integer getId() { return id; }

    public int getNumberAnswered() { return numberAnswered; }

    public void setNumberAnswered(int numberAnswered) {
        this.numberAnswered = numberAnswered;
    }

    public int getUniquelyAnswered() { return uniquelyAnswered; }

    public void setUniquelyAnswered(int uniquelyAnswered) {
        this.uniquelyAnswered = uniquelyAnswered;
    }

    public int getPercentageCorrect() { return percentageCorrect; }

    public void setPercentageCorrect(int percentageCorrect) {
        this.percentageCorrect = percentageCorrect;
    }

    public LocalDate getWeek() { return week; }

    public void setWeek(LocalDate week) {
        this.week = week;
    }

    public Dashboard getDashboard() { return dashboard; }

    public void setDashboard(Dashboard dashboard) {
        this.dashboard = dashboard;
        this.dashboard.addWeeklyScore(this);
    }

    public void accept(Visitor visitor) {
    }

    public void computeStatistics() {
        Set<QuestionAnswer> weeklyQuestionAnswers = getWeeklyQuestionAnswers(dashboard);
        Set<Question> weeklyQuestionsAnswered = weeklyQuestionAnswers.stream()
                .map(QuestionAnswer::getQuizQuestion)
                .map(QuizQuestion::getQuestion).collect(Collectors.toSet());

        setNumberAnswered((int) weeklyQuestionsAnswered.stream()
                .map(Question::getId).count());
        setUniquelyAnswered((int) weeklyQuestionsAnswered.stream()
                .map(Question::getId).distinct().count());
        setPercentageCorrect(weeklyQuestionAnswers.size() > 0 ? (int) Math.round((weeklyQuestionAnswers.stream().filter(QuestionAnswer::isCorrect).count() /
                (double) weeklyQuestionAnswers.size()) * 100.0) : 0);
    }

    public void remove() {
        this.dashboard.getWeeklyScores().remove(this);
        this.dashboard = null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WeeklyScore that = (WeeklyScore) o;
        return numberAnswered == that.numberAnswered &&
                uniquelyAnswered == that.uniquelyAnswered &&
                percentageCorrect == that.percentageCorrect &&
                week.equals(that.week) &&
                dashboard.equals(that.dashboard);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, numberAnswered, uniquelyAnswered, percentageCorrect, week);
    }

    @Override
    public String toString() {
        return "WeeklyScore{" +
                "id=" + getId() +
                ", numberAnswered=" + numberAnswered +
                ", uniquelyAnswered=" + uniquelyAnswered +
                ", percentageCorrect=" + percentageCorrect +
                ", week=" + getWeek() +
                "}";
    }
}
