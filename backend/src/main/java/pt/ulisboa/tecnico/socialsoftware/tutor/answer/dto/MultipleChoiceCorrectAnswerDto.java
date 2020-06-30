package pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.MultipleChoiceQuestionAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.MultipleChoiceQuestion;

public class MultipleChoiceCorrectAnswerDto extends CorrectAnswerDto {
    private Integer correctOptionId;

    public MultipleChoiceCorrectAnswerDto(MultipleChoiceQuestionAnswer questionAnswer) {
        super(questionAnswer);
        this.correctOptionId = questionAnswer.getQuestion().getCorrectOptionId();
    }

    public Integer getCorrectOptionId() {
        return correctOptionId;
    }

    public void setCorrectOptionId(Integer correctOptionId) {
        this.correctOptionId = correctOptionId;
    }

    @Override
    public String toString() {
        return "CorrectAnswerDto{" +
                "correctOptionId=" + getCorrectOptionId() +
                ", sequence=" + getSequence() +
                '}';
    }
}