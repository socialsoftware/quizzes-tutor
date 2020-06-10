package pt.ulisboa.tecnico.socialsoftware.tutor.statement.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.CodeFillInQuestionAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.MultipleChoiceQuestionAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.MultipleChoiceQuestion;

public class StatementDtoFactory {

    public static StatementAnswerDto getStatementAnswerDto(QuestionAnswer questionAnswer){
        if (questionAnswer instanceof MultipleChoiceQuestionAnswer){
            return new MultipleChoiceStatementAnswerDto((MultipleChoiceQuestionAnswer)questionAnswer);
        }
        else if (questionAnswer instanceof CodeFillInQuestionAnswer){
            return new CodeFillInStatementAnswerDto((CodeFillInQuestionAnswer)questionAnswer);
        }
        else{
            // todo might be better to throw a custom exception.
            return null;
        }
    }

    public static StatementQuestionDto getStatementQuestionDto(QuestionAnswer questionAnswer){
        if (questionAnswer instanceof MultipleChoiceQuestionAnswer){
            return new MultipleChoiceStatementQuestionDto((MultipleChoiceQuestionAnswer)questionAnswer);
        }
        else if (questionAnswer instanceof CodeFillInQuestionAnswer){
            return new CodeFillInStatementQuestionDto((CodeFillInQuestionAnswer)questionAnswer);
        }
        else{
            // todo might be better to throw a custom exception.
            return null;
        }
    }
}
