package pt.ulisboa.tecnico.socialsoftware.tutor.discussion.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import pt.ulisboa.tecnico.socialsoftware.tutor.config.DateHandler;
import pt.ulisboa.tecnico.socialsoftware.tutor.discussion.domain.Discussion;
import pt.ulisboa.tecnico.socialsoftware.tutor.discussion.domain.Reply;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.DISCUSSION_MISSING_QUESTION;
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.DISCUSSION_MISSING_USER;

public class DiscussionDto implements Serializable {
    private Integer id;
    private Integer userId;
    private String userName;
    private String message;
    private List<ReplyDto> replies;
    private String date;
    private Integer courseExecutionId;
    private boolean closed;
    private QuestionDto question;


    public DiscussionDto() {
    }

    public DiscussionDto(Discussion discussion, boolean deep) {
        this.id = discussion.getId();

        if (discussion.getQuestion() == null ) {
            throw new TutorException(DISCUSSION_MISSING_QUESTION);
        }
        else{
            if (deep) {
                this.question = new QuestionDto(discussion.getQuestion());
            }
        }

        if (discussion.getUser().getId() == null){
            throw new TutorException(DISCUSSION_MISSING_USER);
        }
        else{
            this.userId = discussion.getUser().getId();
        }
        this.userName = discussion.getUser().getName();
        this.message = discussion.getMessage();
        this.date = DateHandler.toISOString(discussion.getDate());
        this.courseExecutionId = discussion.getQuestionAnswer().getQuizAnswer().getQuiz().getCourseExecution().getId();
        this.closed = discussion.isClosed();
        this.replies = new ArrayList<>();

        List<Reply> discussionReplies = discussion.getReplies();
        if(discussionReplies != null && !discussionReplies.isEmpty()){
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

    public void setUserId(Integer id) {
        this.userId = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public List<ReplyDto> getReplies() {
        if(this.replies == null){
            this.replies = new ArrayList<>();
        }
        return replies;
    }

    public void setReplies(List<ReplyDto> replies) {
        this.replies = replies;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
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

    public boolean isClosed() {
        return closed;
    }

    public void setClosed(boolean closed) {
        this.closed = closed;
    }

    public QuestionDto getQuestion() {
        return question;
    }

    public void setQuestion(QuestionDto question) {
        this.question = question;
    }

    @Override
    public String toString() {
        return "DiscussionDto{" +
                "id=" + id +
                ", userId=" + userId +
                ", userName='" + userName + '\'' +
                ", message='" + message + '\'' +
                ", replies=" + replies +
                ", date='" + date + '\'' +
                ", courseExecutionId=" + courseExecutionId +
                '}';
    }
}
