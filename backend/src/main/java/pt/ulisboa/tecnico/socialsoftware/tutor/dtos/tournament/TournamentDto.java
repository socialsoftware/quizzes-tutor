package pt.ulisboa.tecnico.socialsoftware.tutor.dtos.tournament;

import java.io.Serializable;
import java.util.*;

public class TournamentDto implements Serializable {
    private Integer id;
    private String courseAcronym;
    private Integer quizId;
    private String startTime;
    private String endTime;
    private Integer numberOfQuestions;
    private boolean isCanceled;
    private Set<TournamentTopicDto> topicsDto = new HashSet<>();
    private TournamentCreatorDto creator;
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

    public Set<TournamentTopicDto> getTopicsDto() {
        return topicsDto;
    }

    public void setTopicsDto(Set<TournamentTopicDto> topicsDto) {
        this.topicsDto = topicsDto;
    }

    public TournamentCreatorDto getCreator() {
        return creator;
    }

    public void setCreator(TournamentCreatorDto creator) {
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
}