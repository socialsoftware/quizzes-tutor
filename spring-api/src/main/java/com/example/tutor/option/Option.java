package com.example.tutor.option;

import com.example.tutor.question.Question;
import io.swagger.models.auth.In;

import javax.persistence.*;

import java.io.Serializable;

@Entity
@Table(name = "Options")
public class Option implements Serializable {

    @EmbeddedId
    private OptionPK optionPK;

    @Column(columnDefinition = "iscorrect")
    private Boolean isCorrect;

    @Column(columnDefinition = "content")
    private String content;

    public Integer getQuestionID() {
        return optionPK.getQuestion().getId();
    }

    public void setQuestionID(Integer questionID) {
        this.optionPK.getQuestion().setId(questionID);
    }

    public Integer getOption() {
        return optionPK.getOption();
    }

    public void setOption(Integer option) {
        this.optionPK.setOption(option);
    }

    public Boolean getCorrect() {
        return isCorrect;
    }

    public void setCorrect(Boolean correct) {
        isCorrect = correct;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}