package pt.ulisboa.tecnico.socialsoftware.common.commands.execution;

import io.eventuate.tram.commands.common.Command;

public class GetCourseExecutionCommand implements Command {
    public Integer courseExecutionId;

    public GetCourseExecutionCommand() {
    }

    public GetCourseExecutionCommand(Integer courseExecutionId) {
        this.courseExecutionId = courseExecutionId;
    }

    public Integer getCourseExecutionId() {
        return courseExecutionId;
    }

    public void setCourseExecutionId(Integer courseExecutionId) {
        this.courseExecutionId = courseExecutionId;
    }

    @Override
    public String toString() {
        return "GetCourseExecutionCommand{" +
                "courseExecutionId=" + courseExecutionId +
                '}';
    }
}
