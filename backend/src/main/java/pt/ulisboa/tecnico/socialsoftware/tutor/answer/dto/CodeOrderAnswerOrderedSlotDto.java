package pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.CodeOrderAnswerOrderedSlot;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.CodeOrderSlot;

public class CodeOrderAnswerOrderedSlotDto {
    private Integer spotId;
    private Integer order;

    public CodeOrderAnswerOrderedSlotDto(CodeOrderSlot correctSlot) {
        spotId = correctSlot.getId();
        spotId = correctSlot.getOrder();
    }

    public CodeOrderAnswerOrderedSlotDto(CodeOrderAnswerOrderedSlot answerOrderedSlot) {
        spotId = answerOrderedSlot.getCodeOrderSlot().getId();
        order = answerOrderedSlot.getAssignedOrder();
    }

    public Integer getSpotId() {
        return spotId;
    }

    public void setSpotId(Integer spotId) {
        this.spotId = spotId;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }
}
