package pt.ulisboa.tecnico.socialsoftware.common.dtos.answer;

import pt.ulisboa.tecnico.socialsoftware.common.dtos.answer.StatementQuestionDetailsDto;
import pt.ulisboa.tecnico.socialsoftware.common.dtos.question.Languages;

import java.util.List;

public class CodeOrderStatementQuestionDetailsDto extends StatementQuestionDetailsDto {
    private Languages language;
    private List<CodeOrderSlotStatementQuestionDetailsDto> orderSlots;

    public CodeOrderStatementQuestionDetailsDto() {}

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