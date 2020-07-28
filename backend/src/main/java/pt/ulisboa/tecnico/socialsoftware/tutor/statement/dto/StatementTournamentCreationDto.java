package pt.ulisboa.tecnico.socialsoftware.tutor.statement.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.TopicConjunction;

import java.io.Serializable;

public class StatementTournamentCreationDto implements Serializable {
    private Integer numberOfQuestions;
    private TopicConjunction topicConjunction;

    public Integer getNumberOfQuestions() {
        return numberOfQuestions;
    }

    public void setNumberOfQuestions(Integer numberOfQuestions) {
        this.numberOfQuestions = numberOfQuestions;
    }

    public TopicConjunction getTopicConjunction() {
        return topicConjunction;
    }

    public void setTopicConjunction(TopicConjunction topicConjunction) {
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
