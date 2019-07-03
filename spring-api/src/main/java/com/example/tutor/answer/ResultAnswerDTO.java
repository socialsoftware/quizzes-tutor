package com.example.tutor.answer;

import com.example.tutor.question.QuestionDTO;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;


public class ResultAnswerDTO implements Serializable {

    private Integer question_id;
    private Integer option;
    private LocalDateTime time_taken;

    public ResultAnswerDTO(){

    }

    public ResultAnswerDTO(Answer answer) {
        this.question_id = answer.getQuestionId();
        this.option = answer.getOption();
        this.time_taken = answer.getTimeTaken();
    }

    public Integer getQuestionId() {
        return question_id;
    }

    public void setQuestionId(Integer question_id) {
        this.question_id = question_id;
    }

    public Integer getOption() {
        return option;
    }

    public void setOption(Integer option) {
        this.option = option;
    }

    public LocalDateTime getTimeTaken() {
        return time_taken;
    }

    public void setTimeTaken(LocalDateTime time_taken) {
        this.time_taken = time_taken;
    }
}