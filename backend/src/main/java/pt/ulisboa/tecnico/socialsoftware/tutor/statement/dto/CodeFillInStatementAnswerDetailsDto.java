package pt.ulisboa.tecnico.socialsoftware.tutor.statement.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.AnswerDetails;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.CodeFillInAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.MultipleChoiceAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.MultipleChoiceQuestion;
import pt.ulisboa.tecnico.socialsoftware.tutor.statement.domain.MultipleChoiceAnswerItem;
import pt.ulisboa.tecnico.socialsoftware.tutor.statement.domain.QuestionAnswerItem;

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

    @Override
    public AnswerDetails getAnswerDetails(QuestionAnswer questionAnswer) {
        // TODO: IMPLEMENT
        return null;
    }

    @Override
    public boolean emptyAnswer() {
        // TODO: IMPLEMENT
        return false;
    }

    @Override
    public QuestionAnswerItem getQuestionAnswerItem(String username, int quizId, StatementAnswerDto statementAnswerDto) {
        // TODO: IMPLEMENT
        return null;
    }

    @Override
    public void update(MultipleChoiceQuestion question) {
        // TODO: IMPLEMENT
    }
}
