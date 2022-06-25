package pt.ulisboa.tecnico.socialsoftware.common.activity;

import java.util.List;

import pt.ulisboa.tecnico.socialsoftware.common.dtos.execution.CourseExecutionDto;

public interface CourseExecutionActivities {

    CourseExecutionDto getCourseExecution(Integer courseExecutionId);

    void removeCourseExecution(Integer userId, Integer courseExecutionId);

    void addCourseExecutions(Integer userId, List<CourseExecutionDto> courseExecutionDtoList);

}
