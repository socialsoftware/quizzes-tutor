package com.example.tutor.option;

import java.io.Serializable;


public class StudentOptionDTO implements Serializable {

    private Integer option;
    private String content;

    public StudentOptionDTO(Option option) {
        this.option = option.getOption();
        this.content = option.getContent();
    }

    public Integer getOption() {
        return option;
    }

    public void setOption(Integer option) {
        this.option = option;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

}