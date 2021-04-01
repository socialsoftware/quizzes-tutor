package pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.AnswerDetailsDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.CodeOrderAnswerDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.Visitor;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.*;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.CodeOrderStatementAnswerDetailsDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.StatementAnswerDetailsDto;

import javax.persistence.*;
import java.util.*;
import java.util.stream.Collectors;

@Entity
@DiscriminatorValue(Question.QuestionTypes.CODE_ORDER_QUESTION)
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
        return new CodeOrderAnswerDto(this);
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
        return new CodeOrderStatementAnswerDetailsDto(this);
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
}
