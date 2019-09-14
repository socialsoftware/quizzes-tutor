package pt.ulisboa.tecnico.socialsoftware.tutor.statement.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class StatementQuizDto implements Serializable {
    private Integer quizAnswerId;
    private String title;
    private String availableDate;
    private String conclusionDate;
    private List<StatementQuestionDto> questions = new ArrayList<>();

    public StatementQuizDto(){
    }

    public StatementQuizDto(QuizAnswer quizAnswer) {
        this.quizAnswerId = quizAnswer.getId();
        this.title = quizAnswer.getQuiz().getTitle();
        if (quizAnswer.getQuiz().getAvailableDate() != null) {
            this.availableDate = quizAnswer.getQuiz().getAvailableDate().toString();
        }
        if (quizAnswer.getQuiz().getConclusionDate() != null) {
            this.conclusionDate = quizAnswer.getQuiz().getConclusionDate().toString();
        }
        this.questions = quizAnswer.getQuiz().getQuizQuestions().stream()
                .sorted(Comparator.comparing(QuizQuestion::getSequence))
                .map(StatementQuestionDto::new)
                .collect(Collectors.toList());
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

    public List<StatementQuestionDto> getQuestions() {
        return questions;
    }

    public void setQuestions(List<StatementQuestionDto> questions) {
        this.questions = questions;
    }
}