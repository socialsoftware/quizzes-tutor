package pt.ulisboa.tecnico.socialsoftware.tutor.discussion.dto;

import java.io.Serializable;

import pt.ulisboa.tecnico.socialsoftware.tutor.utils.DateHandler;
import pt.ulisboa.tecnico.socialsoftware.tutor.discussion.domain.Reply;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.REPLY_MISSING_DATA;

public class ReplyDto implements Serializable {
    private Integer id;
    private String name;
    private String username;
    private Integer userId;
    private String message;
    private String date;
    private boolean isPublic;

    public ReplyDto() {
    }

    public ReplyDto(Reply reply) {
        this.name = reply.getUser().getName();
        this.username = reply.getUser().getUsername();
        this.id = reply.getId();
        this.userId = reply.getUser().getId();
        checkEmptyMessage(reply.getMessage());
        this.message = reply.getMessage();
        this.date = DateHandler.toISOString(reply.getDate());
        this.isPublic = reply.isPublic();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
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

    public void setUserId(int teacherId) {
        this.userId = teacherId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public void setPublic(boolean aPublic) {
        this.isPublic = aPublic;
    }

    private void checkEmptyMessage(String message) {
        if (message.trim().length() == 0){
            throw new TutorException(REPLY_MISSING_DATA);
        }
    }

    @Override
    public String toString() {
        return "ReplyDto{" +
                "id=" + id +
                ", userName='" + username + '\'' +
                ", userId=" + userId +
                ", message='" + message + '\'' +
                ", date='" + date + '\'' +
                ", isPublic=" + isPublic +
                '}';
    }
}