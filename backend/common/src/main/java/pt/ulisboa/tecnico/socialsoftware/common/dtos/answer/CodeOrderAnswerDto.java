package pt.ulisboa.tecnico.socialsoftware.common.dtos.answer;

import java.util.ArrayList;
import java.util.List;

public class CodeOrderAnswerDto extends AnswerDetailsDto {
    private List<CodeOrderAnswerSlotDto> orderedSlots = new ArrayList<>();

    public CodeOrderAnswerDto() {
    }

    public List<CodeOrderAnswerSlotDto> getOrderedSlots() {
        return orderedSlots;
    }

    public void setOrderedSlots(List<CodeOrderAnswerSlotDto> orderedSlots) {
        this.orderedSlots = orderedSlots;
    }
}
