package pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.CourseExecution;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.DomainEntity;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.Visitor;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.Student;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Dashboard implements DomainEntity {
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "dashboard", orphanRemoval = true)
    private final Set<WeeklyScore> weeklyScores = new HashSet<>();

    @ElementCollection
    private final Set<RemovedDifficultQuestion> removedDifficultQuestions = new HashSet<>();

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "dashboard", orphanRemoval = true)
    private final Set<FailedAnswer> failedAnswers = new HashSet<>();

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

    private LocalDateTime lastCheckWeeklyScores;

    @ManyToOne
    private CourseExecution courseExecution;

    @ManyToOne
    private Student student;

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

    public int getNumberOfStudentQuizzes() {
        return numberOfStudentQuizzes;
    }

    public int getNumberOfInClassQuizzes() {
        return numberOfInClassQuizzes;
    }

    public int getNumberOfTeacherAnswers() {
        return numberOfTeacherAnswers;
    }

    public int getNumberOfInClassAnswers() {
        return numberOfInClassAnswers;
    }

    public int getNumberOfStudentAnswers() {
        return numberOfStudentAnswers;
    }

    public int getNumberOfCorrectTeacherAnswers() {
        return numberOfCorrectTeacherAnswers;
    }

    public int getNumberOfCorrectInClassAnswers() {
        return numberOfCorrectInClassAnswers;
    }

    public int getNumberOfCorrectStudentAnswers() {
        return numberOfCorrectStudentAnswers;
    }

    public LocalDateTime getLastCheckFailedAnswers() {
        return lastCheckFailedAnswers;
    }

    public void setLastCheckFailedAnswers(LocalDateTime lastCheckFailedAnswer) {
        this.lastCheckFailedAnswers = lastCheckFailedAnswer;
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

    public Set<FailedAnswer> getFailedAnswers() {
        return failedAnswers;
    }

    public Set<RemovedDifficultQuestion> getRemovedDifficultQuestions() {
        return removedDifficultQuestions;
    }

    public void removeRemovedDifficultQuestion(RemovedDifficultQuestion removedDifficultQuestion) {
        removedDifficultQuestions.remove(removedDifficultQuestion);
    }

    public void addRemovedDifficultQuestion(RemovedDifficultQuestion removedDifficultQuestion) {
        if (removedDifficultQuestions.stream()
                .anyMatch(removedDifficultQuestion1 -> removedDifficultQuestion1.getQuestionId().equals(removedDifficultQuestion.getQuestionId()))) {
            throw new TutorException(ErrorMessage.DIFFICULT_QUESTION_ALREADY_REMOVED);
        }
        removedDifficultQuestions.add(removedDifficultQuestion);
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

    public void addFailedAnswer(FailedAnswer failedAnswer) {
        if (failedAnswers.stream().anyMatch(failedAnswer1 -> failedAnswer1.getQuestionAnswer().getQuestion() == failedAnswer.getQuestionAnswer().getQuestion())) {
            throw new TutorException(ErrorMessage.FAILED_ANSWER_ALREADY_CREATED);
        }
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
                ", numberOfTeacherQuizzes=" + numberOfTeacherQuizzes +
                ", numberOfStudentQuizzes=" + numberOfStudentQuizzes +
                ", numberOfInClassQuizzes=" + numberOfInClassQuizzes +
                ", numberOfTeacherAnswers=" + numberOfTeacherAnswers +
                ", numberOfInClassAnswers=" + numberOfInClassAnswers +
                ", numberOfStudentAnswers=" + numberOfStudentAnswers +
                ", numberOfCorrectTeacherAnswers=" + numberOfCorrectTeacherAnswers +
                ", numberOfCorrectInClassAnswers=" + numberOfCorrectInClassAnswers +
                ", numberOfCorrectStudentAnswers=" + numberOfCorrectStudentAnswers +
                ", lastCheckFailedAnswers=" + lastCheckFailedAnswers +
                ", lastCheckWeeklyScores=" + lastCheckWeeklyScores +
                ", courseExecution=" + courseExecution +
                ", student=" + student +
                ", weeklyScores=" + weeklyScores +
                ", removedDifficultQuestions=" + removedDifficultQuestions +
                ", failedAnswers=" + failedAnswers +
                '}';
    }

}