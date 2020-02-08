package pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.dto.QuizDto;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class QuizAnswerDto implements Serializable {
    private Integer id;
    private LocalDateTime answerDate;
    private boolean completed;
    private QuizDto quiz;
    private String username;
    private Set<QuestionAnswerDto> questionAnswers = new HashSet<>();


    public QuizAnswerDto() {
    }

    public QuizAnswerDto(QuizAnswer quizAnswer) {
        this.id = quizAnswer.getId();
        this.answerDate = quizAnswer.getAnswerDate();
        this.completed = quizAnswer.getCompleted();
        this.quiz = new QuizDto(quizAnswer.getQuiz(), false);
        this.username = quizAnswer.getUser().getUsername();
        this.questionAnswers = quizAnswer.getQuestionAnswers().stream().map(QuestionAnswerDto::new).collect(Collectors.toSet());
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public LocalDateTime getAnswerDate() {
        return answerDate;
    }

    public void setAnswerDate(LocalDateTime answerDate) {
        this.answerDate = answerDate;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public QuizDto getQuiz() {
        return quiz;
    }

    public void setQuiz(QuizDto quiz) {
        this.quiz = quiz;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Set<QuestionAnswerDto> getQuestionAnswers() {
        return questionAnswers;
    }

    @Override
    public String toString() {
        return "QuizAnswerDto{" +
                "id=" + id +
                ", answerDate=" + answerDate +
                ", completed=" + completed +
                ", quiz=" + quiz +
                ", username='" + username + '\'' +
                ", questionAnswers=" + questionAnswers +
                '}';
    }

    public void addQuestionAnswer(QuestionAnswerDto questionAnswerDto) {
        this.questionAnswers.add(questionAnswerDto);
    }
}
