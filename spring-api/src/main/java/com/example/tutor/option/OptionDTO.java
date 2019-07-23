package com.example.tutor.option;

import java.io.Serializable;


public class OptionDTO implements Serializable {
    private Integer option;
    private Boolean correct;
    private String content;

    public OptionDTO() {

    }

    public OptionDTO(Option option) {
        this.option = option.getOption();
        this.content = option.getContent();
        this.correct = option.getCorrect();
    }

    public Integer getOption() {
        return option;
    }

    public void setOption(Integer option) {
        this.option = option;
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