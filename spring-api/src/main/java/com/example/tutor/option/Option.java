package com.example.tutor.option;

import com.example.tutor.question.Question;
import io.swagger.models.auth.In;

import javax.persistence.*;

import java.io.Serializable;

@Entity(name = "Option")
@Table(name = "options")
public class Option implements Serializable {

    @EmbeddedId
    private OptionPK optionPK;

    @Column(columnDefinition = "correct")
    private Boolean correct;

    @Column(columnDefinition = "content")
    private String content;

    public Integer getQuestion_id() {
        return optionPK.getQuestion().getId();
    }

    public void setQuestion_id(Integer question_id) {
        this.optionPK.getQuestion().setId(question_id);
    }

    public Integer getOption() {
        return optionPK.getOption();
    }

    public void setOption(Integer option) {
        this.optionPK.setOption(option);
    }

    public Boolean getCorrect() {
        return correct;
    }

    public void setCorrect(Boolean correct) {
        this.correct = correct;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}