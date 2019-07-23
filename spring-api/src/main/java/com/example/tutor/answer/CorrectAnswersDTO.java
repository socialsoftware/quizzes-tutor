package com.example.tutor.answer;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class CorrectAnswersDTO implements Serializable {

    private Map<Integer, CorrectAnswerDTO> answers;

    CorrectAnswersDTO(Map<Integer, CorrectAnswerDTO> answers) {
        this.answers = answers;
    }

    public Map<Integer, CorrectAnswerDTO> getAnswers() {
        return answers;
    }

    public void setAnswers(Map<Integer, CorrectAnswerDTO> answers) {
        this.answers = answers;
    }
}