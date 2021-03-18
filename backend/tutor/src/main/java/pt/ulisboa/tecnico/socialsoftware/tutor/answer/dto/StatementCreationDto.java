package pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto;

import java.io.Serializable;

public class StatementCreationDto implements Serializable {
    private Integer numberOfQuestions;
    private Integer assessment;

    public Integer getNumberOfQuestions() {
        return numberOfQuestions;
    }

    public void setNumberOfQuestions(Integer numberOfQuestions) {
        this.numberOfQuestions = numberOfQuestions;
    }

    public Integer getAssessment() {
        return assessment;
    }

    public void setAssessment(Integer assessment) {
        this.assessment = assessment;
    }

    @Override
    public String toString() {
        return "StatementCreationDto{" +
                "numberOfQuestions=" + numberOfQuestions +
                ", assessment=" + assessment +
                '}';
    }
}
