package pt.ulisboa.tecnico.socialsoftware.auth.command;

import io.eventuate.tram.commands.consumer.CommandHandlers;
import io.eventuate.tram.commands.consumer.CommandMessage;
import io.eventuate.tram.messaging.common.Message;
import io.eventuate.tram.sagas.participant.SagaCommandHandlersBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import pt.ulisboa.tecnico.socialsoftware.auth.services.AuthUserService;
import pt.ulisboa.tecnico.socialsoftware.common.commands.auth.ApproveAuthUserCommand;
import pt.ulisboa.tecnico.socialsoftware.common.commands.auth.RejectAuthUserCommand;
import pt.ulisboa.tecnico.socialsoftware.common.serviceChannels.ServiceChannels;

import static io.eventuate.tram.commands.consumer.CommandHandlerReplyBuilder.withFailure;
import static io.eventuate.tram.commands.consumer.CommandHandlerReplyBuilder.withSuccess;

public class AuthServiceCommandHandlers {
    private Logger logger = LoggerFactory.getLogger(AuthServiceCommandHandlers.class);

    @Autowired
    private AuthUserService authUserService;

    /**
     * Create command handlers.
     *
     * <p>Map each command to different functions to handle.
     *
     * @return The {code CommandHandlers} object.
     */
    public CommandHandlers commandHandlers() {
        return SagaCommandHandlersBuilder
                .fromChannel(ServiceChannels.AUTH_USER_SERVICE_COMMAND_CHANNEL)
                .onMessage(ApproveAuthUserCommand.class, this::approveAuthUser)
                .onMessage(RejectAuthUserCommand.class, this::rejectAuthUser)
                .build();
    }

    public Message approveAuthUser(CommandMessage<ApproveAuthUserCommand> cm) {
        logger.info("Received ApproveAuthUserCommand");

        Integer userId = cm.getCommand().getUserId();
        Integer authUserId = cm.getCommand().getAuthUserId();
        Integer courseExecutionId = cm.getCommand().getCourseExecutionId();
        boolean isActive = cm.getCommand().isActive();

        try {
            authUserService.approveAuthUser(authUserId, userId, courseExecutionId, isActive);
            return withSuccess();
        } catch (Exception e) {
            return withFailure();
        }
    }

    public Message rejectAuthUser(CommandMessage<RejectAuthUserCommand> cm) {
        logger.info("Received RejectAuthUserCommand");

        Integer authUserId = cm.getCommand().getAuthUserId();

        try {
            authUserService.rejectAuthUser(authUserId);
            return withSuccess();
        } catch (Exception e) {
            return withFailure();
        }
    }

}
