package com.example.tutor.question;

import com.example.tutor.image.Image;
import com.example.tutor.option.Option;
import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity(name = "Question")
@Table(name = "questions")
public class Question implements Serializable {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Integer id;

    @Column(columnDefinition = "content")
    private String content;

    @Column(columnDefinition = "new_id")
    private Integer new_id;

    @Column(columnDefinition = "difficulty")
    private Integer difficulty;

    @OneToMany(cascade = { CascadeType.ALL }, fetch = FetchType.EAGER)
    @JoinColumn(name="question_id")
    private List<Option> options = new ArrayList<>();

    @OneToOne(cascade = { CascadeType.ALL }, fetch = FetchType.EAGER, mappedBy = "question")
    @JoinColumn(nullable = true)
    private Image image;


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

    public Integer getNew_id() {
        return new_id;
    }

    public void setNew_id(Integer new_id) {
        this.new_id = new_id;
    }

    public Integer getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(Integer difficulty) {
        this.difficulty = difficulty;
    }

    public List<Option> getOptions() {
        return options;
    }

    public void setOptions(List<Option> options) {
        this.options = options;
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    @Override
    public String toString() {
        return "Question: " + "\n\tContent: " + this.getContent() + "\n\tImage: " + this.getImage().toString();
    }
}