package pt.ulisboa.tecnico.socialsoftware.auth.command;

import io.eventuate.tram.commands.common.Command;
import pt.ulisboa.tecnico.socialsoftware.common.dtos.execution.CourseExecutionDto;

import java.util.List;

public class ApproveAuthUserCommand implements Command {

    private Integer userId;
    private Integer authUserId;
    private List<CourseExecutionDto> courseExecutionDtoList;

    public ApproveAuthUserCommand() {
    }

    public ApproveAuthUserCommand(Integer userId, Integer authUserId, List<CourseExecutionDto> courseExecutionDtos) {
        this.userId = userId;
        this.authUserId = authUserId;
        this.courseExecutionDtoList = courseExecutionDtos;
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

    public List<CourseExecutionDto> getCourseExecutionDtoList() {
        return courseExecutionDtoList;
    }

    public void setCourseExecutionDtoList(List<CourseExecutionDto> courseExecutionDtoList) {
        this.courseExecutionDtoList = courseExecutionDtoList;
    }

    @Override
    public String toString() {
        return "ApproveAuthUserCommand{" +
                "userId=" + userId +
                ", authUserId=" + authUserId +
                ", courseExecutionDtoList=" + courseExecutionDtoList +
                '}';
    }
}
