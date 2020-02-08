package pt.ulisboa.tecnico.socialsoftware.tutor.statement.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer;

import java.io.Serializable;
import java.time.format.DateTimeFormatter;


public class StatementAnswerDto implements Serializable {
    private Integer id;
    private Integer quizAnswerId;
    private Integer quizQuestionId;
    private String answerDate;
    private Integer timeTaken;
    private Integer sequence;
    private Integer optionId;

    public StatementAnswerDto(){}

    public StatementAnswerDto(QuestionAnswer questionAnswer) {
        this.id = questionAnswer.getId();
        this.quizAnswerId = questionAnswer.getQuizAnswer().getId();
        this.quizQuestionId = questionAnswer.getQuizQuestion().getId();
        this.answerDate = questionAnswer.getQuizAnswer().getAnswerDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));;
        this.timeTaken = questionAnswer.getTimeTaken();
        this.sequence = questionAnswer.getSequence();

        if(questionAnswer.getOption() != null) {
            this.optionId = questionAnswer.getOption().getId();
        }
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public String getAnswerDate() {
        return answerDate;
    }

    public void setAnswerDate(String answerDate) {
        this.answerDate = answerDate;
    }

    public Integer getOptionId() {
        return optionId;
    }

    public void setOptionId(Integer optionId) {
        this.optionId = optionId;
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

    @Override
    public String toString() {
        return "StatementAnswerDto{" +
                "id=" + id +
                ", quizAnswerId=" + quizAnswerId +
                ", quizQuestionId=" + quizQuestionId +
                ", answerDate=" + answerDate +
                ", optionId=" + optionId +
                ", timeTaken=" + timeTaken +
                ", sequence=" + sequence +
                '}';
    }
}