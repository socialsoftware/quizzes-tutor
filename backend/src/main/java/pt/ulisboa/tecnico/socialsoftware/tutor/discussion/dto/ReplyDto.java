package pt.ulisboa.tecnico.socialsoftware.tutor.discussion.dto;

import java.io.Serializable;

import java.time.LocalDateTime;

import pt.ulisboa.tecnico.socialsoftware.tutor.config.DateHandler;
import pt.ulisboa.tecnico.socialsoftware.tutor.discussion.domain.Reply;

public class ReplyDto implements Serializable {
    private Integer id;
    private Integer userId;
    private String message;
    private String date;

    public ReplyDto() {
    }

    public ReplyDto(Reply reply) {
        this.setId(reply.getId());
        this.setUserId(reply.getUser().getId());
        this.setMessage(reply.getMessage());
        this.setDate(reply.getDate());
    }

    public String getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = DateHandler.toISOString(date);
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

}