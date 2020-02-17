package pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion;

import java.io.Serializable;

public class CorrectAnswerDto implements Serializable {

    private Integer quizQuestionId;
    private Integer correctOptionId;
    private Integer sequence;

    public CorrectAnswerDto(QuizQuestion quizQuestion) {
        this.quizQuestionId = quizQuestion.getId();
        this.correctOptionId = quizQuestion.getQuestion().getCorrectOptionId();
        this.sequence = quizQuestion.getSequence();
    }

    public Integer getQuizQuestionId() {
        return quizQuestionId;
    }

    public void setQuizQuestionId(Integer quizQuestionId) {
        this.quizQuestionId = quizQuestionId;
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
                "quizQuestionId=" + quizQuestionId +
                ", correctOptionId=" + correctOptionId +
                ", sequence=" + sequence +
                '}';
    }
}