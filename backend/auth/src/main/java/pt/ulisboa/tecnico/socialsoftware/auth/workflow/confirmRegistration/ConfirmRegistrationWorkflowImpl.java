package pt.ulisboa.tecnico.socialsoftware.auth.workflow.confirmRegistration;

import com.uber.cadence.workflow.ActivityException;
import com.uber.cadence.workflow.Saga;
import com.uber.cadence.workflow.Workflow;

import pt.ulisboa.tecnico.socialsoftware.auth.activity.confirmRegistration.ConfirmRegistrationActivities;
import pt.ulisboa.tecnico.socialsoftware.common.activity.UserActivities;

public class ConfirmRegistrationWorkflowImpl implements ConfirmRegistrationWorkflow {

    private final ConfirmRegistrationActivities confirmRegistrationActivities = Workflow
            .newActivityStub(ConfirmRegistrationActivities.class);

    private final UserActivities userActivities = Workflow.newActivityStub(UserActivities.class);

    @Override
    public void confirmRegistration(Integer authUserId, Integer userId, String password) {
        Saga.Options sagaOptions = new Saga.Options.Builder()
                .setParallelCompensation(false)
                .build();
        Saga saga = new Saga(sagaOptions);
        try {
            saga.addCompensation(confirmRegistrationActivities::undoConfirmRegistration, authUserId);

            userActivities.activateUser(authUserId);

            confirmRegistrationActivities.confirmRegistration(authUserId, password);
        } catch (ActivityException e) {
            saga.compensate();
            throw e;
        }
    }

}
