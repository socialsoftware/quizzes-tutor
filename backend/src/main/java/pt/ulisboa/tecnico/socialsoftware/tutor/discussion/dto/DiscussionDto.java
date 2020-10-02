package pt.ulisboa.tecnico.socialsoftware.tutor.discussion.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import pt.ulisboa.tecnico.socialsoftware.tutor.config.DateHandler;
import pt.ulisboa.tecnico.socialsoftware.tutor.discussion.domain.Discussion;
import pt.ulisboa.tecnico.socialsoftware.tutor.discussion.domain.Reply;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto;

public class DiscussionDto implements Serializable {
    private Integer id;
    private Integer userId;
    private QuestionDto question;
    private String userName;
    private String message;
    private List<ReplyDto> replies;
    private boolean available;
    private String date;
    private Integer questionId;
    private Integer courseExecutionId;

    public DiscussionDto() {
    }

    public DiscussionDto(Discussion discussion) {
        this.id = discussion.getId();
        this.userId = discussion.getUserId();
        this.questionId = discussion.getQuestionId();
        this.userName = discussion.getUser().getName();
        this.message = discussion.getMessage();
        this.question = new QuestionDto(discussion.getQuestionAnswer().getQuizQuestion().getQuestion());
        this.available = discussion.isAvailable();
        this.date = DateHandler.toISOString(discussion.getDate());
        this.courseExecutionId = discussion.getQuestionAnswer().getQuizAnswer().getQuiz().getCourseExecution().getId();

        List<Reply> discussionReplies = discussion.getReplies();
        if(discussionReplies != null && !discussionReplies.isEmpty()){
            this.replies = new ArrayList<>();
            for(Reply rep : discussionReplies){
                this.replies.add(new ReplyDto(rep));
            }
        }
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getUserId() {
        return userId;
    }

    public Integer getQuestionId() {
        return question.getId();
    }

    public void setUserId(Integer id) {
        this.userId = id;
    }

    public QuestionDto getQuestion() {
        return question;
    }

    public void setQuestion(QuestionDto question) {
        this.question = question;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public List<ReplyDto> getReplies() {
        return replies;
    }

    public void setReplies(List<ReplyDto> replies) {
        this.replies = replies;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setQuestionId(Integer questionId) {
        this.questionId = questionId;
    }

    public Integer getCourseExecutionId() {
        return courseExecutionId;
    }

    public void setCourseExecutionId(Integer courseExecutionId) {
        this.courseExecutionId = courseExecutionId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "DiscussionDto{" +
                "id=" + id +
                ", userId=" + userId +
                ", question=" + question +
                ", userName='" + userName + '\'' +
                ", message='" + message + '\'' +
                ", replies=" + replies +
                ", available=" + available +
                ", date='" + date + '\'' +
                ", questionId=" + questionId +
                ", courseExecutionId=" + courseExecutionId +
                '}';
    }
}
