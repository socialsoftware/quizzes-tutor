package pt.ulisboa.tecnico.socialsoftware.tutor.statement.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.CodeFillInQuestionAnswer;

import java.util.List;
import java.util.stream.Collectors;

public class CodeFillInStatementAnswerDto extends StatementAnswerDto {

    private List<CodeFillInOptionStatementAnswerDto> selectedOptions;

    public CodeFillInStatementAnswerDto() {
    }

    public CodeFillInStatementAnswerDto(CodeFillInQuestionAnswer questionAnswer) {
        super(questionAnswer);
        if (questionAnswer.getFillInOptions() != null) {
            this.selectedOptions = questionAnswer.getFillInOptions()
                    .stream().map(CodeFillInOptionStatementAnswerDto::new).collect(Collectors.toList());
        }
    }

    public List<CodeFillInOptionStatementAnswerDto> getSelectedOptions() {
        return selectedOptions;
    }

    public void setSelectedOptions(List<CodeFillInOptionStatementAnswerDto> selectedOptions) {
        this.selectedOptions = selectedOptions;
    }

    @Override
    public String toString() {
        return "StatementAnswerDto{" +
                "timeTaken=" + getTimeTaken() +
                ", sequence=" + getSequence() +
                ", questionAnswerId=" + getQuestionAnswerId() +
                '}';
    }
}
