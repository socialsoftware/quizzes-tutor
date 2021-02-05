package pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.CodeOrderQuestion;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class CodeOrderCorrectAnswerDto extends CorrectAnswerDetailsDto {
    private List<CodeOrderAnswerSlotDto> correctOrder;

    public CodeOrderCorrectAnswerDto(CodeOrderQuestion question) {
        this.correctOrder = question.getCodeOrderSlots()
                .stream()
                .map(CodeOrderAnswerSlotDto::new)
                .collect(Collectors.toList());
        this.correctOrder.sort(Comparator.comparing(CodeOrderAnswerSlotDto::getOrder, Comparator.nullsLast(Comparator.naturalOrder())));
    }

    public List<CodeOrderAnswerSlotDto> getCorrectOrder() {
        return correctOrder;
    }

    public void setCorrectOrder(List<CodeOrderAnswerSlotDto> correctOrder) {
        this.correctOrder = correctOrder;
    }
}