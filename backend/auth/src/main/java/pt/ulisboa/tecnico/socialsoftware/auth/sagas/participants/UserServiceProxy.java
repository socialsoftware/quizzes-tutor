package pt.ulisboa.tecnico.socialsoftware.auth.sagas.participants;

import io.eventuate.tram.commands.common.Success;
import io.eventuate.tram.sagas.simpledsl.CommandEndpoint;
import io.eventuate.tram.sagas.simpledsl.CommandEndpointBuilder;
import pt.ulisboa.tecnico.socialsoftware.common.commands.user.*;
import pt.ulisboa.tecnico.socialsoftware.common.dtos.user.UserDto;
import pt.ulisboa.tecnico.socialsoftware.common.serviceChannels.ServiceChannels;

public class UserServiceProxy {

    public final CommandEndpoint<CreateUserCommand> create = CommandEndpointBuilder
            .forCommand(CreateUserCommand.class)
            .withChannel(ServiceChannels.USER_SERVICE_COMMAND_CHANNEL)
            .withReply(UserDto.class)
            .build();

    public final CommandEndpoint<RejectUserCommand> reject = CommandEndpointBuilder
            .forCommand(RejectUserCommand.class)
            .withChannel(ServiceChannels.USER_SERVICE_COMMAND_CHANNEL)
            .withReply(Success.class)
            .build();

    public final CommandEndpoint<AddCourseExecutionsCommand> addCourseExecution = CommandEndpointBuilder
            .forCommand(AddCourseExecutionsCommand.class)
            .withChannel(ServiceChannels.USER_SERVICE_COMMAND_CHANNEL)
            .withReply(Success.class)
            .build();

    public final CommandEndpoint<RemoveCourseExecutionsCommand> removeCourseExecution = CommandEndpointBuilder
            .forCommand(RemoveCourseExecutionsCommand.class)
            .withChannel(ServiceChannels.USER_SERVICE_COMMAND_CHANNEL)
            .withReply(Success.class)
            .build();


    public final CommandEndpoint<ActivateUserCommand> activateUser = CommandEndpointBuilder
            .forCommand(ActivateUserCommand.class)
            .withChannel(ServiceChannels.USER_SERVICE_COMMAND_CHANNEL)
            .withReply(Success.class)
            .build();


}
