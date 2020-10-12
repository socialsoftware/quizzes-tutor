package pt.ulisboa.tecnico.socialsoftware.tutor.statement.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.FillInOption;

import java.io.Serializable;

public class CodeFillInOptionStatementAnswerDto implements Serializable {

    private Integer sequence;
    private Integer optionId;

    public CodeFillInOptionStatementAnswerDto() {
    }

    public CodeFillInOptionStatementAnswerDto(FillInOption option) {
        this.sequence = option.getFillInSpot().getSequence();
        this.optionId = option.getId();
    }

    public Integer getSequence() {
        return sequence;
    }

    public void setSequence(Integer sequence) {
        this.sequence = sequence;
    }

    public Integer getOptionId() {
        return optionId;
    }

    public void setOptionId(Integer optionId) {
        this.optionId = optionId;
    }
}