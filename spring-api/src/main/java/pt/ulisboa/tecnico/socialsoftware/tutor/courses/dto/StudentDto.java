package pt.ulisboa.tecnico.socialsoftware.tutor.courses.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;

import java.io.Serializable;

public class StudentDto implements Serializable {
    private Integer number;
    private String username;
    private String name;
    private Integer year;
    private Integer numberOfTeacherQuizzes;
    private Integer numberOfStudentQuizzes;
    private Integer numberOfAnswers;
    private Integer numberOfTeacherAnswers;
    private int percentageOfCorrectAnswers = 0;
    private int percentageOfCorrectTeacherAnswers = 0;

    public StudentDto(User user) {
        this.number = user.getNumber();
        this.username = user.getUsername();
        this.name = user.getName();
        this.year = user.getYear();
        if (user.getNumberOfTeacherQuizzes() == null) {
           user.calculateNumbers();
        }
        this.numberOfTeacherQuizzes = user.getNumberOfTeacherQuizzes();
        this.numberOfStudentQuizzes = user.getNumberOfStudentQuizzes();
        this.numberOfAnswers = user.getNumberOfAnswers();
        this.numberOfTeacherAnswers = user.getNumberOfTeacherAnswers();

        if (this.numberOfAnswers != 0) {
            this.percentageOfCorrectAnswers = user.getNumberOfCorrectAnswers() * 100 / this.numberOfAnswers;
        }

        if (this.numberOfTeacherAnswers != 0) {
            this.percentageOfCorrectTeacherAnswers = user.getNumberOfCorrectTeacherAnswers() * 100 / this.numberOfTeacherAnswers;
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
