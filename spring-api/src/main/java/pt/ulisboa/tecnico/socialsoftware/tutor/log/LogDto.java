package pt.ulisboa.tecnico.socialsoftware.tutor.log;

import java.io.Serializable;
import java.time.LocalDateTime;

public class LogDto implements Serializable {
    private Integer id;
    private Integer userId;
    private LocalDateTime time;
    private String operation;

    public LogDto(Log log) {
        this.id = log.getId();
        this.userId = log.getUser().getId();
        this.time = log.getTime();
        this.operation = log.getOperation();
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

    @Override
    public String toString() {
        return "LogDto{" +
                "id=" + id +
                ", userId=" + userId +
                ", time=" + time +
                ", operation='" + operation + '\'' +
                '}';
    }
}
