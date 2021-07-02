package pt.ulisboa.tecnico.socialsoftware.common.dtos.tournament;

import pt.ulisboa.tecnico.socialsoftware.common.dtos.question.TopicDto;
import pt.ulisboa.tecnico.socialsoftware.common.dtos.user.StudentDto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TournamentDto implements Serializable {
    private Integer id;
    private String courseAcronym;
    private Integer quizId;
    private String startTime;
    private String endTime;
    private Integer numberOfQuestions;
    private boolean isCanceled;
    private Set<TopicDto> topicsDto = new HashSet<>();
    private StudentDto creator;
    private List<TournamentParticipantDto> participants = new ArrayList<>();
    private boolean privateTournament = false;
    private String password = "";
    private boolean isOpened = false;
    private boolean isClosed = false;

    public TournamentDto() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCourseAcronym() {
        return courseAcronym;
    }

    public void setCourseAcronym(String courseAcronym) {
        this.courseAcronym = courseAcronym;
    }

    public Integer getQuizId() {
        return quizId;
    }

    public void setQuizId(Integer quizId) {
        this.quizId = quizId;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public Integer getNumberOfQuestions() {
        return numberOfQuestions;
    }

    public void setNumberOfQuestions(Integer numberOfQuestions) {
        this.numberOfQuestions = numberOfQuestions;
    }

    public boolean isCanceled() {
        return isCanceled;
    }

    public void setCanceled(boolean canceled) {
        isCanceled = canceled;
    }

    public Set<TopicDto> getTopicsDto() {
        return topicsDto;
    }

    public void setTopicsDto(Set<TopicDto> topicsDto) {
        this.topicsDto = topicsDto;
    }

    public StudentDto getCreator() {
        return creator;
    }

    public void setCreator(StudentDto creator) {
        this.creator = creator;
    }

    public List<TournamentParticipantDto> getParticipants() {
        return participants;
    }

    public void setParticipants(List<TournamentParticipantDto> participants) {
        this.participants = participants;
    }

    public boolean isPrivateTournament() {
        return privateTournament;
    }

    public void setPrivateTournament(boolean privateTournament) {
        this.privateTournament = privateTournament;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isOpened() {
        return isOpened;
    }

    public void setOpened(boolean opened) {
        isOpened = opened;
    }

    public boolean isClosed() {
        return isClosed;
    }

    public void setClosed(boolean closed) {
        isClosed = closed;
    }

    @Override
    public String toString() {
        return "TournamentDto{" +
                "id=" + id +
                ", courseAcronym='" + courseAcronym + '\'' +
                ", quizId=" + quizId +
                ", startTime='" + startTime + '\'' +
                ", endTime='" + endTime + '\'' +
                ", numberOfQuestions=" + numberOfQuestions +
                ", isCanceled=" + isCanceled +
                ", topicsDto=" + topicsDto +
                ", creator=" + creator +
                ", participants=" + participants +
                ", privateTournament=" + privateTournament +
                ", password='" + password + '\'' +
                ", isOpened=" + isOpened +
                ", isClosed=" + isClosed +
                '}';
    }
}