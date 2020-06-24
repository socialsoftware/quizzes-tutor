package pt.ulisboa.tecnico.socialsoftware.tutor.question.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.MultipleChoiceQuestion;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.QUESTION_TYPE_NOT_IMPLEMENTED;

public class QuestionDtoFactory {

    public static QuestionDto getQuestionDto(Question question){
        if (question instanceof MultipleChoiceQuestion){
            return new MultipleChoiceQuestionDto((MultipleChoiceQuestion)question);
        }
        else{
            throw new TutorException(QUESTION_TYPE_NOT_IMPLEMENTED, question.getClass().getName());
        }

    }
}
