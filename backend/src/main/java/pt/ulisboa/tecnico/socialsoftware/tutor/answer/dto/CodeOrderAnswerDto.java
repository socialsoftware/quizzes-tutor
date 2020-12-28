package pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.CodeOrderAnswer;

import java.util.List;
import java.util.stream.Collectors;

public class CodeOrderAnswerDto extends AnswerDetailsDto {
    private List<CodeOrderAnswerOrderedSlotDto> orderedSlots;

    public CodeOrderAnswerDto() {
    }

    public CodeOrderAnswerDto(CodeOrderAnswer answer) {
        if (answer.getOrderedSlots() != null)
            this.orderedSlots = answer.getOrderedSlots().stream().map(CodeOrderAnswerOrderedSlotDto::new).collect(Collectors.toList());
    }

    public List<CodeOrderAnswerOrderedSlotDto> getOrderedSlots() {
        return orderedSlots;
    }

    public void setOrderedSlots(List<CodeOrderAnswerOrderedSlotDto> orderedSlots) {
        this.orderedSlots = orderedSlots;
    }
}
