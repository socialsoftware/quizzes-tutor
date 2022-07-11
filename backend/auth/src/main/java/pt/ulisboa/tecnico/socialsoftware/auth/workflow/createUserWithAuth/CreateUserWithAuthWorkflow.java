package pt.ulisboa.tecnico.socialsoftware.auth.workflow.createUserWithAuth;

import java.util.List;

import com.uber.cadence.workflow.WorkflowMethod;

import pt.ulisboa.tecnico.socialsoftware.common.dtos.execution.CourseExecutionDto;
import pt.ulisboa.tecnico.socialsoftware.common.dtos.user.Role;
import pt.ulisboa.tecnico.socialsoftware.common.utils.CadenceConstants;

public interface CreateUserWithAuthWorkflow {

    @WorkflowMethod(executionStartToCloseTimeoutSeconds = 60, taskList = CadenceConstants.AUTH_TASK_LIST)
    void createUserWithAuth(Integer authUserId, String name, Role role, String username, boolean isActive,
            List<CourseExecutionDto> courseExecutionDtoList);

}
