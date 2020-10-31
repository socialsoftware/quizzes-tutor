package pt.ulisboa.tecnico.socialsoftware.tutor.statement.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.AnswerDetails;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.CodeFillInAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.statement.domain.CodeFillInAnswerItem;
import pt.ulisboa.tecnico.socialsoftware.tutor.statement.domain.QuestionAnswerItem;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CodeFillInStatementAnswerDetailsDto extends StatementAnswerDetailsDto {
    private List<CodeFillInOptionStatementAnswerDto> selectedOptions;

    public CodeFillInStatementAnswerDetailsDto() {
    }

    public CodeFillInStatementAnswerDetailsDto(CodeFillInAnswer questionAnswer) {
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
        return "CodeFillInStatementAnswerDetailsDto{" +
                "selectedOptions=" + selectedOptions +
                '}';
    }

    private CodeFillInAnswer codeFillInAnswer;

    @Override
    public AnswerDetails getAnswerDetails(QuestionAnswer questionAnswer) {
        codeFillInAnswer = new CodeFillInAnswer(questionAnswer);
        questionAnswer.getQuestion().getQuestionDetails().update(this);
        return codeFillInAnswer;
    }

    @Override
    public boolean emptyAnswer() {
        return selectedOptions != null && !selectedOptions.isEmpty();
    }

    @Override
    public QuestionAnswerItem getQuestionAnswerItem(String username, int quizId, StatementAnswerDto statementAnswerDto) {
        return new CodeFillInAnswerItem(username, quizId, statementAnswerDto, this);
    }
}
