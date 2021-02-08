package pt.ulisboa.tecnico.socialsoftware.tutor.quiz.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.utils.DateHandler;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class QuizDto implements Serializable {
    private Integer id;
    private Integer key;
    private boolean scramble;
    private boolean qrCodeOnly;
    private boolean oneWay;
    private boolean timed;
    private String type;
    private String title;
    private String creationDate;
    private String availableDate;
    private String conclusionDate;
    private String resultsDate;
    private Integer series;
    private String version;
    private int numberOfQuestions;
    private int numberOfAnswers;
    private List<QuestionDto> questions = new ArrayList<>();

    public QuizDto(){
    }

    public QuizDto(Quiz quiz, boolean deepCopy) {
        this.id = quiz.getId();
        this.key = quiz.getKey();
        this.scramble = quiz.getScramble();
        this.qrCodeOnly = quiz.isQrCodeOnly();
        this.oneWay = quiz.isOneWay();
        this.title = quiz.getTitle();
        this.timed = quiz.getType().equals(Quiz.QuizType.IN_CLASS);
        this.type = quiz.getType().toString();
        this.series = quiz.getSeries();
        this.version = quiz.getVersion();
        this.numberOfQuestions = quiz.getQuizQuestionsNumber();
        this.numberOfAnswers = quiz.getQuizAnswers().size();
        this.creationDate = DateHandler.toISOString(quiz.getCreationDate());
        this.availableDate = DateHandler.toISOString(quiz.getAvailableDate());
        this.conclusionDate = DateHandler.toISOString(quiz.getConclusionDate());
        this.resultsDate = DateHandler.toISOString(quiz.getResultsDate());

        if (deepCopy) {
            this.questions = quiz.getQuizQuestions().stream()
                    .map(quizQuestion -> {
                       QuestionDto questionDto = new QuestionDto(quizQuestion.getQuestion());
                       questionDto.setSequence(quizQuestion.getSequence());
                       return questionDto;
                    })
                    .collect(Collectors.toList());
        }
    }

    public Integer getId() {
        return id;
    }

    public Integer getKey() {
        return key;
    }

    public void setKey(Integer key) {
        this.key = key;
    }

    public boolean isScramble() {
        return scramble;
    }

    public void setScramble(boolean scramble) {
        this.scramble = scramble;
    }

    public boolean isQrCodeOnly() {
        return qrCodeOnly;
    }

    public void setQrCodeOnly(boolean qrCodeOnly) {
        this.qrCodeOnly = qrCodeOnly;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
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

    public String getResultsDate() {
        return resultsDate;
    }

    public void setResultsDate(String resultsDate) {
        this.resultsDate = resultsDate;
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

    public int getNumberOfQuestions() {
        return numberOfQuestions;
    }

    public void setNumberOfQuestions(int numberOfQuestions) {
        this.numberOfQuestions = numberOfQuestions;
    }

    public int getNumberOfAnswers() {
        return numberOfAnswers;
    }

    public void setNumberOfAnswers(int numberOfAnswers) {
        this.numberOfAnswers = numberOfAnswers;
    }

    public List<QuestionDto> getQuestions() {
        return questions;
    }

    public void setQuestions(List<QuestionDto> questions) {
        this.questions = questions;
    }

    @Override
    public String toString() {
        return "QuizDto{" +
                "id=" + id +
                ", key=" + key +
                ", scramble=" + scramble +
                ", qrCodeOnly=" + qrCodeOnly +
                ", oneWay=" + oneWay +
                ", timed=" + timed +
                ", title='" + title + '\'' +
                ", creationDate='" + creationDate + '\'' +
                ", availableDate='" + availableDate + '\'' +
                ", conclusionDate='" + conclusionDate + '\'' +
                ", resultsDate='" + resultsDate + '\'' +
                ", series=" + series +
                ", version='" + version + '\'' +
                ", numberOfQuestions=" + numberOfQuestions +
                ", numberOfAnswers=" + numberOfAnswers +
                ", questions=" + questions +
                '}';
    }
}