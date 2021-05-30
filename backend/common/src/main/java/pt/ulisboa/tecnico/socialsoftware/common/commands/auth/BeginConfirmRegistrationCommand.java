package pt.ulisboa.tecnico.socialsoftware.common.commands.auth;

import io.eventuate.tram.commands.common.Command;

public class BeginConfirmRegistrationCommand implements Command {

    private Integer authUserId;

    public BeginConfirmRegistrationCommand() {
    }

    public BeginConfirmRegistrationCommand(Integer authUserId) {
        this.authUserId = authUserId;
    }

    public Integer getAuthUserId() {
        return authUserId;
    }

    public void setAuthUserId(Integer authUserId) {
        this.authUserId = authUserId;
    }

    @Override
    public String toString() {
        return "BeginConfirmRegistrationCommand{" +
                "authUserId=" + authUserId +
                '}';
    }
}
