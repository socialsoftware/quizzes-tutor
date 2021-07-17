package pt.ulisboa.tecnico.socialsoftware.common.commands.user;

import io.eventuate.tram.commands.common.Command;

public class RejectUserCommand implements Command {

    private Integer userId;

    public RejectUserCommand() {
    }

    public RejectUserCommand(Integer userId) {
        this.userId = userId;
    }

    public Integer getUserId() {
        return userId;
    }

    @Override
    public String toString() {
        return "RejectUserCommand{" +
                "userId=" + userId +
                '}';
    }
}
