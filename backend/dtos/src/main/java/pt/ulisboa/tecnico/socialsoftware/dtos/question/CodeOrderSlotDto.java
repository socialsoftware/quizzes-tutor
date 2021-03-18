package pt.ulisboa.tecnico.socialsoftware.dtos.question;

import java.io.Serializable;

public class CodeOrderSlotDto implements Serializable {
    private Integer id;
    private String content;
    private Integer order;
    private Integer sequence;

    public CodeOrderSlotDto() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public Integer getSequence() {
        return sequence;
    }

    public void setSequence(Integer sequence) {
        this.sequence = sequence;
    }

    @Override
    public String toString() {
        return "CodeOrderSlotDto{" +
                "id=" + id +
                ", content='" + content + '\'' +
                ", order=" + order +
                ", sequence=" + sequence +
                '}';
    }
}
