package pt.ulisboa.tecnico.socialsoftware.tutor.question.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Option;

import java.io.Serializable;


public class StudentOptionDto implements Serializable {

    private Integer option;
    private String content;

    public StudentOptionDto(Option option) {
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