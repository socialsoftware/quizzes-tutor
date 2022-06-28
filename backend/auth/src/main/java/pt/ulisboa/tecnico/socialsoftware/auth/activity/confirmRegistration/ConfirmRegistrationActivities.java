package pt.ulisboa.tecnico.socialsoftware.auth.activity.confirmRegistration;

import com.uber.cadence.activity.ActivityMethod;

import pt.ulisboa.tecnico.socialsoftware.common.utils.Constants;

public interface ConfirmRegistrationActivities {

    @ActivityMethod(scheduleToCloseTimeoutSeconds = 60, taskList = Constants.AUTH_TASK_LIST)
    void undoConfirmRegistration(Integer authUserId);

    @ActivityMethod(scheduleToCloseTimeoutSeconds = 60, taskList = Constants.AUTH_TASK_LIST)
    void confirmRegistration(Integer authUserId, String password);

}
