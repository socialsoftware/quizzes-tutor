package pt.ulisboa.tecnico.socialsoftware.tutor.quiz.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collectors;

public class QuizDto implements Serializable {
    private Integer id;
    private String title;
    private LocalDateTime date;
    private Integer year;
    private String type;
    private Integer series;
    private String version;
    private Boolean completed;
    private Map<Integer, QuestionDto> questions;

    public QuizDto(){

    }

    public QuizDto(Quiz quiz) {
        this.id = quiz.getId();
        this.title = quiz.getTitle();
        this.date = quiz.getDate();
        this.year = quiz.getYear();
        this.type = quiz.getType();
        this.series = quiz.getSeries();
        this.version = quiz.getVersion();
        this.questions = quiz.getQuizQuestions().entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> new QuestionDto(entry.getValue().getQuestion())));
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

    public Boolean getCompleted() {
        return completed;
    }

    public void setCompleted(Boolean completed) {
        this.completed = completed;
    }

    public Map<Integer, QuestionDto> getQuestions() {
        return questions;
    }

    public void setQuestions(Map<Integer, QuestionDto> questions) {
        this.questions = questions;
    }
}