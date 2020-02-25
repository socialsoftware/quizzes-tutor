package pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer;

import java.io.Serializable;

public class CorrectAnswerDto implements Serializable {
    private Integer correctOptionId;
    private Integer sequence;

    public CorrectAnswerDto(QuestionAnswer questionAnswer) {
        this.correctOptionId = questionAnswer.getQuizQuestion().getQuestion().getCorrectOptionId();
        this.sequence = questionAnswer.getSequence();
    }

    public Integer getCorrectOptionId() {
        return correctOptionId;
    }

    public void setCorrectOptionId(Integer correctOptionId) {
        this.correctOptionId = correctOptionId;
    }

    public Integer getSequence() {
        return sequence;
    }

    public void setSequence(Integer sequence) {
        this.sequence = sequence;
    }

    @Override
    public String toString() {
        return "CorrectAnswerDto{" +
                "correctOptionId=" + correctOptionId +
                ", sequence=" + sequence +
                '}';
    }
}