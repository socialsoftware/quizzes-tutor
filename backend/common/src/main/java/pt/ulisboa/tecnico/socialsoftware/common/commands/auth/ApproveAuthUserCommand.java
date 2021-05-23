package pt.ulisboa.tecnico.socialsoftware.common.commands.auth;

import io.eventuate.tram.commands.common.Command;

//@CommandDestination(ServiceChannels.AUTH_USER_SERVICE_COMMAND_CHANNEL)
public class ApproveAuthUserCommand implements Command {

    private Integer userId;
    private Integer authUserId;
    private Integer courseExecutionId;
    private boolean isActive;

    public ApproveAuthUserCommand() {
    }

    public ApproveAuthUserCommand(Integer userId, Integer authUserId, Integer courseExecutionId, boolean isActive) {
        this.userId = userId;
        this.authUserId = authUserId;
        this.courseExecutionId = courseExecutionId;
        this.isActive = isActive;
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

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    @Override
    public String toString() {
        return "ApproveAuthUserCommand{" +
                "userId=" + userId +
                ", authUserId=" + authUserId +
                ", courseExecutionId=" + courseExecutionId +
                ", isActive=" + isActive +
                '}';
    }
}
