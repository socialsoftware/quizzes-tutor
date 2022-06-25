package pt.ulisboa.tecnico.socialsoftware.tutor.execution.activity;

import java.util.List;

import pt.ulisboa.tecnico.socialsoftware.common.activity.CourseExecutionActivities;
import pt.ulisboa.tecnico.socialsoftware.common.dtos.execution.CourseExecutionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.CourseExecutionService;

public class CourseExecutionActivitiesImpl implements CourseExecutionActivities {

    private final CourseExecutionService courseExecutionService;

    public CourseExecutionActivitiesImpl(CourseExecutionService courseExecutionService) {
        this.courseExecutionService = courseExecutionService;
    }

    @Override
    public CourseExecutionDto getCourseExecution(Integer courseExecutionId) {
        return courseExecutionService.getCourseExecutionById(courseExecutionId);
    }

    @Override
    public void removeCourseExecution(Integer userId, Integer courseExecutionId) {
        courseExecutionService.removeUserFromTecnicoCourseExecution(userId, courseExecutionId);
    }

    @Override
    public void addCourseExecutions(Integer userId, List<CourseExecutionDto> courseExecutionDtoList) {
        courseExecutionService.addCourseExecutions(userId, courseExecutionDtoList);
    }

}
