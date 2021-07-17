package pt.ulisboa.tecnico.socialsoftware.auth.command;

import io.eventuate.tram.commands.common.Command;

public class RejectAuthUserCommand implements Command {

    private Integer authUserId;

    public RejectAuthUserCommand() {
    }

    public RejectAuthUserCommand(Integer authUserId) {
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
        return "RejectAuthUserCommand{" +
                "authUserId=" + authUserId +
                '}';
    }
}
