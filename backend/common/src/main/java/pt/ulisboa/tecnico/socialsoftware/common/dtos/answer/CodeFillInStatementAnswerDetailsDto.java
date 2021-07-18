package pt.ulisboa.tecnico.socialsoftware.common.dtos.answer;

import pt.ulisboa.tecnico.socialsoftware.common.dtos.question.QuestionTypes;

import java.util.ArrayList;
import java.util.List;

public class CodeFillInStatementAnswerDetailsDto extends StatementAnswerDetailsDto {
    private List<CodeFillInOptionStatementAnswerDto> selectedOptions = new ArrayList<>();

    public CodeFillInStatementAnswerDetailsDto() {
        setType(QuestionTypes.CODE_FILL_IN_QUESTION);
    }

    public List<CodeFillInOptionStatementAnswerDto> getSelectedOptions() {
        return selectedOptions;
    }

    public void setSelectedOptions(List<CodeFillInOptionStatementAnswerDto> selectedOptions) {
        this.selectedOptions = selectedOptions;
    }

    @Override
    public String toString() {
        return "CodeFillInStatementAnswerDetailsDto{" +
                "selectedOptions=" + selectedOptions +
                '}';
    }

    @Override
    public boolean emptyAnswer() {
        return selectedOptions == null || selectedOptions.isEmpty();
    }
}
