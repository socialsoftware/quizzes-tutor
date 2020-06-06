package pt.ulisboa.tecnico.socialsoftware.tutor.question.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.CodeFillInQuestion;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.MultipleChoiceQuestion;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;

public class QuestionDtoFactory {

    public static QuestionDto getQuestionDto(Question question){
        if (question instanceof MultipleChoiceQuestion){
            return new MultipleChoiceQuestionDto((MultipleChoiceQuestion)question);
        }
        else if (question instanceof CodeFillInQuestion){
            return new CodeFillInQuestionDto((CodeFillInQuestion)question);
        }
        else{
            // todo might be better to throw a custom exception.
            return null;
        }

    }
}
