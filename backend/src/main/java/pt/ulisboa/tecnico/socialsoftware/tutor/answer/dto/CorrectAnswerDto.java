package pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer;

import java.io.Serializable;

public abstract class CorrectAnswerDto implements Serializable {
    private Integer sequence;

    public CorrectAnswerDto(QuestionAnswer questionAnswer) {
        this.sequence = questionAnswer.getSequence();
    }

    public Integer getSequence() {
        return sequence;
    }

    public void setSequence(Integer sequence) {
        this.sequence = sequence;
    }
}
