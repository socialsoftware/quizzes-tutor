package pt.ulisboa.tecnico.socialsoftware.auth.activity.confirmRegistration;

public interface ConfirmRegistrationActivities {

    void undoConfirmRegistration(Integer authUserId);

    void confirmRegistration(Integer authUserId, String password);

}
