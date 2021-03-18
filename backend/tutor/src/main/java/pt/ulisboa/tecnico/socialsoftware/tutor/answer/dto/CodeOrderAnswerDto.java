package pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.CodeOrderAnswer;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class CodeOrderAnswerDto extends AnswerDetailsDto {
    private List<CodeOrderAnswerSlotDto> orderedSlots = new ArrayList<>();

    public CodeOrderAnswerDto() {
    }

    public CodeOrderAnswerDto(CodeOrderAnswer answer) {
        if (answer.getOrderedSlots() != null) {
            this.orderedSlots = answer.getOrderedSlots().stream().map(CodeOrderAnswerSlotDto::new).collect(Collectors.toList());
            this.orderedSlots.sort(Comparator.comparing(CodeOrderAnswerSlotDto::getOrder, Comparator.nullsLast(Comparator.naturalOrder())));
        }
    }

    public List<CodeOrderAnswerSlotDto> getOrderedSlots() {
        return orderedSlots;
    }

    public void setOrderedSlots(List<CodeOrderAnswerSlotDto> orderedSlots) {
        this.orderedSlots = orderedSlots;
    }
}
