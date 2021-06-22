package pt.ulisboa.tecnico.socialsoftware.auth.command;

import io.eventuate.tram.commands.common.Command;

public class ConfirmRegistrationCommand implements Command {

    private Integer authUserId;
    private String password;

    public ConfirmRegistrationCommand() {
    }

    public ConfirmRegistrationCommand(Integer authUserId, String password) {
        this.authUserId = authUserId;
        this.password = password;
    }

    public Integer getAuthUserId() {
        return authUserId;
    }

    public void setAuthUserId(Integer authUserId) {
        this.authUserId = authUserId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "ConfirmRegistrationCommand{" +
                "authUserId=" + authUserId +
                ", password='" + password + '\'' +
                '}';
    }
}
