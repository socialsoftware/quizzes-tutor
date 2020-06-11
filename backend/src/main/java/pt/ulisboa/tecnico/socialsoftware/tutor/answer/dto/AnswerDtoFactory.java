package pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.CodeFillInQuestionAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.MultipleChoiceQuestionAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.statement.dto.MultipleChoiceStatementAnswerDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.statement.dto.StatementAnswerDto;

public class AnswerDtoFactory {

    public static QuestionAnswerDto getQuestionAnswerDto(QuestionAnswer questionAnswer){
        if (questionAnswer instanceof MultipleChoiceQuestionAnswer){
            return new MultipleChoiceQuestionAnswerDto((MultipleChoiceQuestionAnswer)questionAnswer);
        }
        if (questionAnswer instanceof CodeFillInQuestionAnswer){
            return new CodeFillInQuestionAnswerDto((CodeFillInQuestionAnswer)questionAnswer);
        }
        else{
            // todo might be better to throw a custom exception.
            return null;
        }
    }

    public static CorrectAnswerDto getCorrectAnswerDto(QuestionAnswer questionAnswer){
        if (questionAnswer instanceof MultipleChoiceQuestionAnswer){
            return new MultipleChoiceCorrectAnswerDto((MultipleChoiceQuestionAnswer)questionAnswer);
        }
        if (questionAnswer instanceof CodeFillInQuestionAnswer){
            return new CodeFillInCorrectAnswerDto((CodeFillInQuestionAnswer)questionAnswer);
        }
        else{
            // todo might be better to throw a custom exception.
            return null;
        }
    }

}
