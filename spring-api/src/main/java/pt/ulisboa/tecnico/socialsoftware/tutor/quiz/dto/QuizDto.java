package pt.ulisboa.tecnico.socialsoftware.tutor.quiz.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class QuizDto implements Serializable {
    private Integer id;
    private Integer number;
    private String title;
    private LocalDateTime date;
    private Integer year;
    private String type;
    private Integer series;
    private String version;
    private List<QuestionDto> questions;

    public QuizDto(){
    }

    public QuizDto(Quiz quiz, boolean deepCopy) {
        this.id = quiz.getId();
        this.number = quiz.getNumber();
        this.title = quiz.getTitle();
        this.date = quiz.getDate();
        this.year = quiz.getYear();
        this.type = quiz.getType();
        this.series = quiz.getSeries();
        this.version = quiz.getVersion();
        if (deepCopy) {
            this.questions = quiz.getQuizQuestions().stream()
                    .sorted(Comparator.comparing(QuizQuestion::getSequence))
                    .map(quizQuestion -> new QuestionDto(quizQuestion.getQuestion()))
                    .collect(Collectors.toList());
        }
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

    public List<QuestionDto> getQuestions() {
        return questions;
    }

    public void setQuestions(List<QuestionDto> questions) {
        this.questions = questions;
    }
}