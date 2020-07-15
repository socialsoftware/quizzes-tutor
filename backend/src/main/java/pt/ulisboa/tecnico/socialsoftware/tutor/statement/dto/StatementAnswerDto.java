package pt.ulisboa.tecnico.socialsoftware.tutor.statement.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.AnswerType;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.statement.QuestionAnswerItem;
import pt.ulisboa.tecnico.socialsoftware.tutor.statement.QuestionAnswerItemRepository;

import java.io.Serializable;

import static pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question.QuestionTypes.MULTIPLE_CHOICE_QUESTION;

public class StatementAnswerDto implements Serializable {
    private Integer timeTaken;
    private Integer sequence;
    private Integer questionAnswerId;
    private Integer quizQuestionId;
    private Integer timeToSubmission;

    private StatementAnswerDetailsDto answerDetails;

    public StatementAnswerDto() {
    }

    public StatementAnswerDto(QuestionAnswer questionAnswer) {
        this.timeTaken = questionAnswer.getTimeTaken();
        this.sequence = questionAnswer.getSequence();
        this.questionAnswerId = questionAnswer.getId();
        this.quizQuestionId = questionAnswer.getQuizQuestion().getId();

        this.answerDetails = questionAnswer.getStatementAnswerDetailsDto();
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

    public AnswerType getAnswerType(QuestionAnswer questionAnswer){
        return this.getAnswerDetails() != null ? this.getAnswerDetails().getAnswerType(questionAnswer) : null;
    }

    public StatementAnswerDetailsDto getAnswerDetails() {
        return answerDetails;
    }

    public void setAnswerDetails(StatementAnswerDetailsDto answerDetails) {
        this.answerDetails = answerDetails;
    }
}