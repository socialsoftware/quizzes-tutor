package pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer;

import java.io.Serializable;

public class CorrectAnswerDto implements Serializable {
    private Integer sequence;

    private CorrectAnswerDetailsDto correctAnswerDetails;

    public CorrectAnswerDto(QuestionAnswer questionAnswer) {
        this.sequence = questionAnswer.getSequence();
        this.correctAnswerDetails = questionAnswer.getQuestion().getCorrectAnswerDetailsDto();
    }

    public Integer getSequence() {
        return sequence;
    }

    public void setSequence(Integer sequence) {
        this.sequence = sequence;
    }

    public CorrectAnswerDetailsDto getCorrectAnswerDetails() {
        return correctAnswerDetails;
    }

    public void setCorrectAnswerDetails(CorrectAnswerDetailsDto correctAnswerDetails) {
        this.correctAnswerDetails = correctAnswerDetails;
    }

    @Override
    public String toString() {
        return "CorrectAnswerDto{" +
                "sequence=" + sequence +
                ", correctAnswer=" + correctAnswerDetails +
                '}';
    }
}
