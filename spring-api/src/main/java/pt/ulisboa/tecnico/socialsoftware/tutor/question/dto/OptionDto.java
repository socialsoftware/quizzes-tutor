package pt.ulisboa.tecnico.socialsoftware.tutor.question.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Option;

import java.io.Serializable;


public class OptionDto implements Serializable {
    private Integer id;
    private Integer number;
    private Boolean correct;
    private String content;

    public OptionDto() {
    }

    public OptionDto(Option option) {
        this.id = option.getId();
        this.number = option.getNumber();
        this.content = option.getContent();
        this.correct = option.getCorrect();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
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