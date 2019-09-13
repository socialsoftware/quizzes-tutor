package pt.ulisboa.tecnico.socialsoftware.tutor.access;

import java.time.LocalDateTime;

public class AccessDto {
    private Integer id;
    private Integer userId;
    private LocalDateTime time;
    private String operation;

    public AccessDto(Access access) {
        this.id = access.getId();
        this.userId = access.getUser().getId();
        this.time = access.getTime();
        this.operation = access.getOperation();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }
}
