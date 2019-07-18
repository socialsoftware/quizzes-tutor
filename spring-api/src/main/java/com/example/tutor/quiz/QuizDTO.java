package com.example.tutor.quiz;

import com.example.tutor.question.QuestionDTO;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class QuizDTO implements Serializable {
    private Integer id;
    private String title;
    private LocalDateTime date;
    private Integer year;
    private String type;
    private Integer series;
    private String version;
    private Integer generated_by;
    private Boolean completed;
    private List<QuestionDTO> questions = new ArrayList<>();

    public QuizDTO(){

    }

    public QuizDTO(Quiz quiz) {
        this.id = quiz.getId();
        this.title = quiz.getTitle();
        this.date = quiz.getDate();
        this.year = quiz.getYear();
        this.type = quiz.getType();
        this.series = quiz.getSeries();
        this.version = quiz.getVersion();
        this.generated_by = quiz.getGenerated_by();
        this.completed = quiz.getCompleted();
        this.questions = quiz.getQuestions().stream().map(QuestionDTO::new).collect(Collectors.toList());
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getSeries() {
        return series;
    }

    public void setSeries(Integer series) {
        this.series = series;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Integer getGenerated_by() {
        return generated_by;
    }

    public void setGenerated_by(Integer generated_by) {
        this.generated_by = generated_by;
    }

    public Boolean getCompleted() {
        return completed;
    }

    public void setCompleted(Boolean completed) {
        this.completed = completed;
    }

    public List<QuestionDTO> getQuestions() {
        return questions;
    }

    public void setQuestions(List<QuestionDTO> questions) {
        this.questions = questions;
    }
}