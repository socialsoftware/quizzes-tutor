package com.example.tutor.question;

import com.example.tutor.option.Option;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "Questions")
public class Question implements Serializable {
    @Id
    @GeneratedValue
    private Integer id;

    @Column(columnDefinition = "content")
    private String content;

    @Column(columnDefinition = "newid")
    private Integer newID;

    @Column(columnDefinition = "difficulty")
    private Integer difficulty;

    private Option[] options;

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

    public Integer getNewID() {
        return newID;
    }

    public void setNewID(Integer newID) {
        this.newID = newID;
    }

    public Integer getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(Integer difficulty) {
        this.difficulty = difficulty;
    }

    public Option[] getOptions() {
        return options;
    }

    public void setOptions(Option[] options) {
        this.options = options;
    }
}