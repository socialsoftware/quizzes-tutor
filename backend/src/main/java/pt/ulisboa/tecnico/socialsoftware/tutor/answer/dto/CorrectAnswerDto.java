package pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.statement.dto.CodeFillInStatementAnswerDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.statement.dto.MultipleChoiceStatementAnswerDto;

import java.io.Serializable;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        defaultImpl = MultipleChoiceCorrectAnswerDto.class,
        property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = MultipleChoiceCorrectAnswerDto.class, name = "multiple_choice"),
        @JsonSubTypes.Type(value = CodeFillInCorrectAnswerDto.class, name = "code_fill_in")
})
public abstract class CorrectAnswerDto implements Serializable {
    private Integer sequence;

    public CorrectAnswerDto(QuestionAnswer questionAnswer) {
        this.sequence = questionAnswer.getSequence();
    }

    public Integer getSequence() {
        return sequence;
    }

    public void setSequence(Integer sequence) {
        this.sequence = sequence;
    }

}
