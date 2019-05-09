package com.example.tutor.answer;

import com.example.tutor.question.QuestionDTO;

import java.io.Serializable;
import java.util.List;


public class Result implements Serializable {

    private List<QuestionDTO> answers;

    public Result(List<QuestionDTO> answers) {
        this.answers = answers;
    }

    public List<QuestionDTO> getAnswers() {
        return answers;
    }

    public void setAnswers(List<QuestionDTO> answers) {
        this.answers = answers;
    }
}