package pt.ulisboa.tecnico.socialsoftware.common.dtos.answer;

import pt.ulisboa.tecnico.socialsoftware.common.dtos.question.QuestionTypes;

import java.util.ArrayList;
import java.util.List;

public class CodeOrderStatementAnswerDetailsDto extends StatementAnswerDetailsDto {
    private List<CodeOrderSlotStatementAnswerDetailsDto> orderedSlots = new ArrayList<>();

    public CodeOrderStatementAnswerDetailsDto() {
        setType(QuestionTypes.CODE_ORDER_QUESTION);
    }

    public List<CodeOrderSlotStatementAnswerDetailsDto> getOrderedSlots() {
        return orderedSlots;
    }

    public void setOrderedSlots(List<CodeOrderSlotStatementAnswerDetailsDto> orderedSlots) {
        this.orderedSlots = orderedSlots;
    }

    @Override
    public boolean emptyAnswer() {
        return orderedSlots == null || orderedSlots.isEmpty();
    }

    @Override
    public String toString() {
        return "CodeOrderStatementAnswerDetailsDto{" +
                "orderedSlots=" + orderedSlots +
                '}';
    }
}
