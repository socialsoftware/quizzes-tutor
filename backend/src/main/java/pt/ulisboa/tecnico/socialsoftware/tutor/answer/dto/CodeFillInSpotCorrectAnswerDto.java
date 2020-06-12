package pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.FillInOption;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.FillInSpot;

import java.io.Serializable;

public class CodeFillInSpotCorrectAnswerDto implements Serializable {
    private Integer sequence;
    private Integer optionId;

    public CodeFillInSpotCorrectAnswerDto(FillInSpot fillInSpot) {
        this.sequence = fillInSpot.getSequence();
        this.optionId = fillInSpot.getOptions().stream().filter(FillInOption::isCorrect).findFirst().get().getId();
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
