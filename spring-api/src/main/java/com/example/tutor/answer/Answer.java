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

    public Answer(ResultAnswerDTO ans, User user, Question question, LocalDateTime date, Integer quiz_id){
        this.answerPK = new AnswerPK(date, user, question);
        this.answerPK.setUser(user);
        this.quiz_id = quiz_id;
        this.time_taken = ans.getTimeTaken();
        this.option = ans.getOption();
    }


    public AnswerPK getAnswerPK() {
        return answerPK;
    }

    public void setAnswerPK(AnswerPK answerPK) {
        this.answerPK = answerPK;
    }

    public Integer getQuizId() {
        return quiz_id;
    }

    public void setQuizId(Integer quiz_id) {
        this.quiz_id = quiz_id;
    }

    public LocalDateTime getTimeTaken() {
        return time_taken;
    }

    public void setTimeTaken(LocalDateTime time_taken) {
        this.time_taken = time_taken;
    }

    public Integer getOption() {
        return option;
    }

    public void setOption(Integer option) {
        this.option = option;
    }

    public Integer getQuestionId() {
        return answerPK.getQuestion().getId();
    }

    public void setQuestionId(Integer id) {
        answerPK.getQuestion().setId(id);
    }

    public LocalDateTime getDate() {
        return answerPK.getDate();
    }

    public void setDate(LocalDateTime date) {
        answerPK.setDate(date);
    }

    public Integer getUserId() {
        return answerPK.getUser().getId();
    }

    public void setUserId(Integer id) {
        answerPK.getUser().setId(id);
    }

}