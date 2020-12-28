package pt.ulisboa.tecnico.socialsoftware.tutor.question.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.AnswerDetailsDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.CorrectAnswerDetailsDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.Visitor;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.Updator;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.CodeOrderQuestionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.CodeOrderSlotDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDetailsDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.statement.dto.StatementAnswerDetailsDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.statement.dto.StatementQuestionDetailsDto;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.AT_LEAST_ONE_OPTION_NEEDED;
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.ORDER_SLOT_NOT_FOUND;

@Entity
@DiscriminatorValue(Question.QuestionTypes.CODE_ORDER_QUESTION)
public class CodeOrderQuestion extends QuestionDetails {

    private String language;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "questionDetails", fetch = FetchType.LAZY, orphanRemoval = true)
    private final List<CodeOrderSlot> codeOrderSlots = new ArrayList<>();


    public CodeOrderQuestion() {
        super();
    }

    public CodeOrderQuestion(Question question, CodeOrderQuestionDto codeOrderQuestionDto) {
        super(question);
        update(codeOrderQuestionDto);
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public List<CodeOrderSlot> getCodeOrderSlots() {
        return codeOrderSlots;
    }

    private void setCodeOrderSlots(List<CodeOrderSlotDto> codeOrderSlots) {
        if (codeOrderSlots.isEmpty()) {
            throw new TutorException(AT_LEAST_ONE_OPTION_NEEDED);
        }

        for (var codeOrderSlotDto : codeOrderSlots) {
            if (codeOrderSlotDto.getId() == null) {
                CodeOrderSlot codeOrderSlot = new CodeOrderSlot(codeOrderSlotDto);
                codeOrderSlot.setQuestionDetails(this);
                this.codeOrderSlots.add(codeOrderSlot);
            } else {
                CodeOrderSlot codeOrderSlot = getCodeOrderSlots()
                        .stream()
                        .filter(op -> op.getId().equals(codeOrderSlotDto.getId()))
                        .findFirst()
                        .orElseThrow(() -> new TutorException(ORDER_SLOT_NOT_FOUND, codeOrderSlotDto.getId()));

                codeOrderSlot.setContent(codeOrderSlotDto.getContent());
                codeOrderSlot.setOrder(codeOrderSlotDto.getOrder());
            }
        }
    }

    public void update(CodeOrderQuestionDto questionDetails) {
        setLanguage(questionDetails.getLanguage());
        setCodeOrderSlots(questionDetails.getCodeOrderSlots());
    }


    @Override
    public CorrectAnswerDetailsDto getCorrectAnswerDetailsDto() {
        return null;
    }

    @Override
    public StatementQuestionDetailsDto getStatementQuestionDetailsDto() {
        return null;
    }

    @Override
    public StatementAnswerDetailsDto getEmptyStatementAnswerDetailsDto() {
        return null;
    }

    @Override
    public AnswerDetailsDto getEmptyAnswerDetailsDto() {
        return null;
    }

    @Override
    public QuestionDetailsDto getQuestionDetailsDto() {
        return null;
    }

    @Override
    public void delete() {
        super.delete();
        for (var slots : this.codeOrderSlots) {
            slots.delete();
        }
        this.codeOrderSlots.clear();
    }

    @Override
    public String getCorrectAnswerRepresentation() {
        return null;
    }

    @Override
    public void update(Updator updator) {
        updator.update(this);
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visitQuestionDetails(this);
    }

    public void visitCodeOrderSlots(Visitor visitor) {
        for (var slot : this.getCodeOrderSlots()) {
            slot.accept(visitor);
        }
    }
}
