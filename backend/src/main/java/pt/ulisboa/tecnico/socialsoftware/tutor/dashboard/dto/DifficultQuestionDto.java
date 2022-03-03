package pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.dto;

import java.io.Serializable;
import java.time.LocalDateTime;

import pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.domain.DifficultQuestion;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto;

public class DifficultQuestionDto implements Serializable {

    private Integer id;

    private int percentage;

    private LocalDateTime collected;

    private boolean removed;

    private QuestionDto questionDto;

    public DifficultQuestionDto(){
    }

    public DifficultQuestionDto(DifficultQuestion difficultQuestion){
        setId(id);
        setPercentage(difficultQuestion.getPercentage());
        setCollected(difficultQuestion.getCollected());
        setRemoved(difficultQuestion.isRemoved());
        setQuestionDto(new QuestionDto(difficultQuestion.getQuestion()));
    }

    public void setId(Integer id) {
        this.id = id;
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

    public QuestionDto getQuestionDto() {
        return questionDto;
    }

    public void setQuestionDto(QuestionDto questionDto) {
        this.questionDto = questionDto;
    }

    @Override
    public String toString() {
        return "DifficultQuestion{" +
                "id=" + id +
                ", percentage=" + percentage +
                ", removed=" + removed +
                ", questionDto=" + questionDto +
                "}";
    }
}