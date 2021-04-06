package pt.ulisboa.tecnico.socialsoftware.common.dtos.answer;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import pt.ulisboa.tecnico.socialsoftware.common.dtos.answer.CodeFillInStatementQuestionDetailsDto;

import static pt.ulisboa.tecnico.socialsoftware.common.dtos.question.QuestionTypes.*;
import java.io.Serializable;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        defaultImpl = MultipleChoiceStatementQuestionDetailsDto.class,
        property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = MultipleChoiceStatementQuestionDetailsDto.class, name = MULTIPLE_CHOICE_QUESTION),
        @JsonSubTypes.Type(value = CodeFillInStatementQuestionDetailsDto.class, name = CODE_FILL_IN_QUESTION),
        @JsonSubTypes.Type(value = CodeOrderStatementQuestionDetailsDto.class, name = CODE_ORDER_QUESTION),
})
public abstract class StatementQuestionDetailsDto implements Serializable {
}
