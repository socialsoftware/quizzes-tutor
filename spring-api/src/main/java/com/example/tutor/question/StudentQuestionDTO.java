package com.example.tutor.question;

import com.example.tutor.image.ImageDTO;
import com.example.tutor.option.OptionDTO;
import com.example.tutor.option.StudentOptionDTO;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;


public class StudentQuestionDTO implements Serializable {

    private Integer id;
    private String content;
    private List<StudentOptionDTO> options;
    private ImageDTO image;

    public StudentQuestionDTO(Question question) {
        this.id = question.getId();
        this.content = question.getContent();
        if (question.getImage() != null) {
            this.image = new ImageDTO(question.getImage());
        }
        this.options = question.getOptions().stream().map(StudentOptionDTO::new).collect(Collectors.toList());
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

    public List<StudentOptionDTO> getOptions() {
        return options;
    }

    public void setOptions(List<StudentOptionDTO> options) {
        this.options = options;
    }

    public ImageDTO getImage() {
        return image;
    }

    public void setImage(ImageDTO image) {
        this.image = image;
    }
}