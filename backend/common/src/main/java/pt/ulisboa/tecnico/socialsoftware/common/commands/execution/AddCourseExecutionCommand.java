package pt.ulisboa.tecnico.socialsoftware.common.commands.execution;

import io.eventuate.tram.commands.common.Command;

public class AddCourseExecutionCommand implements Command {

    private Integer userId;
    private Integer courseExecutionId;

    public AddCourseExecutionCommand() {
    }

    public AddCourseExecutionCommand(Integer userId, Integer courseExecutionId) {
        this.userId = userId;
        this.courseExecutionId = courseExecutionId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getCourseExecutionId() {
        return courseExecutionId;
    }

    public void setCourseExecutionId(Integer courseExecutionId) {
        this.courseExecutionId = courseExecutionId;
    }

    @Override
    public String toString() {
        return "AddCourseExecutionCommand{" +
                "userId=" + userId +
                ", courseExecutionId=" + courseExecutionId +
                '}';
    }
}
