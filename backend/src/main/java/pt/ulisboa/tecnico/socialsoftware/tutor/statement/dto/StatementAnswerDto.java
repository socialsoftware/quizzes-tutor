package pt.ulisboa.tecnico.socialsoftware.tutor.statement.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer;

import java.io.Serializable;

public abstract class StatementAnswerDto implements Serializable {
    private Integer timeTaken;
    private Integer sequence;

    public StatementAnswerDto() {
    }

    public StatementAnswerDto(QuestionAnswer questionAnswer) {
        this.timeTaken = questionAnswer.getTimeTaken();
        this.sequence = questionAnswer.getSequence();
    }

    public Integer getTimeTaken() {
        return timeTaken;
    }

    public void setTimeTaken(Integer timeTaken) {
        this.timeTaken = timeTaken;
    }

    public Integer getSequence() {
        return sequence;
    }

    public void setSequence(Integer sequence) {
        this.sequence = sequence;
    }
}