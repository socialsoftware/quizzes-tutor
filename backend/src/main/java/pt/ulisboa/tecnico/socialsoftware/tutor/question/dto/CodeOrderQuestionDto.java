package pt.ulisboa.tecnico.socialsoftware.tutor.question.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class CodeOrderQuestionDto extends QuestionDetailsDto {
    private Languages language;

    private List<CodeOrderSlotDto> codeOrderSlots = new ArrayList<>();

    public CodeOrderQuestionDto() {
    }

    public CodeOrderQuestionDto(CodeOrderQuestion question) {
        this.language = question.getLanguage();
        this.codeOrderSlots = question
                .getCodeOrderSlots()
                .stream()
                .map(CodeOrderSlotDto::new)
                .collect(Collectors.toList());
        this.codeOrderSlots.sort(Comparator.comparing(CodeOrderSlotDto::getOrder, Comparator.nullsLast(Comparator.naturalOrder())));
    }

    public Languages getLanguage() {
        return language;
    }

    public void setLanguage(Languages language) {
        this.language = language;
    }

    public List<CodeOrderSlotDto> getCodeOrderSlots() {
        return codeOrderSlots;
    }

    public void setCodeOrderSlots(List<CodeOrderSlotDto> codeOrderSlots) {
        this.codeOrderSlots = codeOrderSlots;
    }

    @Override
    public String toString() {
        return "CodeOrderQuestionDto{" +
                "language='" + language + '\'' +
                ", fillInSpots=" + codeOrderSlots +
                '}';
    }

    @Override
    public QuestionDetails getQuestionDetails(Question question) {
        return new CodeOrderQuestion(question, this);
    }

    @Override
    public void update(CodeOrderQuestion question) {
        question.update(this);
    }
}
