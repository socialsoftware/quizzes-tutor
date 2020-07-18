package pt.ulisboa.tecnico.socialsoftware.tutor.statement.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.AnswerDetails;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer;

import java.io.Serializable;

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

    public AnswerDetails getAnswerDetails(QuestionAnswer questionAnswer){
        return this.getAnswerDetails() != null ? this.answerDetails.getAnswerDetails(questionAnswer) : null;
    }

    public StatementAnswerDetailsDto getAnswerDetails() {
        return answerDetails;
    }

    public void setAnswerDetails(StatementAnswerDetailsDto answerDetails) {
        this.answerDetails = answerDetails;
    }

    @Override
    public String toString() {
        return "StatementAnswerDto{" +
                "timeTaken=" + timeTaken +
                ", sequence=" + sequence +
                ", questionAnswerId=" + questionAnswerId +
                ", quizQuestionId=" + quizQuestionId +
                ", timeToSubmission=" + timeToSubmission +
                ", answerDetails=" + answerDetails +
                '}';
    }

    public boolean noAnswer() {
        return this.answerDetails.noAnswer();
    }
}