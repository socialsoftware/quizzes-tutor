package pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
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
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Comparator.comparingInt;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toCollection;
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.CANNOT_CLOSE_CURRENT_WEEK;

@Entity
public class WeeklyScore implements DomainEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private int quizzesAnswered;

    private int questionsAnswered;

    private int questionsUniquelyAnswered;

    private int percentageCorrect;

    private int improvedCorrectAnswers;

    private LocalDate week;

    private boolean closed;

    @ManyToOne
    private Dashboard dashboard;

    public WeeklyScore() {
    }

    public WeeklyScore(Dashboard dashboard, LocalDate week) {
        setWeek(week);
        setClosed(false);
        setDashboard(dashboard);
    }

    public void remove() {
        this.dashboard.getWeeklyScores().remove(this);
        this.dashboard = null;
    }

    public void computeStatistics(Set<QuizAnswer> weeklyQuizAnswers) {
        List<QuestionAnswer> publicWeeklyQuestionAnswers = weeklyQuizAnswers.stream()
                .filter(QuizAnswer::canResultsBePublic)
                .sorted(Comparator.comparing(QuizAnswer::getAnswerDate).reversed())
                .flatMap(quizAnswer -> quizAnswer.getQuestionAnswers().stream())
                .collect(Collectors.toList());

        setQuizzesAnswered((int) weeklyQuizAnswers.stream()
                .filter(QuizAnswer::canResultsBePublic)
                .count());

        List<Question> weeklyQuestionsAnswered = publicWeeklyQuestionAnswers.stream()
                .map(QuestionAnswer::getQuizQuestion)
                .map(QuizQuestion::getQuestion).collect(Collectors.toList());

        setQuestionsAnswered((int) weeklyQuestionsAnswered.stream()
                .map(Question::getId).count());

        setQuestionsUniquelyAnswered((int) weeklyQuestionsAnswered.stream()
                .map(Question::getId).distinct().count());

        int uniquelyCorrect = (int) publicWeeklyQuestionAnswers.stream()
                .collect(collectingAndThen(toCollection(() -> new TreeSet<>(comparingInt(questionAnswer -> questionAnswer.getQuizQuestion().getQuestion().getId()))),
                        ArrayList::new)).stream()
                .filter(QuestionAnswer::isCorrect)
                .count();

        setPercentageCorrect(!publicWeeklyQuestionAnswers.isEmpty() ? (int) Math.round((publicWeeklyQuestionAnswers.stream().filter(QuestionAnswer::isCorrect).count() /
                (double) publicWeeklyQuestionAnswers.size()) * 100.0) : 0);

        setImprovedCorrectAnswers(getQuestionsUniquelyAnswered() > 0 ? (int) Math.round(uniquelyCorrect * 100 / (double) getQuestionsUniquelyAnswered()) : 0);

        if (DateHandler.now().isAfter(week.plusDays(7).atStartOfDay())) {
            closed = weeklyQuizAnswers.stream()
                    .noneMatch(quizAnswer -> !quizAnswer.canResultsBePublic());
        }
    }

    public Integer getId() {
        return id;
    }

    public int getQuizzesAnswered() {
        return quizzesAnswered;
    }

    public void setQuizzesAnswered(int quizzesAnswered) {
        this.quizzesAnswered = quizzesAnswered;
    }

    public int getQuestionsAnswered() {
        return questionsAnswered;
    }

    public void setQuestionsAnswered(int numberAnswered) {
        this.questionsAnswered = numberAnswered;
    }

    public int getQuestionsUniquelyAnswered() {
        return questionsUniquelyAnswered;
    }

    public void setQuestionsUniquelyAnswered(int uniquelyAnswered) {
        this.questionsUniquelyAnswered = uniquelyAnswered;
    }

    public int getPercentageCorrect() {
        return percentageCorrect;
    }

    public void setPercentageCorrect(int percentageCorrect) {
        this.percentageCorrect = percentageCorrect;
    }

    public int getImprovedCorrectAnswers() {
        return improvedCorrectAnswers;
    }

    public void setImprovedCorrectAnswers(int improvedCorrectAnswers) {
        this.improvedCorrectAnswers = improvedCorrectAnswers;
    }

    public LocalDate getWeek() {
        return week;
    }

    public void setWeek(LocalDate week) {
        this.week = week;
    }

    public boolean isClosed() {
        return closed;
    }

    public void setClosed(boolean close) {
        TemporalAdjuster weekSunday = TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY);
        LocalDate currentWeek = DateHandler.now().with(weekSunday).toLocalDate();
        if (close && week.isEqual(currentWeek)) {
            throw new TutorException(CANNOT_CLOSE_CURRENT_WEEK, DateHandler.toISOString(week.atStartOfDay()));
        }

        this.closed = close;
    }

    public Dashboard getDashboard() {
        return dashboard;
    }

    public void setDashboard(Dashboard dashboard) {
        this.dashboard = dashboard;
        this.dashboard.addWeeklyScore(this);
    }

    public void accept(Visitor visitor) {
        // Only to generate XML
    }

    @Override
    public String toString() {
        return "WeeklyScore{" +
                "id=" + id +
                ", quizzesAnswered=" + quizzesAnswered +
                ", questionsAnswered=" + questionsAnswered +
                ", questionsUniquelyAnswered=" + questionsUniquelyAnswered +
                ", percentageCorrect=" + percentageCorrect +
                ", week=" + week +
                ", closed=" + closed +
                ", dashboard=" + dashboard +
                '}';
    }
}
