package pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion;

import java.io.Serializable;

public class CorrectAnswerDto implements Serializable {

    private Integer quizQuestionId;
    private Integer correctOptionId;

    public CorrectAnswerDto(QuizQuestion quizQuestion) {
        this.quizQuestionId = quizQuestion.getId();
        this.correctOptionId = quizQuestion.getQuestion().getCorrectOptionId();
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

    @Override
    public String toString() {
        return "CorrectAnswerDto{" +
                "quizQuestionId=" + quizQuestionId +
                ", correctOptionId=" + correctOptionId +
                '}';
    }
}