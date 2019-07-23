package com.example.tutor.image;

import com.example.tutor.question.Question;

import javax.persistence.*;
import java.io.Serializable;

@Entity(name = "Image")
@Table(name = "images")
public class Image implements Serializable {

    @Id
    @GeneratedValue
    private Integer id;

    @OneToOne
    @JoinColumn(name="question_id")
    private Question question;

    @Column(columnDefinition = "url")
    private String url;

    @Column(columnDefinition = "width")
    private Integer width;

    public Image() {}

    public Image(ImageDTO imageDTO) {
        this.url = imageDTO.getUrl();
        this.width = imageDTO.getWidth();
    }

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

}