package pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.AnswerDetails;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.CodeFillInAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.CodeFillInQuestion;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.CodeFillInAnswerItem;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswerItem;

import javax.persistence.Transient;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CodeFillInStatementAnswerDetailsDto extends StatementAnswerDetailsDto {
    private List<CodeFillInOptionStatementAnswerDto> selectedOptions = new ArrayList<>();

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

    @Transient
    private CodeFillInAnswer codeFillInAnswer;

    @Override
    public AnswerDetails getAnswerDetails(QuestionAnswer questionAnswer) {
        codeFillInAnswer = new CodeFillInAnswer(questionAnswer);
        questionAnswer.getQuestion().getQuestionDetails().update(this);
        return codeFillInAnswer;
    }

    @Override
    public void update(CodeFillInQuestion question) {
        codeFillInAnswer.setFillInOptions(question, this);
    }

    @Override
    public boolean emptyAnswer() {
        return selectedOptions == null || selectedOptions.isEmpty();
    }

    @Override
    public QuestionAnswerItem getQuestionAnswerItem(String username, int quizId, StatementAnswerDto statementAnswerDto) {
        return new CodeFillInAnswerItem(username, quizId, statementAnswerDto, this);
    }
}
