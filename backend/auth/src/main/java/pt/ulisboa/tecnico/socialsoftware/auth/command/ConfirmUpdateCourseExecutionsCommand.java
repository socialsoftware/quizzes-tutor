package pt.ulisboa.tecnico.socialsoftware.auth.command;

import io.eventuate.tram.commands.common.Command;
import pt.ulisboa.tecnico.socialsoftware.common.dtos.execution.CourseExecutionDto;

import java.util.List;

public class ConfirmUpdateCourseExecutionsCommand implements Command {
    private Integer authUserId;
    private String ids;
    private List<CourseExecutionDto> courseExecutionDtoList;
    private String email;

    public ConfirmUpdateCourseExecutionsCommand() {
    }

    public ConfirmUpdateCourseExecutionsCommand(Integer authUserId, String ids,
                                                List<CourseExecutionDto> courseExecutionDtoList, String email) {
        this.authUserId = authUserId;
        this.ids = ids;
        this.courseExecutionDtoList = courseExecutionDtoList;
        this.email = email;
    }

    public Integer getAuthUserId() {
        return authUserId;
    }

    public void setAuthUserId(Integer authUserId) {
        this.authUserId = authUserId;
    }

    public String getIds() {
        return ids;
    }

    public void setIds(String ids) {
        this.ids = ids;
    }

    public List<CourseExecutionDto> getCourseExecutionDtoList() {
        return courseExecutionDtoList;
    }

    public void setCourseExecutionDtoList(List<CourseExecutionDto> courseExecutionDtoList) {
        this.courseExecutionDtoList = courseExecutionDtoList;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "ConfirmUpdateCourseExecutionsCommand{" +
                "authUserId=" + authUserId +
                ", ids='" + ids + '\'' +
                ", courseExecutionDtoList=" + courseExecutionDtoList +
                ", email='" + email + '\'' +
                '}';
    }
}
