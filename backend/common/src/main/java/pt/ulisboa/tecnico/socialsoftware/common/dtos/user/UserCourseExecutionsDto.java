package pt.ulisboa.tecnico.socialsoftware.common.dtos.user;

import pt.ulisboa.tecnico.socialsoftware.common.dtos.execution.CourseExecutionDto;

import java.util.List;

public class UserCourseExecutionsDto {

    private List<CourseExecutionDto> courseExecutionDtoList;

    public UserCourseExecutionsDto() {}

    public List<CourseExecutionDto> getCourseExecutionDtoList() {
        return courseExecutionDtoList;
    }

    public void setCourseExecutionDtoList(List<CourseExecutionDto> courseExecutionDtoList) {
        this.courseExecutionDtoList = courseExecutionDtoList;
    }

    @Override
    public String toString() {
        return "UserCourseExecutions{" +
                "courseExecutionDtoList=" + courseExecutionDtoList +
                '}';
    }
}
