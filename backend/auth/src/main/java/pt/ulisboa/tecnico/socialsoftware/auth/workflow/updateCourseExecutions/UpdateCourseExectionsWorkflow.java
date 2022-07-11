package pt.ulisboa.tecnico.socialsoftware.auth.workflow.updateCourseExecutions;

import java.util.List;

import com.uber.cadence.workflow.WorkflowMethod;

import pt.ulisboa.tecnico.socialsoftware.common.dtos.execution.CourseExecutionDto;
import pt.ulisboa.tecnico.socialsoftware.common.utils.CadenceConstants;

public interface UpdateCourseExectionsWorkflow {

    @WorkflowMethod(executionStartToCloseTimeoutSeconds = 60, taskList = CadenceConstants.AUTH_TASK_LIST)
    void updateCourseExecutions(Integer authUserId, Integer userId, String ids,
            List<CourseExecutionDto> courseExecutionDtoList, String email);

}
