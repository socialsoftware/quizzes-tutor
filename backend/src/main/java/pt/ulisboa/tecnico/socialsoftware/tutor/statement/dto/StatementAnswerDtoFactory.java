package pt.ulisboa.tecnico.socialsoftware.tutor.statement.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.MultipleChoiceQuestionAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.MultipleChoiceQuestion;

public class StatementAnswerDtoFactory {

    public static StatementAnswerDto getStatementAnswerDto(QuestionAnswer questionAnswer){
        if (questionAnswer instanceof MultipleChoiceQuestionAnswer){
            return new MultipleChoiceStatementAnswerDto((MultipleChoiceQuestionAnswer)questionAnswer);
        }
        else{
            // todo might be better to throw a custom exception.
            return null;
        }
    }
}
