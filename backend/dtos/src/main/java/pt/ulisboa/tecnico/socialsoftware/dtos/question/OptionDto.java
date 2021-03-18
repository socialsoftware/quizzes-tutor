package pt.ulisboa.tecnico.socialsoftware.dtos.question;

import java.io.Serializable;

public class OptionDto implements Serializable {
    private Integer id;
    private Integer sequence;
    private boolean correct;
    private String content;

    public OptionDto() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getSequence() {
        return sequence;
    }

    public void setSequence(Integer sequence) {
        this.sequence = sequence;
    }

    public boolean isCorrect() {
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
        return "OptionDto{" +
                "id=" + id +
                ", correct=" + correct +
                ", content='" + content + '\'' +
                '}';
    }
}