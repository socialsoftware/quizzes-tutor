package pt.ulisboa.tecnico.socialsoftware.tutor.anticorruptionlayer.tournament.dtos;

import pt.ulisboa.tecnico.socialsoftware.tutor.anticorruptionlayer.question.dtos.TopicWithCourseDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.TopicDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.domain.Tournament;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.domain.TournamentTopic;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class StatementTournamentCreationDto implements Serializable {
    private Integer id;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer numberOfQuestions;
    private Set<TopicWithCourseDto> topics = new HashSet<>();

    public StatementTournamentCreationDto() {}

    public StatementTournamentCreationDto(Tournament tournament) {
        setNumberOfQuestions(tournament.getNumberOfQuestions());
        setTopics(tournament.getTopics().stream().map(TopicWithCourseDto::new).collect(Collectors.toSet()));
        setStartTime(tournament.getStartTime());
        setEndTime(tournament.getEndTime());
        setId(tournament.getId());
    }

    public Integer getNumberOfQuestions() {
        return numberOfQuestions;
    }

    public void setNumberOfQuestions(Integer numberOfQuestions) {
        this.numberOfQuestions = numberOfQuestions;
    }

    public Set<TopicWithCourseDto> getTopics() {
        return topics;
    }

    public void setTopics(Set<TopicWithCourseDto> topics) {
        this.topics = topics;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    @Override
    public String toString() {
        return "StatementCreationDto{" +
                "numberOfQuestions=" + numberOfQuestions +
                ", topics=" + topics +
                '}';
    }
}
