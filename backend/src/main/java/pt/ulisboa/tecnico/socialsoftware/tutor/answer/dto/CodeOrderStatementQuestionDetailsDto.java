package pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.CodeOrderQuestion;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Languages;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class CodeOrderStatementQuestionDetailsDto extends StatementQuestionDetailsDto {
    private Languages language;
    private List<CodeOrderSlotStatementQuestionDetailsDto> orderSlots;

    public CodeOrderStatementQuestionDetailsDto(CodeOrderQuestion question) {
        this.language = question.getLanguage();
        this.orderSlots = question.getCodeOrderSlots().stream().map(CodeOrderSlotStatementQuestionDetailsDto::new).collect(Collectors.toList());
        Collections.shuffle(this.orderSlots);
    }

    public Languages getLanguage() {
        return language;
    }

    public void setLanguage(Languages language) {
        this.language = language;
    }

    public List<CodeOrderSlotStatementQuestionDetailsDto> getOrderSlots() {
        return orderSlots;
    }

    public void setOrderSlots(List<CodeOrderSlotStatementQuestionDetailsDto> orderSlots) {
        this.orderSlots = orderSlots;
    }

    @Override
    public String toString() {
        return "CodeOrderStatementQuestionDetailsDto{" +
                "language='" + language + '\'' +
                ", orderSlots=" + orderSlots +
                '}';
    }
}