package com.example.tutor.question;

import com.example.tutor.image.Image;
import com.example.tutor.image.ImageDTO;
import com.example.tutor.option.Option;
import com.example.tutor.option.OptionDTO;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


public class QuestionDTO implements Serializable {
    private String content;
    private Integer difficulty;
    private Boolean active;
    private List<OptionDTO> options;
    private ImageDTO image;

    public QuestionDTO() {

    }

    public QuestionDTO(Question question) {
        this.content = question.getContent();
        this.difficulty = question.getDifficulty();
        this.active = question.getActive();
        if (question.getImage() != null) {
            this.image = new ImageDTO(question.getImage());
        }
        this.options = question.getOptions().stream().map(OptionDTO::new).collect(Collectors.toList());
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(Integer difficulty) {
        this.difficulty = difficulty;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public List<OptionDTO> getOptions() {
        return options;
    }

    public void setOptions(List<OptionDTO> options) {
        this.options = options;
    }

    public ImageDTO getImage() {
        return image;
    }

    public void setImage(ImageDTO image) {
        this.image = image;
    }
}