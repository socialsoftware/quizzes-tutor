package pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.QuizDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserDto;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Set;

public class QuizAnswerDto {
    private Integer id;
    private LocalDateTime answerDate;
    private boolean completed;
    private QuizDto quiz;
    private String username;

    public QuizAnswerDto() {
    }

    public QuizAnswerDto(QuizAnswer quizAnswer) {
        this.id = quizAnswer.getId();
        this.answerDate = quizAnswer.getAnswerDate();
        this.completed = quizAnswer.getCompleted();
        this.quiz = new QuizDto(quizAnswer.getQuiz(), false);
        this.username = quizAnswer.getUser().getUsername();
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

}
