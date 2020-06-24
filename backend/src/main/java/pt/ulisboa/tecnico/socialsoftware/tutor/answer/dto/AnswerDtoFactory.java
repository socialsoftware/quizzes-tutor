package pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.MultipleChoiceQuestionAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.statement.dto.MultipleChoiceStatementAnswerDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.statement.dto.StatementAnswerDto;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.QUESTION_TYPE_NOT_IMPLEMENTED;

public class AnswerDtoFactory {

    public static QuestionAnswerDto getQuestionAnswerDto(QuestionAnswer questionAnswer){
        if (questionAnswer instanceof MultipleChoiceQuestionAnswer){
            return new MultipleChoiceQuestionAnswerDto((MultipleChoiceQuestionAnswer)questionAnswer);
        }
        else{
            throw new TutorException(QUESTION_TYPE_NOT_IMPLEMENTED, questionAnswer.getClass().getName());
        }
    }

    public static CorrectAnswerDto getCorrectAnswerDto(QuestionAnswer questionAnswer){
        if (questionAnswer instanceof MultipleChoiceQuestionAnswer){
            return new MultipleChoiceCorrectAnswerDto((MultipleChoiceQuestionAnswer)questionAnswer);
        }
        else{
            throw new TutorException(QUESTION_TYPE_NOT_IMPLEMENTED, questionAnswer.getClass().getName());
        }
    }

}
