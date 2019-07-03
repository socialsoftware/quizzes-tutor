package com.example.tutor.answer;

import java.io.Serializable;
import java.util.List;

public class CorrectAnswersDTO implements Serializable {

    private List<CorrectAnswerDTO> answers;

    CorrectAnswersDTO(List<CorrectAnswerDTO> answers) {
        this.answers = answers;
    }

    public List<CorrectAnswerDTO> getAnswers() {
        return answers;
    }

    public void setAnswers(List<CorrectAnswerDTO> answers) {
        this.answers = answers;
    }
}