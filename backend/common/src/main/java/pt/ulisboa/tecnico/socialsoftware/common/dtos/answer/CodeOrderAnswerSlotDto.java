package pt.ulisboa.tecnico.socialsoftware.common.dtos.answer;

import java.io.Serializable;

public class CodeOrderAnswerSlotDto implements Serializable {
    private Integer slotId;
    private Integer order;
    private boolean correct;
    private Integer sequence;

    public CodeOrderAnswerSlotDto() {
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

    public Integer getSequence() {
        return sequence;
    }

    public void setSequence(Integer sequence) {
        this.sequence = sequence;
    }
}
