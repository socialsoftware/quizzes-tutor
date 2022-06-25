package pt.ulisboa.tecnico.socialsoftware.auth.workflow.updateCourseExecutions;

import java.util.List;

import com.uber.cadence.workflow.WorkflowMethod;

import pt.ulisboa.tecnico.socialsoftware.common.dtos.execution.CourseExecutionDto;

public interface UpdateCourseExectionsWorkflow {

    @WorkflowMethod
    void updateCourseExecutions(Integer authUserId, Integer userId, String ids,
            List<CourseExecutionDto> courseExecutionDtoList, String email);

}
