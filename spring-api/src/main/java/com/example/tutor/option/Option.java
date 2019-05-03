package com.example.tutor.option;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;

@Entity(name = "Option")
@Table(name = "options")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Option implements Serializable {

    @EmbeddedId
    private OptionPK optionPK;

    @Column(columnDefinition = "correct")
    private Boolean correct;

    @Column(columnDefinition = "content")
    private String content;

    @JsonIgnore
    @JsonProperty(value = "question_id")
    public Integer getQuestion_id() {
        return optionPK.getQuestion().getId();
    }

    public void setQuestion_id(Integer question_id) {
        this.optionPK.getQuestion().setId(question_id);
    }

    @JsonIgnore
    @JsonProperty(value = "option")
    public Integer getOption() {
        return optionPK.getOption();
    }

    public void setOption(Integer option) {
        this.optionPK.setOption(option);
    }

    @JsonIgnore
    @JsonProperty(value = "correct")
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