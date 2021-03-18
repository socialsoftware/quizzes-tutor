package pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.CodeOrderSlotStatementAnswerDetailsDto;

import javax.persistence.Embeddable;

@Embeddable
public class CodeOrderSlotAnswerItem {
    private Integer slotId;
    private Integer assignedOrder;

    public CodeOrderSlotAnswerItem() {}

    public CodeOrderSlotAnswerItem(CodeOrderSlotStatementAnswerDetailsDto codeOrderSlotStatementAnswerDto) {
        slotId = codeOrderSlotStatementAnswerDto.getSlotId();
        assignedOrder = codeOrderSlotStatementAnswerDto.getOrder();
    }

    public Integer getSlotId() {
        return slotId;
    }

    public void setSlotId(Integer slotId) {
        this.slotId = slotId;
    }

    public Integer getAssignedOrder() {
        return assignedOrder;
    }

    public void setAssignedOrder(Integer assignedOrder) {
        this.assignedOrder = assignedOrder;
    }
}
