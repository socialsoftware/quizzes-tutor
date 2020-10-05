package pt.ulisboa.tecnico.socialsoftware.tutor.discussion.dto;

import java.io.Serializable;

import java.time.LocalDateTime;

import pt.ulisboa.tecnico.socialsoftware.tutor.config.DateHandler;
import pt.ulisboa.tecnico.socialsoftware.tutor.discussion.domain.Reply;

public class ReplyDto implements Serializable {
    private Integer id;
    private String userName;
    private Integer userId;
    private String message;
    private String date;
    private boolean available;

    public ReplyDto() {
    }

    public ReplyDto(Reply reply) {
        this.userName = reply.getUser().getUsername();
        this.id = reply.getId();
        this.userId = reply.getUser().getId();
        this.message = reply.getMessage();
        this.date = DateHandler.toISOString(reply.getDate());
        this.available = reply.isAvailable();
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

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }
}