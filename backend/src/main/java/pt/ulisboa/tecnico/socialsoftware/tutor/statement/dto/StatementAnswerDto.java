package pt.ulisboa.tecnico.socialsoftware.tutor.statement.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer;

import java.io.Serializable;

public class StatementAnswerDto implements Serializable {
    private Integer timeTaken;
    private Integer sequence;
    private Integer optionId;

    public StatementAnswerDto() {}

    public StatementAnswerDto(QuestionAnswer questionAnswer) {
        this.timeTaken = questionAnswer.getTimeTaken();
        this.sequence = questionAnswer.getSequence();

        if (questionAnswer.getOption() != null) {
            this.optionId = questionAnswer.getOption().getId();
        }
    }

    public Integer getOptionId() {
        return optionId;
    }

    public void setOptionId(Integer optionId) {
        this.optionId = optionId;
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

    @Override
    public String toString() {
        return "StatementAnswerDto{" +
                ", optionId=" + optionId +
                ", timeTaken=" + timeTaken +
                ", sequence=" + sequence +
                '}';
    }
}