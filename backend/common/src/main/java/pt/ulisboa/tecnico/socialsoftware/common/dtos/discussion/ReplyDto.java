package pt.ulisboa.tecnico.socialsoftware.common.dtos.discussion;

import java.io.Serializable;

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