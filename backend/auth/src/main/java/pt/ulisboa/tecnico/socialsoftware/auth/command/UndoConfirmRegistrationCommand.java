package pt.ulisboa.tecnico.socialsoftware.auth.command;

import io.eventuate.tram.commands.common.Command;

public class UndoConfirmRegistrationCommand implements Command {

    private Integer authUserId;

    public UndoConfirmRegistrationCommand() {
    }

    public UndoConfirmRegistrationCommand(Integer authUserId) {
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
        return "UndoConfirmRegistrationCommand{" +
                "authUserId=" + authUserId +
                '}';
    }
}
