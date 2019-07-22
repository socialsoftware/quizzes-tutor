package com.example.tutor.option;

import com.example.tutor.question.Question;

import javax.persistence.*;
import java.io.Serializable;

@Embeddable
public class OptionKey implements Serializable {
    @Column(name = "option")
    private Integer option;

    @OneToOne
    @JoinColumn(name = "question_id")
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

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        OptionKey o = (OptionKey) obj;
        return this.question.getId() == o.getQuestion().getId() && this.option == o.getOption();
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}