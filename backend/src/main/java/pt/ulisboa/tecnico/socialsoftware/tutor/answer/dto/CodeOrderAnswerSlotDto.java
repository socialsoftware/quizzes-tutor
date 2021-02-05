package pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.CodeOrderAnswerSlot;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.CodeOrderSlot;

public class CodeOrderAnswerSlotDto {
    private Integer slotId;
    private Integer order;
    private boolean correct;

    public CodeOrderAnswerSlotDto(CodeOrderSlot correctSlot) {
        slotId = correctSlot.getId();
        order = correctSlot.getOrder();
        correct = true;
    }

    public CodeOrderAnswerSlotDto(CodeOrderAnswerSlot answerOrderedSlot) {
        slotId = answerOrderedSlot.getCodeOrderSlot().getId();
        order = answerOrderedSlot.getAssignedOrder();
        correct = answerOrderedSlot.isCorrect();
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

    public boolean isCorrect() {
        return correct;
    }

    public void setCorrect(boolean correct) {
        this.correct = correct;
    }
}
