package pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.CodeOrderAnswerOrderedSlot;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.CodeOrderSlot;

public class CodeOrderAnswerOrderedSlotDto {
    private Integer spotId;
    private Integer order;
    private boolean correct;

    public CodeOrderAnswerOrderedSlotDto(CodeOrderSlot correctSlot) {
        spotId = correctSlot.getId();
        order = correctSlot.getOrder();
        correct = true;
    }

    public CodeOrderAnswerOrderedSlotDto(CodeOrderAnswerOrderedSlot answerOrderedSlot) {
        spotId = answerOrderedSlot.getCodeOrderSlot().getId();
        order = answerOrderedSlot.getAssignedOrder();
        correct = answerOrderedSlot.isCorrect();
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

    public boolean isCorrect() {
        return correct;
    }

    public void setCorrect(boolean correct) {
        this.correct = correct;
    }
}
