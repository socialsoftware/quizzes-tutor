package com.example.tutor.option;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;

@Entity(name = "Option")
@Table(name = "options")
public class Option implements Serializable {

    @EmbeddedId
    private OptionKey optionKey;

    @Column(columnDefinition = "correct")
    private Boolean correct;

    @Column(columnDefinition = "content")
    private String content;

    public Option(){}

    public Option(OptionDTO option) {
        this.optionKey = new OptionKey();
        this.optionKey.setOption(option.getOption());
        this.content = option.getContent();
        this.correct = option.getCorrect();
    }

    public Integer getQuestionId() {
        return optionKey.getQuestion().getId();
    }

    public void setQuestionId(Integer question_id) {
        this.optionKey.getQuestion().setId(question_id);
    }

    public Integer getOption() {
        return optionKey.getOption();
    }

    public void setOption(Integer option) {
        this.optionKey.setOption(option);
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