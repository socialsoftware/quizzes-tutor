package pt.ulisboa.tecnico.socialsoftware.tutor.user.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;

import java.io.Serializable;
import java.time.format.DateTimeFormatter;

public class StudentDto implements Serializable {
    private String username;
    private String name;
    private Integer numberOfTeacherQuizzes;
    private Integer numberOfStudentQuizzes;
    private Integer numberOfAnswers;
    private Integer numberOfTeacherAnswers;
    private int percentageOfCorrectAnswers = 0;
    private int percentageOfCorrectTeacherAnswers = 0;
    private String creationDate;
    private String lastAccess;

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

        if (user.getLastAccess() != null)
            this.lastAccess = user.getLastAccess().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        if (user.getCreationDate() != null)
            this.creationDate = user.getCreationDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        if (this.numberOfAnswers != 0)
            this.percentageOfCorrectAnswers = user.getNumberOfCorrectAnswers() * 100 / this.numberOfAnswers;
        if (this.numberOfTeacherAnswers != 0)
            this.percentageOfCorrectTeacherAnswers = user.getNumberOfCorrectTeacherAnswers() * 100 / this.numberOfTeacherAnswers;
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

    public Integer getNumberOfTeacherQuizzes() {
        return numberOfTeacherQuizzes;
    }

    public void setNumberOfTeacherQuizzes(Integer numberOfTeacherQuizzes) {
        this.numberOfTeacherQuizzes = numberOfTeacherQuizzes;
    }

    public Integer getNumberOfStudentQuizzes() {
        return numberOfStudentQuizzes;
    }

    public void setNumberOfStudentQuizzes(Integer numberOfStudentQuizzes) {
        this.numberOfStudentQuizzes = numberOfStudentQuizzes;
    }

    public Integer getNumberOfAnswers() {
        return numberOfAnswers;
    }

    public void setNumberOfAnswers(Integer numberOfAnswers) {
        this.numberOfAnswers = numberOfAnswers;
    }

    public Integer getNumberOfTeacherAnswers() {
        return numberOfTeacherAnswers;
    }

    public void setNumberOfTeacherAnswers(Integer numberOfTeacherAnswers) {
        this.numberOfTeacherAnswers = numberOfTeacherAnswers;
    }

    public int getPercentageOfCorrectAnswers() {
        return percentageOfCorrectAnswers;
    }

    public void setPercentageOfCorrectAnswers(int percentageOfCorrectAnswers) {
        this.percentageOfCorrectAnswers = percentageOfCorrectAnswers;
    }

    public int getPercentageOfCorrectTeacherAnswers() {
        return percentageOfCorrectTeacherAnswers;
    }

    public void setPercentageOfCorrectTeacherAnswers(int percentageOfCorrectTeacherAnswers) {
        this.percentageOfCorrectTeacherAnswers = percentageOfCorrectTeacherAnswers;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public String getLastAccess() {
        return lastAccess;
    }

    public void setLastAccess(String lastAccess) {
        this.lastAccess = lastAccess;
    }

    @Override
    public String toString() {
        return "StudentDto{" +
                "username='" + username + '\'' +
                ", name='" + name + '\'' +
                ", numberOfTeacherQuizzes=" + numberOfTeacherQuizzes +
                ", numberOfStudentQuizzes=" + numberOfStudentQuizzes +
                ", numberOfAnswers=" + numberOfAnswers +
                ", numberOfTeacherAnswers=" + numberOfTeacherAnswers +
                ", percentageOfCorrectAnswers=" + percentageOfCorrectAnswers +
                ", percentageOfCorrectTeacherAnswers=" + percentageOfCorrectTeacherAnswers +
                ", creationDate='" + creationDate + '\'' +
                ", lastAccess='" + lastAccess + '\'' +
                '}';
    }
}
