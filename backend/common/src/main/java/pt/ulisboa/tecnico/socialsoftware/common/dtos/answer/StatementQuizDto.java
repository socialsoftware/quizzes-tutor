package pt.ulisboa.tecnico.socialsoftware.common.dtos.answer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class StatementQuizDto implements Serializable {
    private Integer id;
    private Integer quizAnswerId;
    private String title;
    private boolean oneWay;
    private boolean timed;
    private String availableDate;
    private String conclusionDate;
    private Long timeToAvailability;
    private Long timeToSubmission;
    private List<StatementQuestionDto> questions = new ArrayList<>();
    private List<StatementAnswerDto> answers = new ArrayList<>();

    public StatementQuizDto() {}

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public Integer getQuizAnswerId() {
        return quizAnswerId;
    }

    public void setQuizAnswerId(Integer quizAnswerId) {
        this.quizAnswerId = quizAnswerId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isOneWay() {
        return oneWay;
    }

    public void setOneWay(boolean oneWay) {
        this.oneWay = oneWay;
    }

    public boolean isTimed() {
        return timed;
    }

    public void setTimed(boolean timed) {
        this.timed = timed;
    }

    public String getAvailableDate() {
        return availableDate;
    }

    public void setAvailableDate(String availableDate) {
        this.availableDate = availableDate;
    }

    public String getConclusionDate() {
        return conclusionDate;
    }

    public void setConclusionDate(String conclusionDate) {
        this.conclusionDate = conclusionDate;
    }

    public Long getTimeToAvailability() {
        return timeToAvailability;
    }

    public void setTimeToAvailability(Long timeToAvailability) {
        this.timeToAvailability = timeToAvailability;
    }

    public Long getTimeToSubmission() {
        return timeToSubmission;
    }

    public void setTimeToSubmission(Long timeToSubmission) {
        this.timeToSubmission = timeToSubmission;
    }

    public List<StatementQuestionDto> getQuestions() {
        return questions;
    }

    public void setQuestions(List<StatementQuestionDto> questions) {
        this.questions = questions;
    }

    public List<StatementAnswerDto> getAnswers() {
        return answers;
    }

    public void setAnswers(List<StatementAnswerDto> answers) {
        this.answers = answers;
    }

    @Override
    public String toString() {
        return "StatementQuizDto{" +
                "id=" + id +
                ", quizAnswerId=" + quizAnswerId +
                ", title='" + title + '\'' +
                ", oneWay=" + oneWay +
                ", availableDate='" + availableDate + '\'' +
                ", conclusionDate='" + conclusionDate + '\'' +
                ", timeToAvailability=" + timeToAvailability +
                ", timeToSubmission=" + timeToSubmission +
                ", questions=" + questions +
                ", answers=" + answers +
                '}';
    }
}