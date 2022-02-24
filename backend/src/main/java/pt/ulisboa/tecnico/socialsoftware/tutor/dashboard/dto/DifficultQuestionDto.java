package pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.dto;

import java.io.Serializable;
import java.time.LocalDateTime;

import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
import pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.domain.DifficultQuestion;

public class DifficultQuestionDto implements Serializable {

    private Integer id;

    private int percentage;

    private LocalDateTime collected;

    private boolean removed;

    private Question question;

    public DifficultQuestionDto(){
    }

    public DifficultQuestionDto(DifficultQuestion difficultQuestion){
        setPercentage(difficultQuestion.getPercentage());
        setCollected(difficultQuestion.getCollected());
        setRemoved(difficultQuestion.isRemoved());
        setQuestion(difficultQuestion.getQuestion());
    }

    public Integer getId() {
        return id;
    }

    public LocalDateTime getCollected() {
        return collected;
    }

    public void setCollected(LocalDateTime collected) {
        this.collected = collected;
    }

    public boolean isRemoved() {
        return removed;
    }

    public void setRemoved(boolean removed) {
        this.removed = removed;
    }

    public int getPercentage() {
        return percentage;
    }

    public void setPercentage(int percentage) {
        this.percentage = percentage;
    }

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    @Override
    public String toString() {
        return "DifficultQuestion{" +
                "id=" + id +
                ", percentage=" + percentage +
                ", removed=" + removed +
                ", question=" + question +
                "}";
    }
}