package pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.AnswerDetailsDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.CodeOrderAnswerDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.Visitor;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.*;
import pt.ulisboa.tecnico.socialsoftware.tutor.statement.dto.CodeOrderStatementAnswerDetailsDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.statement.dto.StatementAnswerDetailsDto;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.QUESTION_ORDER_SLOT_MISMATCH;

@Entity
@DiscriminatorValue(Question.QuestionTypes.CODE_ORDER_QUESTION)
public class CodeOrderAnswer extends AnswerDetails {

    @OneToMany(mappedBy = "codeOrderAnswer", fetch = FetchType.EAGER)
    private final Set<CodeOrderAnswerSlot> orderedSlots = new HashSet<>();

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
            var toDelete = new ArrayList<>(this.orderedSlots);
            toDelete.forEach(CodeOrderAnswerSlot::remove);
            this.orderedSlots.clear();
        }
    }

    @Override
    public AnswerDetailsDto getAnswerDetailsDto() {
        return new CodeOrderAnswerDto(this);
    }

    @Override
    public String getAnswerRepresentation() {
        var correctAnswers = this.getOrderedSlots().stream().filter(CodeOrderAnswerSlot::isCorrect).count();
        var questionOptions = ((CodeOrderQuestion) this.getQuestionAnswer().getQuestion().getQuestionDetails()).getCodeOrderSlots().size();
        return String.format("%d/%d", correctAnswers, questionOptions);
    }

    @Override
    public StatementAnswerDetailsDto getStatementAnswerDetailsDto() {
        return new CodeOrderStatementAnswerDetailsDto(this);
    }

    @Override
    public boolean isAnswered() {
        return orderedSlots != null && !orderedSlots.isEmpty();
    }

    public Set<CodeOrderAnswerSlot> getOrderedSlots() {
        return orderedSlots;
    }

    public void setOrderedSlots(CodeOrderQuestion question,
                                CodeOrderStatementAnswerDetailsDto codeOrderStatementAnswerDetailsDto) {
        this.getOrderedSlots().clear();
        if (codeOrderStatementAnswerDetailsDto.emptyAnswer()) {
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
