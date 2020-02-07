package pt.ulisboa.tecnico.socialsoftware.tutor.statement.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class StatementQuizDto implements Serializable {
    private Integer id;
    private Integer quizAnswerId;
    private String title;
    private String availableDate;
    private String conclusionDate;
    private Long secondsToAvailability;
    private Long secondsToSubmission;
    private List<StatementQuestionDto> questions = new ArrayList<>();
    private List<StatementAnswerDto> answers = new ArrayList<>();

    public StatementQuizDto(){}

    public StatementQuizDto(QuizAnswer quizAnswer) {
        this.id = quizAnswer.getQuiz().getId();
        this.quizAnswerId = quizAnswer.getId();
        this.title = quizAnswer.getQuiz().getTitle();
        if (quizAnswer.getQuiz().getAvailableDate() != null) {
            this.availableDate = String.valueOf(quizAnswer.getQuiz().getAvailableDate());
        }
        if (quizAnswer.getQuiz().getConclusionDate() != null) {
            this.conclusionDate = String.valueOf(quizAnswer.getQuiz().getConclusionDate());
            this.secondsToSubmission = ChronoUnit.SECONDS.between(LocalDateTime.now(), quizAnswer.getQuiz().getConclusionDate());
        }
        this.questions = quizAnswer.getQuiz().getQuizQuestions().stream()
                .sorted(Comparator.comparing(QuizQuestion::getSequence))
                .map(StatementQuestionDto::new)
                .collect(Collectors.toList());

        this.answers = quizAnswer.getQuestionAnswers().stream().map(StatementAnswerDto::new).sorted(Comparator.comparing(StatementAnswerDto::getSequence)).collect(Collectors.toList());
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public Long getSecondsToAvailability() {
        return secondsToAvailability;
    }

    public void setSecondsToAvailability(Long secondsToAvailability) {
        this.secondsToAvailability = secondsToAvailability;
    }

    public Long getSecondsToSubmission() {
        return secondsToSubmission;
    }

    public void setSecondsToSubmission(Long secondsToSubmission) {
        this.secondsToSubmission = secondsToSubmission;
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
                ", availableDate='" + availableDate + '\'' +
                ", conclusionDate='" + conclusionDate + '\'' +
                ", secondsToAvailability=" + secondsToAvailability +
                ", secondsToSubmission=" + secondsToSubmission +
                ", questions=" + questions +
                ", answers=" + answers +
                '}';
    }
}