package com.example.tutor.answer;

import java.io.Serializable;
import java.time.LocalDateTime;

public class AnswerDTO implements Serializable {

    private Integer question_id;
    private LocalDateTime date;
    private Integer student_id;
    private Integer quiz_id;
    private LocalDateTime time_taken;
    private Integer option;

    public AnswerDTO(){

    }

    public AnswerDTO(Answer answer) {
        this.question_id = answer.getQuestion_id();
        this.date = answer.getDate();
        this.student_id = answer.getStudent_id();
        this.question_id = answer.getQuestion_id();
        this.time_taken = answer.getTime_taken();
        this.option = answer.getOption();
    }

    public Integer getQuestion_id() {
        return question_id;
    }

    public void setQuestion_id(Integer question_id) {
        this.question_id = question_id;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public Integer getStudent_id() {
        return student_id;
    }

    public void setStudent_id(Integer student_id) {
        this.student_id = student_id;
    }

    public Integer getQuiz_id() {
        return quiz_id;
    }

    public void setQuiz_id(Integer quiz_id) {
        this.quiz_id = quiz_id;
    }

    public LocalDateTime getTime_taken() {
        return time_taken;
    }

    public void setTime_taken(LocalDateTime time_taken) {
        this.time_taken = time_taken;
    }

    public Integer getOption() {
        return option;
    }

    public void setOption(Integer option) {
        this.option = option;
    }
}