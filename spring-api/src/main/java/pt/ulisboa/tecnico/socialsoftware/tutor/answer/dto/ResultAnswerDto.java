package pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.Answer;

import java.io.Serializable;
import java.time.LocalDateTime;


public class ResultAnswerDto implements Serializable {
    private Integer questionId;
    private Integer option;
    private LocalDateTime timeTaken;

    public ResultAnswerDto(){
    }

    public ResultAnswerDto(Answer answer) {
        this.questionId = answer.getQuizQuestion().getQuestion().getId();
        this.option = answer.getOption();
        this.timeTaken = answer.getTimeTaken();
    }

    public Integer getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Integer questionId) {
        this.questionId = questionId;
    }

    public Integer getOption() {
        return option;
    }

    public void setOption(Integer option) {
        this.option = option;
    }

    public LocalDateTime getTimeTaken() {
        return timeTaken;
    }

    public void setTimeTaken(LocalDateTime timeTaken) {
        this.timeTaken = timeTaken;
    }

}