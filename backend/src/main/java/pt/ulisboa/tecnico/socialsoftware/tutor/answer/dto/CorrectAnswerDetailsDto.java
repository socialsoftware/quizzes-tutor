package pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.MultipleChoiceQuestionDto;

import java.io.Serializable;

import static pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question.QuestionTypes.*;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        defaultImpl = MultipleChoiceQuestionDto.class,
        property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = MultipleChoiceCorrectAnswerDto.class, name = MULTIPLE_CHOICE_QUESTION),
        @JsonSubTypes.Type(value = CodeFillInCorrectAnswerDto.class, name = CODE_FILL_IN_QUESTION),
        @JsonSubTypes.Type(value = CodeOrderCorrectAnswerDto.class, name = CODE_ORDER_QUESTION),
})
public abstract class CorrectAnswerDetailsDto implements Serializable {

}
