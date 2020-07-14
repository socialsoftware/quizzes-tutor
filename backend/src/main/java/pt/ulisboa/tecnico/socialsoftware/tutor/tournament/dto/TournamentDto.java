package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.config.DateHandler;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.TopicConjunction;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.TopicConjunctionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.domain.Tournament;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.StudentDto;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class TournamentDto implements Serializable {
    private Integer id;
    private String startTime = null;
    private String endTime = null;
    private Integer numberOfQuestions;
    private Tournament.Status state;
    private TopicConjunctionDto topicConjunction;
    private Set<StudentDto> participants = new HashSet<>();
    private String courseAcronym = null;
    private Integer quizId = null;
    private boolean privateTournament = false;
    private String password = "";

    public TournamentDto() {
    }

    public TournamentDto(Tournament tournament) {
        this.id = tournament.getId();
        this.startTime = DateHandler.toISOString(tournament.getStartTime());
        this.endTime = DateHandler.toISOString(tournament.getEndTime());
        this.numberOfQuestions = tournament.getNumberOfQuestions();
        this.state = tournament.getState();
        this.topicConjunction = new TopicConjunctionDto(tournament.getTopicConjunction());
        this.participants = tournament.getParticipants().stream().map(StudentDto::new).collect(Collectors.toSet());
        this.courseAcronym = tournament.getCourseExecution().getAcronym();
        if (tournament.hasQuiz()) this.quizId = tournament.getQuizId();
        this.privateTournament = tournament.isPrivateTournament();
        this.password = tournament.getPassword();
    }

    public Integer getId() { return id; }

    public String getStartTime() { return startTime; }

    public void setStartTime(String startTime) { this.startTime = startTime; }

    public String getEndTime() { return endTime; }

    public void setEndTime(String endTime) { this.endTime = endTime; }

    public Integer getNumberOfQuestions() { return numberOfQuestions; }

    public void setNumberOfQuestions(Integer numberOfQuestions) { this.numberOfQuestions = numberOfQuestions; }

    public Tournament.Status getState() { return state; }

    public void setState(Tournament.Status state) { this.state = state; }

    public TopicConjunctionDto getTopicConjunction() { return topicConjunction; }

    public void setTopicConjunction(TopicConjunctionDto topicConjunction) { this.topicConjunction = topicConjunction; }

    public Set<StudentDto> getParticipants() { return participants; }

    public String getCourseAcronym() { return courseAcronym; }

    public void setCourseAcronym(String courseAcronym) { this.courseAcronym = courseAcronym; }

    public Integer getQuizId() { return quizId; }

    public void setQuizId(Integer quizId) { this.quizId = quizId; }

    public boolean isPrivateTournament() { return privateTournament; }

    public void setPrivateTournament(boolean privateTournament) { this.privateTournament = privateTournament; }

    public String getPassword() { return password; }

    public void setPassword(String password) { this.password = password; }
}