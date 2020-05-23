package pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.MultipleChoiceQuestionAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.statement.dto.MultipleChoiceStatementAnswerDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.statement.dto.StatementAnswerDto;

public class QuestionAnswerDtoFactory {

    public static QuestionAnswerDto getQuestionAnswerDto(QuestionAnswer questionAnswer){
        if (questionAnswer instanceof MultipleChoiceQuestionAnswer){
            return new MultipleChoiceQuestionAnswerDto((MultipleChoiceQuestionAnswer)questionAnswer);
        }
        else{
            // todo might be better to throw a custom exception.
            return null;
        }
    }

}
