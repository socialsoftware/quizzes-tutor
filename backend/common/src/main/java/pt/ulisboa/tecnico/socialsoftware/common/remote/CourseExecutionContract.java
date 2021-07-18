package pt.ulisboa.tecnico.socialsoftware.common.remote;

import pt.ulisboa.tecnico.socialsoftware.common.dtos.execution.CourseExecutionDto;

public interface CourseExecutionContract {
    CourseExecutionDto findCourseExecution(Integer courseExecutionId);

    CourseExecutionDto findCourseExecutionByFields(String acronym, String academicTerm, String type);

    CourseExecutionDto findDemoCourseExecution();
}
