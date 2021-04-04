package pt.ulisboa.tecnico.socialsoftware.tutor.question.domain;

import pt.ulisboa.tecnico.socialsoftware.dtos.question.*;
import pt.ulisboa.tecnico.socialsoftware.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.*;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.AnswerDetailsDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.CodeOrderAnswerDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.CodeOrderCorrectAnswerDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.CorrectAnswerDetailsDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.Visitor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static pt.ulisboa.tecnico.socialsoftware.exceptions.ErrorMessage.*;

@Entity
@DiscriminatorValue(QuestionTypes.CODE_ORDER_QUESTION)
public class CodeOrderQuestion extends QuestionDetails {

    private Languages language;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "questionDetails", fetch = FetchType.LAZY, orphanRemoval = true)
    private final List<CodeOrderSlot> codeOrderSlots = new ArrayList<>();


    public CodeOrderQuestion() {
        super();
    }

    public CodeOrderQuestion(Question question, CodeOrderQuestionDto codeOrderQuestionDto) {
        super(question);
        update(codeOrderQuestionDto);
    }

    public Languages getLanguage() {
        return language;
    }

    public void setLanguage(Languages language) {
        this.language = language;
    }

    public List<CodeOrderSlot> getCodeOrderSlots() {
        return codeOrderSlots;
    }

    private void setCodeOrderSlots(List<CodeOrderSlotDto> codeOrderSlots) {
        if (codeOrderSlots.isEmpty()) {
            throw new TutorException(AT_LEAST_THREE_SLOTS_NEEDED);
        }
        if (codeOrderSlots.stream().filter(x -> x.getOrder() != null).count() < 3 ){
            throw new TutorException(AT_LEAST_THREE_SLOTS_NEEDED);
        }

        // Ensures some randomization when creating the slots ids.
        Collections.shuffle(codeOrderSlots);

        var sequence = 1;

        for (var codeOrderSlotDto : codeOrderSlots) {

            // The codeOrderSlotDto.getSequence() is specially relevant for exp-imp process.
            int newSequence = codeOrderSlotDto.getSequence() != null ? codeOrderSlotDto.getSequence() : sequence++;

            if (codeOrderSlotDto.getId() == null) {
                CodeOrderSlot codeOrderSlot = new CodeOrderSlot(codeOrderSlotDto);
                codeOrderSlot.setQuestionDetails(this);
                codeOrderSlot.setSequence(newSequence);
                this.codeOrderSlots.add(codeOrderSlot);
            } else {
                CodeOrderSlot codeOrderSlot = getCodeOrderSlots()
                        .stream()
                        .filter(op -> op.getId().equals(codeOrderSlotDto.getId()))
                        .findFirst()
                        .orElseThrow(() -> new TutorException(ORDER_SLOT_NOT_FOUND, codeOrderSlotDto.getId()));

                codeOrderSlot.setContent(codeOrderSlotDto.getContent());
                codeOrderSlot.setOrder(codeOrderSlotDto.getOrder());
                codeOrderSlot.setSequence(newSequence);
            }
        }
    }

    @Override
    public void update(QuestionDetailsDto questionDetailsDto) {
        if (questionDetailsDto instanceof CodeOrderQuestionDto) {
            CodeOrderQuestionDto codeOrderQuestionDto = (CodeOrderQuestionDto) questionDetailsDto;
            setLanguage(codeOrderQuestionDto.getLanguage());
            setCodeOrderSlots(codeOrderQuestionDto.getCodeOrderSlots());
        }
        else {
            throw new TutorException(INVALID_QUESTION_DETAILS_DTO, questionDetailsDto.getClass().getName());
        }

    }


    @Override
    public CorrectAnswerDetailsDto getCorrectAnswerDetailsDto() {
        return new CodeOrderCorrectAnswerDto(this);
    }

    @Override
    public StatementQuestionDetailsDto getStatementQuestionDetailsDto() {
        return new CodeOrderStatementQuestionDetailsDto(this);
    }

    @Override
    public StatementAnswerDetailsDto getEmptyStatementAnswerDetailsDto() {
        return new CodeOrderStatementAnswerDetailsDto();
    }

    @Override
    public AnswerDetailsDto getEmptyAnswerDetailsDto() {
        return new CodeOrderAnswerDto();
    }

    @Override
    public QuestionDetailsDto getQuestionDetailsDto() {
        return this.getDto();
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
        return this.codeOrderSlots.stream()
                .filter(codeOrderSlot -> codeOrderSlot.getOrder() != null)
                .sorted(Comparator.comparing(CodeOrderSlot::getOrder))
                .map(codeOrderSlot -> String.valueOf(codeOrderSlot.getSequence()))
                .collect(Collectors.joining(" | "));
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

    public CodeOrderSlot getCodeOrderSlotBySlotId(Integer slotId) {
        return this.codeOrderSlots
                .stream()
                .filter(slot1 -> slot1.getId().equals(slotId))
                .findAny()
                .orElseThrow(() -> new TutorException(QUESTION_ORDER_SLOT_MISMATCH, slotId));
    }

    @Override
    public String getAnswerRepresentation(List<Integer> selectedIds) {
        return selectedIds.stream()
                .map(x -> String.valueOf(this.codeOrderSlots.stream().filter(co -> co.getId().equals(x)).findAny().get().getSequence()))
                .collect(Collectors.joining(" | "));
    }

    public CodeOrderQuestionDto getDto() {
        CodeOrderQuestionDto dto = new CodeOrderQuestionDto();
        dto.setLanguage(getLanguage());
        dto.setCodeOrderSlots(
                getCodeOrderSlots()
                .stream()
                .map(CodeOrderSlot::getDto)
                .collect(Collectors.toList()));
        dto.getCodeOrderSlots().sort(Comparator.comparing(CodeOrderSlotDto::getOrder, Comparator.nullsLast(Comparator.naturalOrder())));
        return dto;
    }
}
