package pt.ulisboa.tecnico.socialsoftware.tutor.api;

import pt.ulisboa.tecnico.socialsoftware.tutor.anticorruptionlayer.execution.dtos.CourseExecutionDto;

public interface CourseExecutionInterface {
    CourseExecutionDto getCourseExecutionById(Integer courseExecutionId);

    Integer getDemoCourseExecutionId();
}
