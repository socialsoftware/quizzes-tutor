package pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.domain;

import jakarta.persistence.Embeddable;

import java.time.LocalDateTime;

@Embeddable
public class RemovedDifficultQuestion {
    private Integer questionId;

    private LocalDateTime removedDate;

    public RemovedDifficultQuestion() {
    }

    public RemovedDifficultQuestion(Integer questionId, LocalDateTime removedDate) {
        this.questionId = questionId;
        this.removedDate = removedDate;
    }

    public Integer getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Integer questionId) {
        this.questionId = questionId;
    }

    public LocalDateTime getRemovedDate() {
        return removedDate;
    }

    public void setRemovedDate(LocalDateTime removedDate) {
        this.removedDate = removedDate;
    }
}
