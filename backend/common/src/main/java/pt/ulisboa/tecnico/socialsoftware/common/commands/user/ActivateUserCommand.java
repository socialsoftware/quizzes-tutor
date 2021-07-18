package pt.ulisboa.tecnico.socialsoftware.common.commands.user;

import io.eventuate.tram.commands.common.Command;

public class ActivateUserCommand implements Command {

    private Integer userId;

    public ActivateUserCommand() {
    }

    public ActivateUserCommand(Integer userId) {
        this.userId = userId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "ActivateUserCommand{" +
                "userId=" + userId +
                '}';
    }
}
