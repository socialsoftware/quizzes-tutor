package pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer;

import java.io.Serializable;

public class QuestionAnswerDto implements Serializable {
    private Integer id;
    private Integer timeTaken;
    private Integer sequence;
    private Integer quizAnswerId;
    private Integer quizQuestionId;
    private Integer optionId;


    public QuestionAnswerDto() {}

    public QuestionAnswerDto(QuestionAnswer questionAnswer) {
        this.id = questionAnswer.getId();
        this.timeTaken = questionAnswer.getTimeTaken();
        this.sequence = questionAnswer.getSequence();
        this.quizAnswerId = questionAnswer.getQuizAnswer().getId();
        this.quizQuestionId = questionAnswer.getQuizQuestion().getId();
        if (questionAnswer.getOption() != null) {
            this.optionId = questionAnswer.getOption().getId();
        }
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getTimeTaken() {
        return timeTaken;
    }

    public void setTimeTaken(Integer timeTaken) {
        this.timeTaken = timeTaken;
    }

    public Integer getSequence() {
        return sequence;
    }

    public void setSequence(Integer sequence) {
        this.sequence = sequence;
    }

    public Integer getQuizAnswerId() {
        return quizAnswerId;
    }

    public void setQuizAnswerId(Integer quizAnswerId) {
        this.quizAnswerId = quizAnswerId;
    }

    public Integer getQuizQuestionId() {
        return quizQuestionId;
    }

    public void setQuizQuestionId(Integer quizQuestionId) {
        this.quizQuestionId = quizQuestionId;
    }

    public Integer getOptionId() {
        return optionId;
    }

    public void setOptionId(Integer optionId) {
        this.optionId = optionId;
    }

    @Override
    public String toString() {
        return "QuestionAnswerDto{" +
                "id=" + id +
                ", timeTaken=" + timeTaken +
                ", sequence=" + sequence +
                ", quizAnswerId=" + quizAnswerId +
                ", quizQuestionId=" + quizQuestionId +
                ", optionId=" + optionId +
                '}';
    }
}
