package pt.ulisboa.tecnico.socialsoftware.auth.sagas.confirmRegistration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pt.ulisboa.tecnico.socialsoftware.auth.command.ConfirmRegistrationCommand;
import pt.ulisboa.tecnico.socialsoftware.auth.command.UndoConfirmRegistrationCommand;
import pt.ulisboa.tecnico.socialsoftware.common.commands.user.ActivateUserCommand;

public class ConfirmRegistrationSagaData {

    private final Logger logger = LoggerFactory.getLogger(ConfirmRegistrationSagaData.class);

    private Integer authUserId;
    private Integer userId;
    private String password;

    public ConfirmRegistrationSagaData() {
    }

    public ConfirmRegistrationSagaData(Integer authUserId, Integer userId, String password) {
        this.authUserId = authUserId;
        this.userId = userId;
        this.password = password;
    }

    public Integer getAuthUserId() {
        return authUserId;
    }

    public void setAuthUserId(Integer authUserId) {
        this.authUserId = authUserId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    UndoConfirmRegistrationCommand undoConfirmRegistration() {
        logger.info("Sent UndoConfirmRegistrationCommand");
        return new UndoConfirmRegistrationCommand(getAuthUserId());
    }

    ActivateUserCommand activateUser() {
        logger.info("Sent ActivateUserCommand");
        return new ActivateUserCommand(getUserId());
    }

    ConfirmRegistrationCommand confirmRegistration() {
        logger.info("Sent ConfirmRegistrationCommand");
        return new ConfirmRegistrationCommand(getAuthUserId(), getPassword());
    }

    @Override
    public String toString() {
        return "ConfirmRegistrationSagaData{" +
                "authUserId=" + authUserId +
                ", userId=" + userId +
                ", password='" + password + '\'' +
                '}';
    }
}
