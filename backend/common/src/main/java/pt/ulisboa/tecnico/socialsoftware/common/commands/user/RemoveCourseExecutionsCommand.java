package pt.ulisboa.tecnico.socialsoftware.common.commands.user;

import io.eventuate.tram.commands.common.Command;
import pt.ulisboa.tecnico.socialsoftware.common.dtos.execution.CourseExecutionDto;

import java.util.List;

public class RemoveCourseExecutionsCommand implements Command {

    private Integer userId;
    private List<CourseExecutionDto> courseExecutionDtoList;

    public RemoveCourseExecutionsCommand() {
    }

    public RemoveCourseExecutionsCommand(Integer userId, List<CourseExecutionDto> courseExecutionDtoList) {
        this.userId = userId;
        this.courseExecutionDtoList = courseExecutionDtoList;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public List<CourseExecutionDto> getCourseExecutionDtoList() {
        return courseExecutionDtoList;
    }

    public void setCourseExecutionDtoList(List<CourseExecutionDto> courseExecutionDtoList) {
        this.courseExecutionDtoList = courseExecutionDtoList;
    }
}
