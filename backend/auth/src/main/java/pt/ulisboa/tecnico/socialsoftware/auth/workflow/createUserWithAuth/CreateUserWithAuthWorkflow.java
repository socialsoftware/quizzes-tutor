package pt.ulisboa.tecnico.socialsoftware.auth.workflow.createUserWithAuth;

import java.util.List;

import com.uber.cadence.workflow.WorkflowMethod;

import pt.ulisboa.tecnico.socialsoftware.common.dtos.execution.CourseExecutionDto;
import pt.ulisboa.tecnico.socialsoftware.common.dtos.user.Role;

public interface CreateUserWithAuthWorkflow {

    @WorkflowMethod
    void createUserWithAuth(Integer authUserId, String name, Role role, String username, boolean isActive,
            List<CourseExecutionDto> courseExecutionDtoList);

}
