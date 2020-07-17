package pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer;

import java.io.Serializable;

public class CorrectAnswerDto implements Serializable {
    private Integer sequence;

    private CorrectAnswerDetailsDto correctAnswer;

    public CorrectAnswerDto(QuestionAnswer questionAnswer) {
        this.sequence = questionAnswer.getSequence();
        this.correctAnswer = questionAnswer.getQuestion().getCorrectAnswerDto();
    }

    public Integer getSequence() {
        return sequence;
    }

    public void setSequence(Integer sequence) {
        this.sequence = sequence;
    }

    public CorrectAnswerDetailsDto getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(CorrectAnswerDetailsDto correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    @Override
    public String toString() {
        return "CorrectAnswerDto{" +
                "sequence=" + sequence +
                ", correctAnswer=" + correctAnswer +
                '}';
    }
}
