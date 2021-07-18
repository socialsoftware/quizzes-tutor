package pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain;

import pt.ulisboa.tecnico.socialsoftware.common.dtos.answer.*;
import pt.ulisboa.tecnico.socialsoftware.common.dtos.question.QuestionTypes;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.Visitor;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.CodeOrderQuestion;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.CodeOrderSlot;

import javax.persistence.*;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@DiscriminatorValue(QuestionTypes.CODE_ORDER_QUESTION)
public class CodeOrderAnswer extends AnswerDetails {
    @OneToMany(mappedBy = "codeOrderAnswer", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private final Set<CodeOrderAnswerSlot> orderedSlots = new HashSet<>();

    public CodeOrderAnswer() {
        super();
    }

    public CodeOrderAnswer(QuestionAnswer questionAnswer) {
        super(questionAnswer);
    }

    @Override
    public boolean isCorrect() {
        return this.getOrderedSlots().stream().allMatch(CodeOrderAnswerSlot::isCorrect);
    }

    @Override
    public void remove() {
        orderedSlots.forEach(CodeOrderAnswerSlot::remove);
    }

    @Override
    public AnswerDetailsDto getAnswerDetailsDto() {
        return getDto();
    }

    @Override
    public String getAnswerRepresentation() {
        return this.getOrderedSlots().stream()
                .sorted(Comparator.comparing(CodeOrderAnswerSlot::getAssignedOrder))
                .map(x -> x.getCodeOrderSlot().getSequence().toString())
                .collect(Collectors.joining(" | "));
    }

    @Override
    public StatementAnswerDetailsDto getStatementAnswerDetailsDto() {
        return getCodeOrderStatementAnswerDetailsDto();
    }

    @Override
    public boolean isAnswered() {
        return !orderedSlots.isEmpty();
    }

    public Set<CodeOrderAnswerSlot> getOrderedSlots() {
        return orderedSlots;
    }

    public void setOrderedSlots(CodeOrderQuestion question,
                                CodeOrderStatementAnswerDetailsDto codeOrderStatementAnswerDetailsDto) {
        this.orderedSlots.clear();
        if (!codeOrderStatementAnswerDetailsDto.emptyAnswer()) {
            for (var slot : codeOrderStatementAnswerDetailsDto.getOrderedSlots()) {

                CodeOrderSlot orderSlot = question
                        .getCodeOrderSlotBySlotId(slot.getSlotId());

                var answerSlot = new CodeOrderAnswerSlot(orderSlot, this, slot.getOrder());
                this.orderedSlots.add(answerSlot);
            }
        }
    }


    @Override
    public void accept(Visitor visitor) {
        visitor.visitAnswerDetails(this);
    }

    public CodeOrderAnswerDto getDto() {
        CodeOrderAnswerDto dto = new CodeOrderAnswerDto();
        if (getOrderedSlots() != null) {
            dto.setOrderedSlots(getOrderedSlots().stream().map(CodeOrderAnswerSlot::getDto).collect(Collectors.toList()));
            dto.getOrderedSlots().sort(Comparator.comparing(CodeOrderAnswerSlotDto::getOrder, Comparator.nullsLast(Comparator.naturalOrder())));
        }
        return dto;
    }

    public CodeOrderStatementAnswerDetailsDto getCodeOrderStatementAnswerDetailsDto() {
        CodeOrderStatementAnswerDetailsDto dto = new CodeOrderStatementAnswerDetailsDto();
        if (getOrderedSlots() != null) {
            dto.setOrderedSlots(getOrderedSlots()
                    .stream()
                    .map(CodeOrderAnswerSlot::getCodeOrderSlotStatementAnswerDetailsDto)
                    .collect(Collectors.toList()));
            dto.getOrderedSlots().sort(Comparator.comparing(CodeOrderSlotStatementAnswerDetailsDto::getOrder, Comparator.nullsLast(Comparator.naturalOrder())));
        }
        return dto;
    }
}
