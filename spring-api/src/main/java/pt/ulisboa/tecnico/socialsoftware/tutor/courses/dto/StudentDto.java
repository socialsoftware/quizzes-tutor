package pt.ulisboa.tecnico.socialsoftware.tutor.courses.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;

import java.io.Serializable;

public class StudentDto implements Serializable {
    private Integer number;
    private String username;
    private String name;
    private Integer year;
    private long numberOfTeacherQuizzes;
    private long numberOfStudentQuizzes;
    private int numberOfAnswers;
    private Float percentageOfCorrectAnswers;
    private int numberOfTeacherAnswers;
    private Float percentageOfCorrectTeacherAnswers;

    public StudentDto(User user) {
        this.number = user.getNumber();
        this.username = user.getUsername();
        this.name = user.getName();
        this.year = user.getYear();
        this.numberOfTeacherQuizzes = user.getQuizAnswers().stream()
                .filter(quizAnswer -> quizAnswer.getQuiz().getType().equals(Quiz.QuizType.TEACHER))
                .count();
        this.numberOfStudentQuizzes = user.getQuizAnswers().stream()
                .filter(quizAnswer -> quizAnswer.getQuiz().getType().equals(Quiz.QuizType.STUDENT))
                .count();
        this.numberOfAnswers = user.getQuizAnswers().stream()
                .mapToInt(quizAnswer -> quizAnswer.getQuiz().getQuizQuestions().size())
                .sum();
        long numberOfCorrectAnswers = user.getQuizAnswers().stream()
                .flatMap(quizAnswer -> quizAnswer.getQuestionAnswers().stream())
                .filter(questionAnswer -> questionAnswer.getOption() != null &&
                        questionAnswer.getOption().getCorrect())
                .count();
        this.percentageOfCorrectAnswers =
                this.numberOfAnswers != 0 ? (float)numberOfCorrectAnswers / this.numberOfAnswers : 0;
        this.numberOfTeacherAnswers = user.getQuizAnswers().stream()
                .filter(quizAnswer -> quizAnswer.getQuiz().getType().equals(Quiz.QuizType.TEACHER))
                .mapToInt(quizAnswer -> quizAnswer.getQuiz().getQuizQuestions().size())
                .sum();
        long numberOfCorrectTeacherAnswers = user.getQuizAnswers().stream()
                .filter(quizAnswer -> quizAnswer.getQuiz().getType().equals(Quiz.QuizType.TEACHER))
                .flatMap(quizAnswer -> quizAnswer.getQuestionAnswers().stream())
                .filter(questionAnswer -> questionAnswer.getOption() != null &&
                        questionAnswer.getOption().getCorrect())
                .count();
        this.percentageOfCorrectTeacherAnswers =
                this.numberOfTeacherAnswers != 0 ? (float)numberOfCorrectTeacherAnswers / this.numberOfTeacherAnswers : 0;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public long getNumberOfTeacherQuizzes() {
        return numberOfTeacherQuizzes;
    }

    public void setNumberOfTeacherQuizzes(long numberOfTeacherQuizzes) {
        this.numberOfTeacherQuizzes = numberOfTeacherQuizzes;
    }

    public long getNumberOfStudentQuizzes() {
        return numberOfStudentQuizzes;
    }

    public void setNumberOfStudentQuizzes(long numberOfStudentQuizzes) {
        this.numberOfStudentQuizzes = numberOfStudentQuizzes;
    }

    public int getNumberOfAnswers() {
        return numberOfAnswers;
    }

    public void setNumberOfAnswers(int numberOfAnswers) {
        this.numberOfAnswers = numberOfAnswers;
    }

    public Float getPercentageOfCorrectAnswers() {
        return percentageOfCorrectAnswers;
    }

    public void setPercentageOfCorrectAnswers(Float percentageOfCorrectAnswers) {
        this.percentageOfCorrectAnswers = percentageOfCorrectAnswers;
    }

    public int getNumberOfTeacherAnswers() {
        return numberOfTeacherAnswers;
    }

    public void setNumberOfTeacherAnswers(int numberOfTeacherAnswers) {
        this.numberOfTeacherAnswers = numberOfTeacherAnswers;
    }

    public Float getPercentageOfCorrectTeacherAnswers() {
        return percentageOfCorrectTeacherAnswers;
    }

    public void setPercentageOfCorrectTeacherAnswers(Float percentageOfCorrectTeacherAnswers) {
        this.percentageOfCorrectTeacherAnswers = percentageOfCorrectTeacherAnswers;
    }

    @Override
    public String toString() {
        return "StudentDto{" +
                "number=" + number +
                ", username='" + username + '\'' +
                ", name='" + name + '\'' +
                ", year=" + year +
                ", numberOfTeacherQuizzes=" + numberOfTeacherQuizzes +
                ", numberOfStudentQuizzes=" + numberOfStudentQuizzes +
                ", numberOfAnswers=" + numberOfAnswers +
                ", percentageOfCorrectAnswers=" + percentageOfCorrectAnswers +
                ", numberOfTeacherAnswers=" + numberOfTeacherAnswers +
                ", percentageOfCorrectTeacherAnswers=" + percentageOfCorrectTeacherAnswers +
                '}';
    }
}
