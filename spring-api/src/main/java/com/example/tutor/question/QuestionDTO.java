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

    private Integer id;
    private String content;
    private List<OptionDTO> options;
    private ImageDTO image;
    private Integer correct_option;

    public QuestionDTO(Question question) {
        this.id = question.getId();
        this.content = question.getContent();
        if (question.getImage() != null) {
            this.image = new ImageDTO(question.getImage());
        }
        this.options = question.getOptions().stream().map(OptionDTO::new).collect(Collectors.toList());
    }

    public QuestionDTO(Question question, Boolean b) {
        this.id = question.getId();
        Optional<Option> a = question.getOptions().stream().filter(Option::getCorrect).findFirst();
        if(a.isPresent()){
            this.correct_option = a.get().getOption();
        }
    }


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
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

    public Integer getCorrect_option() {
        return correct_option;
    }

    public void setCorrect_option(Integer correct_option) {
        this.correct_option = correct_option;
    }
}