package pt.ulisboa.tecnico.socialsoftware.tutor.user.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;

import java.io.Serializable;

public class StudentDto implements Serializable {
    private String username;
    private String name;
    private Integer numberOfTeacherQuizzes;
    private Integer numberOfStudentQuizzes;
    private Integer numberOfAnswers;
    private Integer numberOfTeacherAnswers;
    private int percentageOfCorrectAnswers = 0;
    private int percentageOfCorrectTeacherAnswers = 0;

    public StudentDto(User user) {
        this.username = user.getUsername();
        this.name = user.getName();
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
                ", username='" + username + '\'' +
                ", name='" + name + '\'' +
                ", numberOfTeacherQuizzes=" + numberOfTeacherQuizzes +
                ", numberOfStudentQuizzes=" + numberOfStudentQuizzes +
                ", numberOfAnswers=" + numberOfAnswers +
                ", percentageOfCorrectAnswers=" + percentageOfCorrectAnswers +
                ", numberOfTeacherAnswers=" + numberOfTeacherAnswers +
                ", percentageOfCorrectTeacherAnswers=" + percentageOfCorrectTeacherAnswers +
                '}';
    }
}
