package pt.ulisboa.tecnico.socialsoftware.tutor.api;

import pt.ulisboa.tecnico.socialsoftware.dtos.execution.CourseExecutionDto;

public interface CourseExecutionInterface {
    CourseExecutionDto getCourseExecutionById(Integer courseExecutionId);

    Integer getDemoCourseExecutionId();
}
