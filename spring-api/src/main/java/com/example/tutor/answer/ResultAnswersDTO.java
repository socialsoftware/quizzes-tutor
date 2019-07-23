package com.example.tutor.answer;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;


public class ResultAnswersDTO implements Serializable {

    private Integer quiz_id;
    private LocalDateTime answer_date;
    private Map<Integer, ResultAnswerDTO> answers;

    public ResultAnswersDTO(){

    }

    ResultAnswersDTO(Map<Integer, ResultAnswerDTO> answers) {
        this.answers = answers;
    }

    public Integer getQuizId() {
        return quiz_id;
    }

    public void setQuizId(Integer quiz_id) {
        this.quiz_id = quiz_id;
    }

    public LocalDateTime getAnswerDate() {
        return answer_date;
    }

    public void setAnswerDate(LocalDateTime answer_date) {
        this.answer_date = answer_date;
    }

    public Map<Integer, ResultAnswerDTO> getAnswers() {
        return answers;
    }

    public void setAnswers(Map<Integer, ResultAnswerDTO> answers) {
        this.answers = answers;
    }
}
