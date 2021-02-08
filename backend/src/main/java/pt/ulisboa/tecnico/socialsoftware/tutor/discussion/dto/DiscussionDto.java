package pt.ulisboa.tecnico.socialsoftware.tutor.discussion.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import pt.ulisboa.tecnico.socialsoftware.tutor.utils.DateHandler;
import pt.ulisboa.tecnico.socialsoftware.tutor.discussion.domain.Discussion;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.DISCUSSION_MISSING_QUESTION;

public class DiscussionDto implements Serializable {
    private Integer id;
    private String name;
    private String username;
    private String message;
    private List<ReplyDto> replies = new ArrayList<>();
    private String date;
    private Integer courseExecutionId;
    private boolean closed;
    private QuestionDto question;


    public DiscussionDto() {
    }

    public DiscussionDto(Discussion discussion, boolean deep) {
        this.id = discussion.getId();

        if (discussion.getQuestion() == null) {
            throw new TutorException(DISCUSSION_MISSING_QUESTION);
        }

        if (deep) {
            this.question = new QuestionDto(discussion.getQuestion());
        }


        this.name = discussion.getUser().getName();
        this.username = discussion.getUser().getUsername();
        this.message = discussion.getMessage();
        this.date = DateHandler.toISOString(discussion.getDate());
        this.courseExecutionId = discussion.getCourseExecution().getId();
        this.closed = discussion.isClosed();

        if (discussion.getReplies() != null) {
            this.replies = discussion.getReplies().stream().map(ReplyDto::new).collect(Collectors.toList());
        }
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<ReplyDto> getReplies() {
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
                ", ame='" + name + '\'' +
                ", username='" + username + '\'' +
                ", message='" + message + '\'' +
                ", replies=" + replies +
                ", date='" + date + '\'' +
                ", courseExecutionId=" + courseExecutionId +
                '}';
    }
}
