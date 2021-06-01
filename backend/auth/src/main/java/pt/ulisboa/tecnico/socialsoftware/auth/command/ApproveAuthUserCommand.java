package pt.ulisboa.tecnico.socialsoftware.auth.command;

import io.eventuate.tram.commands.common.Command;

public class ApproveAuthUserCommand implements Command {

    private Integer userId;
    private Integer authUserId;
    private Integer courseExecutionId;

    public ApproveAuthUserCommand() {
    }

    public ApproveAuthUserCommand(Integer userId, Integer authUserId, Integer courseExecutionId) {
        this.userId = userId;
        this.authUserId = authUserId;
        this.courseExecutionId = courseExecutionId;
    }

    public Integer getUserId() {
        return userId;
    }

    public Integer getAuthUserId() {
        return authUserId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public void setAuthUserId(Integer authUserId) {
        this.authUserId = authUserId;
    }

    public Integer getCourseExecutionId() {
        return courseExecutionId;
    }

    public void setCourseExecutionId(Integer courseExecutionId) {
        this.courseExecutionId = courseExecutionId;
    }

    @Override
    public String toString() {
        return "ApproveAuthUserCommand{" +
                "userId=" + userId +
                ", authUserId=" + authUserId +
                ", courseExecutionId=" + courseExecutionId +
                '}';
    }
}
