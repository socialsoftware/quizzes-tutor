package pt.ulisboa.tecnico.socialsoftware.tutor.statement.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.CodeFillInQuestionAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.MultipleChoiceQuestionAnswer;

public class CodeFillInStatementAnswerDto extends StatementAnswerDto {

    public CodeFillInStatementAnswerDto() {
    }

    public CodeFillInStatementAnswerDto(CodeFillInQuestionAnswer questionAnswer) {
        super(questionAnswer);
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
