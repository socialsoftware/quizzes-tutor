package pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer;

import java.time.LocalDateTime;

public class QuizAnswerDto {
    private Integer id;
    private LocalDateTime availableDate;
    private LocalDateTime assignedDate;
    private LocalDateTime answerDate;
    private Boolean completed;
    // TODO: add more attributes

    public QuizAnswerDto(QuizAnswer quizAnswer) {
        this.id = quizAnswer.getId();
        this.availableDate = quizAnswer.getQuiz().getAvailableDate();
        this.assignedDate = quizAnswer.getAssignedDate();
        this.assignedDate = quizAnswer.getAnswerDate();
        this.completed = quizAnswer.getCompleted();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public LocalDateTime getAvailableDate() {
        return availableDate;
    }

    public void setAvailableDate(LocalDateTime availableDate) {
        this.availableDate = availableDate;
    }

    public LocalDateTime getAssignedDate() {
        return assignedDate;
    }

    public void setAssignedDate(LocalDateTime assignedDate) {
        this.assignedDate = assignedDate;
    }

    public LocalDateTime getAnswerDate() {
        return answerDate;
    }

    public void setAnswerDate(LocalDateTime answerDate) {
        this.answerDate = answerDate;
    }

    public Boolean getCompleted() {
        return completed;
    }

    public void setCompleted(Boolean completed) {
        this.completed = completed;
    }
}
