package pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.AnswerDetails;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.Updator;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswerItem;

import java.io.Serializable;

import static pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question.QuestionTypes.*;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        defaultImpl = MultipleChoiceStatementAnswerDetailsDto.class,
        property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = MultipleChoiceStatementAnswerDetailsDto.class, name = MULTIPLE_CHOICE_QUESTION),
        @JsonSubTypes.Type(value = CodeFillInStatementAnswerDetailsDto.class, name = CODE_FILL_IN_QUESTION),
        @JsonSubTypes.Type(value = CodeOrderStatementAnswerDetailsDto.class, name = CODE_ORDER_QUESTION),
})
public abstract class StatementAnswerDetailsDto implements Updator, Serializable {
    public abstract AnswerDetails getAnswerDetails(QuestionAnswer questionAnswer);

    public abstract boolean emptyAnswer();

    public abstract QuestionAnswerItem getQuestionAnswerItem(String username, int quizId, StatementAnswerDto statementAnswerDto);
}
