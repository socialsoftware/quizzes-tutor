package pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.CodeFillInOption;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.CodeFillInSpot;

import java.io.Serializable;

public class CodeFillInSpotCorrectAnswerDto implements Serializable {
    private Integer sequence;
    private Integer optionId;

    public CodeFillInSpotCorrectAnswerDto(CodeFillInSpot codeFillInSpot) {
        this.sequence = codeFillInSpot.getSequence();
        this.optionId = codeFillInSpot.getOptions().stream().filter(CodeFillInOption::isCorrect).findFirst().get().getId();
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
