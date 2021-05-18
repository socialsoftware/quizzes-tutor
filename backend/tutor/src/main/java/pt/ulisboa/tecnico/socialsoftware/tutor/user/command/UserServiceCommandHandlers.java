package pt.ulisboa.tecnico.socialsoftware.tutor.user.command;

import io.eventuate.tram.commands.consumer.CommandHandlers;
import io.eventuate.tram.commands.consumer.CommandMessage;
import io.eventuate.tram.messaging.common.Message;
import io.eventuate.tram.sagas.participant.SagaCommandHandlersBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import pt.ulisboa.tecnico.socialsoftware.common.commands.auth.RejectAuthUserCommand;
import pt.ulisboa.tecnico.socialsoftware.common.commands.user.CreateUserCommand;
import pt.ulisboa.tecnico.socialsoftware.common.commands.user.RejectUserCommand;
import pt.ulisboa.tecnico.socialsoftware.common.dtos.user.Role;
import pt.ulisboa.tecnico.socialsoftware.common.dtos.user.UserDto;
import pt.ulisboa.tecnico.socialsoftware.common.serviceChannels.ServiceChannels;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserService;

import static io.eventuate.tram.commands.consumer.CommandHandlerReplyBuilder.withFailure;
import static io.eventuate.tram.commands.consumer.CommandHandlerReplyBuilder.withSuccess;

public class UserServiceCommandHandlers {
    private Logger logger = LoggerFactory.getLogger(UserServiceCommandHandlers.class);

    @Autowired
    private UserService userService;

    /**
     * Create command handlers.
     *
     * <p>Map each command to different functions to handle.
     *
     * @return The {code CommandHandlers} object.
     */
    public CommandHandlers commandHandlers() {
        return SagaCommandHandlersBuilder
                .fromChannel(ServiceChannels.USER_SERVICE_COMMAND_CHANNEL)
                .onMessage(CreateUserCommand.class, this::createUser)
                .onMessage(RejectUserCommand.class, this::rejectUser)
                .build();
    }

    public Message createUser(CommandMessage<CreateUserCommand> cm) {
        logger.info("Received CreateUserCommand");

        String name = cm.getCommand().getName();
        Role role = cm.getCommand().getRole();
        String username = cm.getCommand().getUsername();
        boolean isActive = cm.getCommand().isActive();
        boolean isAdmin = cm.getCommand().isAdmin();

        try {
            UserDto userDto = userService.createUser(name, role, username, isActive, isAdmin);
            return withSuccess(userDto);
        } catch (Exception e) {
            return withFailure();
        }
    }

    public Message rejectUser(CommandMessage<RejectUserCommand> cm) {
        logger.info("Received RejectUserCommand");

        Integer userId = cm.getCommand().getUserId();

        try {
            userService.deleteUser(userId);
            return withSuccess();
        } catch (Exception e) {
            return withFailure();
        }
    }
}
