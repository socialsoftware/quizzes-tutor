package pt.ulisboa.tecnico.socialsoftware.auth.command;

import io.eventuate.tram.commands.consumer.CommandHandlers;
import io.eventuate.tram.commands.consumer.CommandMessage;
import io.eventuate.tram.messaging.common.Message;
import io.eventuate.tram.sagas.participant.SagaCommandHandlersBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import pt.ulisboa.tecnico.socialsoftware.auth.services.AuthUserService;
import pt.ulisboa.tecnico.socialsoftware.common.dtos.execution.CourseExecutionDto;
import pt.ulisboa.tecnico.socialsoftware.common.serviceChannels.ServiceChannels;

import java.util.List;

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
                .onMessage(UndoUpdateCourseExecutionsCommand.class, this::undoUpdateCourseExecutions)
                .onMessage(ConfirmUpdateCourseExecutionsCommand.class, this::confirmUpdateCourseExecutions)
                .onMessage(UndoConfirmRegistrationCommand.class, this::undoConfirmRegistration)
                .onMessage(ConfirmRegistrationCommand.class, this::confirmRegistration)
                .build();
    }

    public Message confirmUpdateCourseExecutions(CommandMessage<ConfirmUpdateCourseExecutionsCommand> cm) {
        logger.info("Received ConfirmUpdateCourseExecutionsCommand");

        Integer authUserId = cm.getCommand().getAuthUserId();
        String ids = cm.getCommand().getIds();
        List<CourseExecutionDto> courseExecutionDtoList = cm.getCommand().getCourseExecutionDtoList();
        String email = cm.getCommand().getEmail();

        try {
            authUserService.confirmUpdateCourseExecutions(authUserId, ids, courseExecutionDtoList, email);
            return withSuccess();
        } catch (Exception e) {
            return withFailure();
        }
    }

    public Message approveAuthUser(CommandMessage<ApproveAuthUserCommand> cm) {
        logger.info("Received ApproveAuthUserCommand");

        Integer userId = cm.getCommand().getUserId();
        Integer authUserId = cm.getCommand().getAuthUserId();
        List<CourseExecutionDto> courseExecutionList = cm.getCommand().getCourseExecutionDtoList();

        try {
            authUserService.approveAuthUser(authUserId, userId, courseExecutionList);
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

    public Message undoUpdateCourseExecutions(CommandMessage<UndoUpdateCourseExecutionsCommand> cm) {
        logger.info("Received UndoUpdateCourseExecutionsCommand");

        Integer authUserId = cm.getCommand().getAuthUserId();

        try {
            authUserService.undoUpdateCourseExecutions(authUserId);
            return withSuccess();
        } catch (Exception e) {
            return withFailure();
        }
    }


    public Message undoConfirmRegistration(CommandMessage<UndoConfirmRegistrationCommand> cm) {
        logger.info("Received UndoConfirmRegistrationCommand");

        Integer authUserId = cm.getCommand().getAuthUserId();

        try {
            authUserService.undoConfirmAuthUserRegistration(authUserId);
            return withSuccess();
        } catch (Exception e) {
            return withFailure();
        }
    }


    public Message confirmRegistration(CommandMessage<ConfirmRegistrationCommand> cm) {
        logger.info("Received ConfirmRegistrationCommand");

        Integer authUserId = cm.getCommand().getAuthUserId();
        String password = cm.getCommand().getPassword();

        try {
            authUserService.confirmAuthUserRegistration(authUserId, password);
            return withSuccess();
        } catch (Exception e) {
            return withFailure();
        }
    }

}
