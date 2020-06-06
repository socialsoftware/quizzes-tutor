package pt.ulisboa.tecnico.socialsoftware.tutor.question.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.FillInSpot;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Option;

import java.io.Serializable;

// TODO CLEAN UP
public class FillInSpotDto implements Serializable {
    private Integer id;
    private Integer sequence;
    private boolean correct;
    private String content;

    public FillInSpotDto() {
    }

    public FillInSpotDto(FillInSpot option) {
        this.id = option.getId();
    }

    public Integer getId() {
        return id;
    }

    public Integer getSequence() {
        return sequence;
    }

    public void setSequence(Integer sequence) {
        this.sequence = sequence;
    }

    public boolean getCorrect() {
        return correct;
    }

    public void setCorrect(boolean correct) {
        this.correct = correct;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "FillInSpotDto {" +
                "id=" + id +
                ", id=" + id +
                ", correct=" + correct +
                ", content='" + content + '\'' +
                '}';
    }
}