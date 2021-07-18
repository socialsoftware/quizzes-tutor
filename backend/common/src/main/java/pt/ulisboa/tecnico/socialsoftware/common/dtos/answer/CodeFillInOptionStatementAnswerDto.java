package pt.ulisboa.tecnico.socialsoftware.common.dtos.answer;

import java.io.Serializable;

public class CodeFillInOptionStatementAnswerDto implements Serializable {

    private Integer sequence;
    private Integer optionSequence;
    private Integer optionId;

    public CodeFillInOptionStatementAnswerDto() {
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