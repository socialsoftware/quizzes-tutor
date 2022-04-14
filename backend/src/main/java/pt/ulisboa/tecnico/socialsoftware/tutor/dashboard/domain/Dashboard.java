package pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.dto.DifficultQuestionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.DomainEntity;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.Visitor;

import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.Student;
import pt.ulisboa.tecnico.socialsoftware.tutor.utils.DateHandler;
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.CourseExecution;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;


import javax.persistence.*;

@Entity
public class Dashboard implements DomainEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private int numberOfTeacherQuizzes = 0;
    private int numberOfStudentQuizzes = 0;
    private int numberOfInClassQuizzes = 0;
    private int numberOfTeacherAnswers = 0;
    private int numberOfInClassAnswers = 0;
    private int numberOfStudentAnswers = 0;
    private int numberOfCorrectTeacherAnswers = 0;
    private int numberOfCorrectInClassAnswers = 0;
    private int numberOfCorrectStudentAnswers = 0;

    private LocalDateTime lastCheckFailedAnswers;
    private LocalDateTime lastCheckDifficultQuestions;
    private LocalDateTime lastCheckWeeklyScores;

    @ManyToOne
    private CourseExecution courseExecution;

    @ManyToOne
    private Student student;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "dashboard", orphanRemoval = true)
    private Set<WeeklyScore> weeklyScores = new HashSet<>();

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "dashboard", orphanRemoval = true)
    private Set<DifficultQuestion> difficultQuestions = new HashSet<>();

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "dashboard", orphanRemoval = true)
    private Set<FailedAnswer> failedAnswers = new HashSet<>();

    public Dashboard() {
    }

    public Dashboard(CourseExecution courseExecution, Student student) {
        setCourseExecution(courseExecution);
        setStudent(student);
    }

    public void remove() {
        student.getDashboards().remove(this);
        student = null;
    }

    public Integer getId() {
        return id;
    }

    public int getNumberOfTeacherQuizzes() {
        return numberOfTeacherQuizzes;
    }

    public void setNumberOfTeacherQuizzes(int numberOfTeacherQuizzes) {
        this.numberOfTeacherQuizzes = numberOfTeacherQuizzes;
    }

    public int getNumberOfStudentQuizzes() {
        return numberOfStudentQuizzes;
    }

    public void setNumberOfStudentQuizzes(int numberOfStudentQuizzes) {
        this.numberOfStudentQuizzes = numberOfStudentQuizzes;
    }

    public int getNumberOfInClassQuizzes() {
        return numberOfInClassQuizzes;
    }

    public void setNumberOfInClassQuizzes(int numberOfInClassQuizzes) {
        this.numberOfInClassQuizzes = numberOfInClassQuizzes;
    }

    public int getNumberOfTeacherAnswers() {
        return numberOfTeacherAnswers;
    }

    public void setNumberOfTeacherAnswers(int numberOfTeacherAnswers) {
        this.numberOfTeacherAnswers = numberOfTeacherAnswers;
    }

    public int getNumberOfInClassAnswers() {
        return numberOfInClassAnswers;
    }

    public void setNumberOfInClassAnswers(int numberOfInClassAnswers) {
        this.numberOfInClassAnswers = numberOfInClassAnswers;
    }

    public int getNumberOfStudentAnswers() {
        return numberOfStudentAnswers;
    }

    public void setNumberOfStudentAnswers(int numberOfStudentAnswers) {
        this.numberOfStudentAnswers = numberOfStudentAnswers;
    }

    public int getNumberOfCorrectTeacherAnswers() {
        return numberOfCorrectTeacherAnswers;
    }

    public void setNumberOfCorrectTeacherAnswers(int numberOfCorrectTeacherAnswers) {
        this.numberOfCorrectTeacherAnswers = numberOfCorrectTeacherAnswers;
    }

    public int getNumberOfCorrectInClassAnswers() {
        return numberOfCorrectInClassAnswers;
    }

    public void setNumberOfCorrectInClassAnswers(int numberOfCorrectInClassAnswers) {
        this.numberOfCorrectInClassAnswers = numberOfCorrectInClassAnswers;
    }

    public int getNumberOfCorrectStudentAnswers() {
        return numberOfCorrectStudentAnswers;
    }

    public void setNumberOfCorrectStudentAnswers(int numberOfCorrectStudentAnswers) {
        this.numberOfCorrectStudentAnswers = numberOfCorrectStudentAnswers;
    }

    public LocalDateTime getLastCheckFailedAnswers() {
        return lastCheckFailedAnswers;
    }

    public void setLastCheckFailedAnswers(LocalDateTime lastCheckFailedAnswer) {
        this.lastCheckFailedAnswers = lastCheckFailedAnswer;
    }

    public LocalDateTime getLastCheckDifficultQuestions() {
        return lastCheckDifficultQuestions;
    }

    public void setLastCheckDifficultQuestions(LocalDateTime lastCheckDifficultAnswers) {
        this.lastCheckDifficultQuestions = lastCheckDifficultAnswers;
    }

    public LocalDateTime getLastCheckWeeklyScores() {
        return lastCheckWeeklyScores;
    }

    public void setLastCheckWeeklyScores(LocalDateTime currentWeek) {
        this.lastCheckWeeklyScores = currentWeek;
    }

    public CourseExecution getCourseExecution() {
        return courseExecution;
    }

    public void setCourseExecution(CourseExecution courseExecution) {
        this.courseExecution = courseExecution;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
        this.student.addDashboard(this);
    }

    public Set<DifficultQuestion> getDifficultQuestions() {
        return difficultQuestions;
    }

    public void setDifficultQuestions(Set<DifficultQuestion> difficultQuestions) {
        this.difficultQuestions = difficultQuestions;
    }

    public Set<FailedAnswer> getFailedAnswers() {
        return failedAnswers;
    }

    public Set<WeeklyScore> getWeeklyScores() {
        return weeklyScores;
    }

    public void addWeeklyScore(WeeklyScore weeklyScore) {
        if (weeklyScores.stream().anyMatch(weeklyScore1 -> weeklyScore1.getWeek().isEqual(weeklyScore.getWeek()))) {
            throw new TutorException(ErrorMessage.WEEKLY_SCORE_ALREADY_CREATED);
        }
        weeklyScores.add(weeklyScore);
    }

    public void addDifficultQuestion(DifficultQuestion difficultQuestion) {
        if (difficultQuestions.stream()
                .anyMatch(difficultQuestion1 -> difficultQuestion1.getQuestion() == difficultQuestion.getQuestion())) {
            throw new TutorException(ErrorMessage.DIFFICULT_QUESTION_ALREADY_CREATED);
        }
        difficultQuestions.add(difficultQuestion);
    }

    public void addFailedAnswer(FailedAnswer failedAnswer) {
        if (failedAnswers.stream().anyMatch(failedAnswer1 -> failedAnswer1.getQuestionAnswer() == failedAnswer.getQuestionAnswer())) {
            throw new TutorException(ErrorMessage.FAILED_ANSWER_ALREADY_CREATED);
        }
        failedAnswers.add(failedAnswer);
    }

    public void statistics(QuizAnswer quizAnswer) {
        switch (quizAnswer.getQuiz().getType()) {
            case IN_CLASS:
                numberOfInClassQuizzes++;
                break;
            case GENERATED:
                numberOfStudentQuizzes++;
                break;
            case PROPOSED:
                numberOfTeacherQuizzes++;
                break;
        }

        quizAnswer.getQuestionAnswers().forEach(questionAnswer -> {
            switch (quizAnswer.getQuiz().getType()) {
                case IN_CLASS:
                    numberOfInClassAnswers++;
                    numberOfCorrectInClassAnswers = questionAnswer.isCorrect() ? numberOfCorrectInClassAnswers + 1 : numberOfCorrectInClassAnswers;
                    break;
                case GENERATED:
                    numberOfStudentAnswers++;
                    numberOfCorrectStudentAnswers = questionAnswer.isCorrect() ? numberOfCorrectStudentAnswers + 1 : numberOfCorrectStudentAnswers;
                    break;
                case PROPOSED:
                    numberOfTeacherAnswers++;
                    numberOfCorrectTeacherAnswers = questionAnswer.isCorrect() ? numberOfCorrectTeacherAnswers + 1 : numberOfCorrectTeacherAnswers;
                    break;
            }
        });
    }

    public void accept(Visitor visitor) {
    }

    @Override
    public String toString() {
        return "Dashboard{" +
                "id=" + id +
                ", lastCheckWeeklyScores=" + lastCheckWeeklyScores +
                ", lastCheckFailedAnswers=" + lastCheckFailedAnswers +
                ", lastCheckDifficultAnswers=" + lastCheckDifficultQuestions +
                "}";
    }

}