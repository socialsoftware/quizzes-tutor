package pt.ulisboa.tecnico.socialsoftware.tutor.statement.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.CodeOrderSlot;

import java.io.Serializable;

public class CodeOrderSlotStatementQuestionDetailsDto implements Serializable {
    private Integer id;
    private Integer order;
    private String content;

    public CodeOrderSlotStatementQuestionDetailsDto(CodeOrderSlot codeOrderSlot) {
        id = codeOrderSlot.getId();
        order = codeOrderSlot.getOrder();
        content = codeOrderSlot.getContent();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
