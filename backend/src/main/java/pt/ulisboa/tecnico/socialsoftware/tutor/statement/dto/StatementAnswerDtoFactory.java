package pt.ulisboa.tecnico.socialsoftware.tutor.statement.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.MultipleChoiceQuestionAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.MultipleChoiceQuestion;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.QUESTION_TYPE_NOT_IMPLEMENTED;

public class StatementAnswerDtoFactory {

    public static StatementAnswerDto getStatementAnswerDto(QuestionAnswer questionAnswer){
        if (questionAnswer instanceof MultipleChoiceQuestionAnswer){
            return new MultipleChoiceStatementAnswerDto((MultipleChoiceQuestionAnswer)questionAnswer);
        }
        else{
            throw new TutorException(QUESTION_TYPE_NOT_IMPLEMENTED, questionAnswer.getClass().getName());
        }
    }
}
