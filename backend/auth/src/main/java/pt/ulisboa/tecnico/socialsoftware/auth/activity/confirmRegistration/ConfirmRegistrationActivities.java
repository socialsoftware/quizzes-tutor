package pt.ulisboa.tecnico.socialsoftware.auth.activity.confirmRegistration;

import com.uber.cadence.activity.ActivityMethod;

import pt.ulisboa.tecnico.socialsoftware.common.utils.CadenceConstants;

public interface ConfirmRegistrationActivities {

    @ActivityMethod(scheduleToCloseTimeoutSeconds = 60, taskList = CadenceConstants.AUTH_TASK_LIST)
    void undoConfirmRegistration(Integer authUserId);

    @ActivityMethod(scheduleToCloseTimeoutSeconds = 60, taskList = CadenceConstants.AUTH_TASK_LIST)
    void confirmRegistration(Integer authUserId, String password);

}
