package pt.ulisboa.tecnico.socialsoftware.dtos.question;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.io.Serializable;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        defaultImpl = MultipleChoiceQuestionDto.class,
        property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = MultipleChoiceQuestionDto.class, name = QuestionTypes.MULTIPLE_CHOICE_QUESTION),
        @JsonSubTypes.Type(value = CodeFillInQuestionDto.class, name = QuestionTypes.CODE_FILL_IN_QUESTION),
        @JsonSubTypes.Type(value = CodeOrderQuestionDto.class, name = QuestionTypes.CODE_ORDER_QUESTION),
})
public abstract class QuestionDetailsDto implements Serializable {
}
