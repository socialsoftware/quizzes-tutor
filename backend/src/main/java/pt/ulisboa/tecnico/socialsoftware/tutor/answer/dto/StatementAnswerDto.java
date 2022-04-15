package pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.AnswerDetails;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswerItem;
import pt.ulisboa.tecnico.socialsoftware.tutor.discussion.dto.DiscussionDto;

import java.io.Serializable;

public class StatementAnswerDto implements Serializable {
    private Integer timeTaken;
    private Integer timeToSubmission;
    private Integer sequence;
    private Integer quizAnswerId;
    private Integer questionAnswerId;
    private Integer questionId;
    private Integer quizQuestionId;
    private DiscussionDto userDiscussion;

    private StatementAnswerDetailsDto answerDetails;

    public StatementAnswerDto() {
    }

    public StatementAnswerDto(QuestionAnswer questionAnswer) {
        this.timeTaken = questionAnswer.getTimeTaken();
        this.sequence = questionAnswer.getSequence();
        this.quizAnswerId = questionAnswer.getQuizAnswer().getId();
        this.questionAnswerId = questionAnswer.getId();
        this.questionId = questionAnswer.getQuizQuestion().getQuestion().getId();
        this.quizQuestionId = questionAnswer.getQuizQuestion().getId();

        this.answerDetails = questionAnswer.getStatementAnswerDetailsDto();

        if (questionAnswer.getDiscussion() != null){
            this.userDiscussion = new DiscussionDto(questionAnswer.getDiscussion(),false);
        }
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

    public Integer getQuizAnswerId() {
        return quizAnswerId;
    }

    public void setQuizAnswerId(Integer quizAnswerId) {
        this.quizAnswerId = quizAnswerId;
    }

    public Integer getQuestionAnswerId() {
        return questionAnswerId;
    }

    public void setQuestionAnswerId(Integer questionAnswerId) {
        this.questionAnswerId = questionAnswerId;
    }

    public Integer getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Integer questionId) {
        this.questionId = questionId;
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

    public DiscussionDto getUserDiscussion() {
        return userDiscussion;
    }

    public void setUserDiscussion(DiscussionDto userDiscussion) {
        this.userDiscussion = userDiscussion;
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

    public boolean emptyAnswer() {
        return this.answerDetails.emptyAnswer();
    }

    public QuestionAnswerItem getQuestionAnswerItem(String username, int quizId) {
        return this.answerDetails.getQuestionAnswerItem(username, quizId, this);
    }
}