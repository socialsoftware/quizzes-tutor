package com.example.tutor.answer;

import com.example.tutor.question.Question;
import com.example.tutor.user.User;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity(name = "Answer")
@Table(name = "answers")
public class Answer implements Serializable {
    @EmbeddedId
    private AnswerPK answerPK;

    @Column(columnDefinition = "quiz_id")
    private Integer quiz_id;

    @Column(columnDefinition = "time_taken")
    private LocalDateTime time_taken;

    @Column(columnDefinition = "option")
    private Integer option;

    public Answer() {

    }

    public Answer(AnswerDTO ans, User s, Question q){
        this.answerPK = new AnswerPK(ans.getDate(), s, q);
        this.answerPK.setUser(s);
        this.quiz_id = ans.getQuiz_id();
        this.time_taken = ans.getTime_taken();
        this.option = ans.getOption();
    }


    public AnswerPK getAnswerPK() {
        return answerPK;
    }

    public void setAnswerPK(AnswerPK answerPK) {
        this.answerPK = answerPK;
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

    public Integer getQuestion_id() {
        return answerPK.getQuestion().getId();
    }

    public void setQuestion_id(Integer id) {
        answerPK.getQuestion().setId(id);
    }

    public LocalDateTime getDate() {
        return answerPK.getDate();
    }

    public void setDate(LocalDateTime date) {
        answerPK.setDate(date);
    }

    public Integer getUser_id() {
        return answerPK.getUser().getId();
    }

    public void setUser_id(Integer id) {
        answerPK.getUser().setId(id);
    }

}