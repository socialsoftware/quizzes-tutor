package pt.ulisboa.tecnico.socialsoftware.tutor.courses.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;

import java.io.Serializable;

public class StudentDto implements Serializable {
    private Integer number;
    private String username;
    private String name;
    private Integer year;
    private int numberOfTeacherQuizzes;
    private int numberOfStudentQuizzes;
    private int numberOfAnswers;
    private int numberOfTeacherAnswers;
    private Integer percentageOfCorrectAnswers = 0;
    private Integer percentageOfCorrectTeacherAnswers = 0;

    public StudentDto(User user) {
        this.number = user.getNumber();
        this.username = user.getUsername();
        this.name = user.getName();
        this.year = user.getYear();
        this.numberOfTeacherQuizzes = (int) user.getQuizAnswers().stream()
                .filter(quizAnswer -> quizAnswer.getQuiz().getType().equals(Quiz.QuizType.TEACHER))
                .count();

        this.numberOfStudentQuizzes = (int) user.getQuizAnswers().stream()
                .filter(quizAnswer -> quizAnswer.getQuiz().getType().equals(Quiz.QuizType.STUDENT))
                .count();

        this.numberOfAnswers = user.getQuizAnswers().stream()
                .mapToInt(quizAnswer -> quizAnswer.getQuiz().getQuizQuestions().size())
                .sum();

        int numberOfCorrectAnswers = (int) user.getQuizAnswers().stream()
                .flatMap(quizAnswer -> quizAnswer.getQuestionAnswers().stream())
                .filter(questionAnswer -> questionAnswer.getOption() != null &&
                        questionAnswer.getOption().getCorrect())
                .count();

        this.numberOfTeacherAnswers = user.getQuizAnswers().stream()
                .filter(quizAnswer -> quizAnswer.getQuiz().getType().equals(Quiz.QuizType.TEACHER))
                .mapToInt(quizAnswer -> quizAnswer.getQuiz().getQuizQuestions().size())
                .sum();

        int numberOfCorrectTeacherAnswers = (int) user.getQuizAnswers().stream()
                .filter(quizAnswer -> quizAnswer.getQuiz().getType().equals(Quiz.QuizType.TEACHER))
                .flatMap(quizAnswer -> quizAnswer.getQuestionAnswers().stream())
                .filter(questionAnswer -> questionAnswer.getOption() != null &&
                        questionAnswer.getOption().getCorrect())
                .count();

        if (this.numberOfAnswers != 0) {
            this.percentageOfCorrectAnswers = numberOfCorrectAnswers * 100 / this.numberOfAnswers;
        }

        if (this.numberOfTeacherAnswers != 0) {
            this.percentageOfCorrectTeacherAnswers = numberOfCorrectTeacherAnswers * 100 / this.numberOfTeacherAnswers;
        }
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

    public void setNumberOfTeacherQuizzes(int numberOfTeacherQuizzes) {
        this.numberOfTeacherQuizzes = numberOfTeacherQuizzes;
    }

    public long getNumberOfStudentQuizzes() {
        return numberOfStudentQuizzes;
    }

    public void setNumberOfStudentQuizzes(int numberOfStudentQuizzes) {
        this.numberOfStudentQuizzes = numberOfStudentQuizzes;
    }

    public int getNumberOfAnswers() {
        return numberOfAnswers;
    }

    public void setNumberOfAnswers(int numberOfAnswers) {
        this.numberOfAnswers = numberOfAnswers;
    }

    public Integer getPercentageOfCorrectAnswers() {
        return percentageOfCorrectAnswers;
    }

    public void setPercentageOfCorrectAnswers(Integer percentageOfCorrectAnswers) {
        this.percentageOfCorrectAnswers = percentageOfCorrectAnswers;
    }

    public int getNumberOfTeacherAnswers() {
        return numberOfTeacherAnswers;
    }

    public void setNumberOfTeacherAnswers(int numberOfTeacherAnswers) {
        this.numberOfTeacherAnswers = numberOfTeacherAnswers;
    }

    public Integer getPercentageOfCorrectTeacherAnswers() {
        return percentageOfCorrectTeacherAnswers;
    }

    public void setPercentageOfCorrectTeacherAnswers(Integer percentageOfCorrectTeacherAnswers) {
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
