package pt.ulisboa.tecnico.socialsoftware.tutor.api;

import pt.ulisboa.tecnico.socialsoftware.common.dtos.execution.CourseExecutionDto;

public interface CourseExecutionContract {
    CourseExecutionDto findCourseExecutionById(Integer courseExecutionId);

    Integer getDemoCourseExecutionId();
}
