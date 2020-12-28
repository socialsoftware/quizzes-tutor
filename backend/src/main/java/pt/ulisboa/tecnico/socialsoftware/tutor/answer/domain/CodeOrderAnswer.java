package pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.AnswerDetailsDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.Visitor;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.CodeOrderQuestion;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.CodeOrderSlot;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
import pt.ulisboa.tecnico.socialsoftware.tutor.statement.dto.CodeOrderStatementAnswerDetailsDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.statement.dto.StatementAnswerDetailsDto;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.QUESTION_ORDER_SLOT_MISMATCH;

@Entity
@DiscriminatorValue(Question.QuestionTypes.CODE_ORDER_QUESTION)
public class CodeOrderAnswer extends AnswerDetails {

    @OneToMany(mappedBy = "codeOrderAnswer", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private final Set<CodeOrderAnswerOrderedSlot> orderedSlots = new HashSet<>();

    public CodeOrderAnswer() {
        super();
    }

    public CodeOrderAnswer(QuestionAnswer questionAnswer) {
        super(questionAnswer);
    }

    @Override
    public boolean isCorrect() {
        return false;
    }

    @Override
    public void remove() {
        if (this.orderedSlots != null) {
            this.orderedSlots.forEach(CodeOrderAnswerOrderedSlot::remove);
            this.orderedSlots.clear();
        }
    }

    @Override
    public AnswerDetailsDto getAnswerDetailsDto() {
        return null;
    }

    @Override
    public String getAnswerRepresentation() {
        return null;
    }

    @Override
    public StatementAnswerDetailsDto getStatementAnswerDetailsDto() {
        return null;
    }

    @Override
    public boolean isAnswered() {
        return false;
    }

    public Set<CodeOrderAnswerOrderedSlot> getOrderedSlots() {
        return orderedSlots;
    }

    public void setOrderedSlots(CodeOrderQuestion question,
                                CodeOrderStatementAnswerDetailsDto codeOrderStatementAnswerDetailsDto) {
        this.getOrderedSlots().clear();
        if (codeOrderStatementAnswerDetailsDto.emptyAnswer()) {
            for (var slot : codeOrderStatementAnswerDetailsDto.getOrderedSlots()) {

                CodeOrderSlot orderSlot = question
                        .getCodeOrderSlots()
                        .stream()
                        .filter(slot1 -> slot1.getId().equals(slot.getSlotId()))
                        .findAny()
                        .orElseThrow(() -> new TutorException(QUESTION_ORDER_SLOT_MISMATCH, slot.getSlotId()));

                var answerSlot = new CodeOrderAnswerOrderedSlot();
                answerSlot.setCodeOrderSlot(orderSlot);
                answerSlot.setCodeOrderAnswer(this);
                answerSlot.setAssignedOrder(slot.getOrder());
            }
        }
    }


    @Override
    public void accept(Visitor visitor) {

    }
}
