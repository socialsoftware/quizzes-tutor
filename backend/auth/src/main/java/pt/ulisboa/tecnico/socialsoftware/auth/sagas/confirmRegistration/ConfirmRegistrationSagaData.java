package pt.ulisboa.tecnico.socialsoftware.auth.sagas.confirmRegistration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import pt.ulisboa.tecnico.socialsoftware.common.commands.auth.BeginConfirmRegistrationCommand;
import pt.ulisboa.tecnico.socialsoftware.common.commands.auth.ConfirmRegistrationCommand;
import pt.ulisboa.tecnico.socialsoftware.common.commands.auth.UndoConfirmRegistrationCommand;
import pt.ulisboa.tecnico.socialsoftware.common.commands.user.ActivateUserCommand;

public class ConfirmRegistrationSagaData {

    private final Logger logger = LoggerFactory.getLogger(ConfirmRegistrationSagaData.class);

    private Integer authUserId;
    private Integer userId;
    private PasswordEncoder passwordEncoder;
    private String password;

    public ConfirmRegistrationSagaData() {
    }

    public ConfirmRegistrationSagaData(Integer authUserId, Integer userId, PasswordEncoder passwordEncoder, String password) {
        this.authUserId = authUserId;
        this.userId = userId;
        this.passwordEncoder = passwordEncoder;
        this.password = password;
    }

    public Logger getLogger() {
        return logger;
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

    public PasswordEncoder getPasswordEncoder() {
        return passwordEncoder;
    }

    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
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

    BeginConfirmRegistrationCommand beginConfirmRegistration() {
        logger.info("Sent BeginConfirmRegistrationCommand");
        return new BeginConfirmRegistrationCommand(getAuthUserId());
    }

    ActivateUserCommand activateUser() {
        logger.info("Sent ActivateUserCommand");
        return new ActivateUserCommand(getUserId());
    }

    ConfirmRegistrationCommand confirmRegistration() {
        logger.info("Sent ConfirmRegistrationCommand");
        return new ConfirmRegistrationCommand(getAuthUserId(), getPasswordEncoder(), getPassword());
    }
}
