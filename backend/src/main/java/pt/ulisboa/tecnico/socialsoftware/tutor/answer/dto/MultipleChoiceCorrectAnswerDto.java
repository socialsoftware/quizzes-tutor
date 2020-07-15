package pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.MultipleChoiceQuestion;

public class MultipleChoiceCorrectAnswerDto extends CorrectAnswerTypeDto {
    private Integer correctOptionId;

    public MultipleChoiceCorrectAnswerDto(MultipleChoiceQuestion question) {
        this.correctOptionId = question.getCorrectOptionId();
    }

    public Integer getCorrectOptionId() {
        return correctOptionId;
    }

    public void setCorrectOptionId(Integer correctOptionId) {
        this.correctOptionId = correctOptionId;
    }

    // TODO[is->has]: update to string.
    /*@Override
    public String toString() {
        return "CorrectAnswerDto{" +
                "correctOptionId=" + getCorrectOptionId() +
                ", sequence=" + getSequence() +
                '}';
    }*/
}