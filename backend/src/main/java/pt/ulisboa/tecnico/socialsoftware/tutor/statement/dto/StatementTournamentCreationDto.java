package pt.ulisboa.tecnico.socialsoftware.tutor.statement.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.TopicConjunctionDto;

import java.io.Serializable;

public class StatementTournamentCreationDto implements Serializable {
    private Integer numberOfQuestions;
    private TopicConjunctionDto topicConjunction;

    public Integer getNumberOfQuestions() {
        return numberOfQuestions;
    }

    public void setNumberOfQuestions(Integer numberOfQuestions) {
        this.numberOfQuestions = numberOfQuestions;
    }

    public TopicConjunctionDto getTopicConjunction() {
        return topicConjunction;
    }

    public void setTopicConjunction(TopicConjunctionDto topicConjunction) {
        this.topicConjunction = topicConjunction;
    }

    @Override
    public String toString() {
        return "StatementCreationDto{" +
                "numberOfQuestions=" + numberOfQuestions +
                ", topicConjunction=" + topicConjunction +
                '}';
    }
}
