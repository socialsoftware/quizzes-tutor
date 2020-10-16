package pt.ulisboa.tecnico.socialsoftware.tutor.statement.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.discussion.dto.DiscussionDto;

import java.io.Serializable;

public class StatementAnswerDto implements Serializable {
    private Integer timeTaken;
    private Integer sequence;
    private Integer questionAnswerId;
    private DiscussionDto userDiscussion;
    private Integer optionId;
    private Integer quizQuestionId;
    private Integer timeToSubmission;

    public StatementAnswerDto() {}

    public StatementAnswerDto(QuestionAnswer questionAnswer) {
        this.timeTaken = questionAnswer.getTimeTaken();
        this.sequence = questionAnswer.getSequence();
        this.questionAnswerId = questionAnswer.getId();
        this.quizQuestionId = questionAnswer.getQuizQuestion().getId();

        if (questionAnswer.getOption() != null) {
            this.optionId = questionAnswer.getOption().getId();
        }

        if(questionAnswer.getDiscussion() != null){
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

    public Integer getQuestionAnswerId() {
        return questionAnswerId;
    }

    public void setQuestionAnswerId(Integer questionAnswerId) {
        this.questionAnswerId = questionAnswerId;
    }

    public Integer getOptionId() {
        return optionId;
    }

    public void setOptionId(Integer optionId) {
        this.optionId = optionId;
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
                ", optionId=" + optionId +
                '}';
    }
}