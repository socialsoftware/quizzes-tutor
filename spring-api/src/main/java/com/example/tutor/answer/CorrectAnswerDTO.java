package com.example.tutor.answer;

import com.example.tutor.question.Question;

import java.io.Serializable;
import java.util.List;

public class CorrectAnswerDTO implements Serializable {

    private Integer question_id;
    private Integer correct_option;

    public CorrectAnswerDTO(Question question) {
        this.question_id = question.getId();
        this.correct_option = question.getCorrectOption();
    }

    public Integer getQuestionId() {
        return question_id;
    }

    public void setQuestionId(Integer question_id) {
        this.question_id = question_id;
    }

    public Integer getCorrectOption() {
        return correct_option;
    }

    public void setCorrectOption(Integer correct_option) {
        this.correct_option = correct_option;
    }
}