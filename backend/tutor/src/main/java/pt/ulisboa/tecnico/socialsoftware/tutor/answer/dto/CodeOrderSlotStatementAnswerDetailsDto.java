package pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.CodeOrderAnswerSlot;

import java.io.Serializable;

public class CodeOrderSlotStatementAnswerDetailsDto implements Serializable {
    private Integer slotId;
    private Integer order;

    public CodeOrderSlotStatementAnswerDetailsDto() {
    }

    public CodeOrderSlotStatementAnswerDetailsDto(Integer slotId, Integer order) {
        this.slotId = slotId;
        this.order = order;
    }

    public CodeOrderSlotStatementAnswerDetailsDto(CodeOrderAnswerSlot option) {
        this.order = option.getAssignedOrder();
        this.slotId = option.getCodeOrderSlot().getId();
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