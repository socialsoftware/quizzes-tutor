package pt.ulisboa.tecnico.socialsoftware.auth.workflow.confirmRegistration;

import com.uber.cadence.workflow.WorkflowMethod;

public interface ConfirmRegistrationWorkflow {

    @WorkflowMethod
    void confirmRegistration(Integer authUserId, Integer userId, String password);

}
