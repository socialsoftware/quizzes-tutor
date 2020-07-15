package pt.ulisboa.tecnico.socialsoftware.tutor.statement.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.AnswerType;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.statement.QuestionAnswerItemRepository;

import static pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question.QuestionTypes.MULTIPLE_CHOICE_QUESTION;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        defaultImpl = MultipleChoiceStatementAnswerDto.class,
        property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = MultipleChoiceStatementAnswerDto.class, name = MULTIPLE_CHOICE_QUESTION)
})
public abstract class StatementAnswerDetailsDto {
    public abstract AnswerType getAnswerType(QuestionAnswer questionAnswer);
}
