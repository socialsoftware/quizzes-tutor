package pt.ulisboa.tecnico.socialsoftware.tutor.statement.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.CodeOrderAnswerOrderedSlot;

import java.io.Serializable;

public class CodeOrderSlotStatementAnswerDetailsDto implements Serializable {
    private Integer slotId;
    private Integer order;

    public CodeOrderSlotStatementAnswerDetailsDto() {
    }

    public CodeOrderSlotStatementAnswerDetailsDto(CodeOrderAnswerOrderedSlot option) {
        this.order = option.getAssignedOrder();
        this.slotId = option.getId();
    }

    public Integer getSlotId() {
        return slotId;
    }

    public void setSlotId(Integer slotId) {
        this.slotId = slotId;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }
}