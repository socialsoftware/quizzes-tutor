package pt.ulisboa.tecnico.socialsoftware.tutor.statement.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer;

import java.io.Serializable;

import static pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question.QuestionTypes.CODE_FILL_IN;
import static pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question.QuestionTypes.MULTIPLE_CHOICE_QUESTION;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        defaultImpl = MultipleChoiceStatementAnswerDto.class,
        property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = MultipleChoiceStatementAnswerDto.class, name = MULTIPLE_CHOICE_QUESTION),
        @JsonSubTypes.Type(value = CodeFillInStatementAnswerDto.class, name = CODE_FILL_IN)
})
public abstract class StatementAnswerDto implements Serializable {
    private Integer timeTaken;
    private Integer sequence;
    private Integer questionAnswerId;
    private Integer quizQuestionId;
    private Integer timeToSubmission;

    public StatementAnswerDto() {
    }

    public StatementAnswerDto(QuestionAnswer questionAnswer) {
        this.timeTaken = questionAnswer.getTimeTaken();
        this.sequence = questionAnswer.getSequence();
        this.questionAnswerId = questionAnswer.getId();
        this.quizQuestionId = questionAnswer.getQuizQuestion().getId();
    }

    public Integer getTimeTaken() {
        return timeTaken;
    }

    public void setTimeTaken(Integer timeTaken) {
        this.timeTaken = timeTaken;
    }

    public Integer getSequence() {
        return sequence;
    }

    public void setSequence(Integer sequence) {
        this.sequence = sequence;
    }

    public Integer getQuestionAnswerId() {
        return questionAnswerId;
    }

    public void setQuestionAnswerId(Integer questionAnswerId) {
        this.questionAnswerId = questionAnswerId;
    }

    public Integer getQuizQuestionId() {
        return quizQuestionId;
    }

    public void setQuizQuestionId(Integer quizQuestionId) {
        this.quizQuestionId = quizQuestionId;
    }

    public Integer getTimeToSubmission() {
        return timeToSubmission;
    }

    public void setTimeToSubmission(Integer timeToSubmission) {
        this.timeToSubmission = timeToSubmission;
    }

}