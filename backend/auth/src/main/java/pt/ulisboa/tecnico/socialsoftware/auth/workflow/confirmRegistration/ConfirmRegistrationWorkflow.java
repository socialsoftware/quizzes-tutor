package pt.ulisboa.tecnico.socialsoftware.auth.workflow.confirmRegistration;

import com.uber.cadence.workflow.WorkflowMethod;

import pt.ulisboa.tecnico.socialsoftware.common.utils.CadenceConstants;

public interface ConfirmRegistrationWorkflow {

    @WorkflowMethod(executionStartToCloseTimeoutSeconds = 60, taskList = CadenceConstants.AUTH_TASK_LIST)
    void confirmRegistration(Integer authUserId, Integer userId, String password);

}
