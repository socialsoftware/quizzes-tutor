package com.example.tutor.answer;

import com.example.tutor.question.Question;
import com.example.tutor.user.User;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Embeddable
public class AnswerPK implements Serializable {
    @OneToOne
    @JoinColumn(name = "question_id")
    private Question question;

    @Column(name = "answer_date")
    private LocalDateTime date;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    public AnswerPK() {

    }

    public AnswerPK(LocalDateTime d, User s, Question q) {
        this.date = d;
        this.user = s;
        this.question = q;
    }

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        AnswerPK o = (AnswerPK) obj;
        return this.question.getId() == o.getQuestion().getId() && this.user.getId() == o.getUser().getId() && this.date == o.getDate();
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}