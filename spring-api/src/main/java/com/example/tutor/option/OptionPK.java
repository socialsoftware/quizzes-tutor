package com.example.tutor.option;

import com.example.tutor.question.Question;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.io.Serializable;

@Embeddable
public class OptionPK implements Serializable {
    @Column(name = "option")
    private Integer option;

    @ManyToOne
    @JoinColumn(name = "questionid")
    private Question question;

    public Integer getOption() {
        return option;
    }

    public void setOption(Integer option) {
        this.option = option;
    }

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }
}