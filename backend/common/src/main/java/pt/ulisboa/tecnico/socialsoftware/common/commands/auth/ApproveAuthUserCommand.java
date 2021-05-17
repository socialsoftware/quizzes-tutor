package pt.ulisboa.tecnico.socialsoftware.common.commands.auth;

import io.eventuate.tram.commands.CommandDestination;
import io.eventuate.tram.commands.common.Command;
import pt.ulisboa.tecnico.socialsoftware.common.serviceChannels.ServiceChannels;

@CommandDestination(ServiceChannels.AUTH_USER_SERVICE_COMMAND_CHANNEL)
public class ApproveAuthUserCommand implements Command {

    private Integer userId;
    private Integer authUserId;

    public ApproveAuthUserCommand() {
    }

    public ApproveAuthUserCommand(Integer authUserId, Integer userId) {
        this.userId = userId;
        this.authUserId = authUserId;
    }

    public Integer getUserId() {
        return userId;
    }

    public Integer getAuthUserId() {
        return authUserId;
    }
}
