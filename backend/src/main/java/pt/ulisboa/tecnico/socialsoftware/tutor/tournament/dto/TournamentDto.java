package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.utils.DateHandler;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.TopicDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.domain.Tournament;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.StudentDto;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

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

    public TournamentDto(Tournament tournament) {
        this.id = tournament.getId();
        this.courseAcronym = tournament.getCourseExecution().getAcronym();
        if (tournament.hasQuiz()) this.quizId = tournament.getQuiz().getId();
        this.startTime = DateHandler.toISOString(tournament.getStartTime());
        this.endTime = DateHandler.toISOString(tournament.getEndTime());
        this.numberOfQuestions = tournament.getNumberOfQuestions();
        this.isCanceled = tournament.isCanceled();
        this.topicsDto = tournament.getTopics().stream()
                .map(TopicDto::new)
                .collect(Collectors.toSet());
        this.creator = new StudentDto(tournament.getCreator());
        this.participants = tournament.getParticipants().stream()
                .map(user -> new TournamentParticipantDto(user, tournament.getQuiz()))
                .sorted(Comparator.comparing(TournamentParticipantDto::getScore).reversed())
                .collect(Collectors.toList());
        this.privateTournament = tournament.isPrivateTournament();
        this.password = tournament.getPassword();
        this.isOpened = tournament.getStartTime().isBefore(DateHandler.now()) && tournament.getEndTime().isAfter(DateHandler.now());
        this.isClosed = tournament.getEndTime().isBefore(DateHandler.now());
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
}