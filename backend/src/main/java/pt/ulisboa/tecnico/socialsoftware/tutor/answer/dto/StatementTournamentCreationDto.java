package pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.TopicDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.domain.Tournament;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class StatementTournamentCreationDto implements Serializable {
    private Integer numberOfQuestions;
    private Set<TopicDto> topics = new HashSet<>();

    public StatementTournamentCreationDto() {}

    public StatementTournamentCreationDto(Tournament tournament) {
        setNumberOfQuestions(tournament.getNumberOfQuestions());
        setTopics(tournament.getTopics().stream().map(TopicDto::new).collect(Collectors.toSet()));
    }

    public Integer getNumberOfQuestions() {
        return numberOfQuestions;
    }

    public void setNumberOfQuestions(Integer numberOfQuestions) {
        this.numberOfQuestions = numberOfQuestions;
    }

    public Set<TopicDto> getTopics() {
        return topics;
    }

    public void setTopics(Set<TopicDto> topics) { this.topics = topics; }

    @Override
    public String toString() {
        return "StatementCreationDto{" +
                "numberOfQuestions=" + numberOfQuestions +
                ", topics=" + topics +
                '}';
    }
}
