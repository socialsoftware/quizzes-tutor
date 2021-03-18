package pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.CodeFillInOption;

import java.io.Serializable;

public class CodeFillInOptionStatementAnswerDto implements Serializable {

    private Integer sequence;
    private Integer optionSequence;
    private Integer optionId;

    public CodeFillInOptionStatementAnswerDto() {
    }

    public CodeFillInOptionStatementAnswerDto(CodeFillInOption option) {
        this.sequence = option.getFillInSpot().getSequence();
        this.optionSequence = option.getSequence();
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

    public Integer getOptionSequence() {
        return optionSequence;
    }

    public void setOptionSequence(Integer optionSequence) {
        this.optionSequence = optionSequence;
    }
}