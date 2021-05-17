package pt.ulisboa.tecnico.socialsoftware.auth.sagas.participants;

import io.eventuate.tram.commands.common.Success;
import io.eventuate.tram.sagas.simpledsl.CommandEndpoint;
import io.eventuate.tram.sagas.simpledsl.CommandEndpointBuilder;
import pt.ulisboa.tecnico.socialsoftware.common.commands.auth.ApproveAuthUserCommand;
import pt.ulisboa.tecnico.socialsoftware.common.commands.auth.RejectAuthUserCommand;
import pt.ulisboa.tecnico.socialsoftware.common.serviceChannels.ServiceChannels;

public class AuthUserServiceProxy {

    public final CommandEndpoint<ApproveAuthUserCommand> approve = CommandEndpointBuilder
            .forCommand(ApproveAuthUserCommand.class)
            .withChannel(ServiceChannels.AUTH_USER_SERVICE_COMMAND_CHANNEL)
            .withReply(Success.class)
            .build();

    public final CommandEndpoint<RejectAuthUserCommand> reject = CommandEndpointBuilder
            .forCommand(RejectAuthUserCommand.class)
            .withChannel(ServiceChannels.AUTH_USER_SERVICE_COMMAND_CHANNEL)
            .withReply(Success.class)
            .build();
}
