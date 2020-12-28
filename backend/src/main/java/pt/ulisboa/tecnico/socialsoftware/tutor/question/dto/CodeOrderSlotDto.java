package pt.ulisboa.tecnico.socialsoftware.tutor.question.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.CodeOrderSlot;

import java.io.Serializable;

public class CodeOrderSlotDto implements Serializable {
    private Integer id;
    private String content;
    private Integer order;

    public CodeOrderSlotDto(CodeOrderSlot codeOrderSlot) {
        this.id = codeOrderSlot.getId();
        this.content = codeOrderSlot.getContent();
        this.order = codeOrderSlot.getOrder();
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
}
