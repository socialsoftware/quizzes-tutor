package com.example.tutor.answer;

import com.example.tutor.question.QuestionDTO;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;


public class ResultAnswerDTO implements Serializable {

    private Integer questionId;
    private Integer option;
    private LocalDateTime timeTaken;

    public ResultAnswerDTO(){

    }

    public ResultAnswerDTO(Answer answer) {
        this.questionId = answer.getQuestionId();
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