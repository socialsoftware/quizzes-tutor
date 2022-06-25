package pt.ulisboa.tecnico.socialsoftware.auth.activity.confirmRegistration;

import pt.ulisboa.tecnico.socialsoftware.auth.services.local.AuthUserProvidedService;

public class ConfirmRegistrationActivitiesImpl implements ConfirmRegistrationActivities {

    private final AuthUserProvidedService authUserService;

    public ConfirmRegistrationActivitiesImpl(AuthUserProvidedService authUserService) {
        this.authUserService = authUserService;
    }

    @Override
    public void undoConfirmRegistration(Integer authUserId) {
        authUserService.undoConfirmAuthUserRegistration(authUserId);

    }

    @Override
    public void confirmRegistration(Integer authUserId, String password) {
        authUserService.confirmAuthUserRegistration(authUserId, password);
    }

}
