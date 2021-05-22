package pt.ulisboa.tecnico.socialsoftware.tutor.execution.command;

import io.eventuate.tram.commands.consumer.CommandHandlers;
import io.eventuate.tram.commands.consumer.CommandMessage;
import io.eventuate.tram.messaging.common.Message;
import io.eventuate.tram.sagas.participant.SagaCommandHandlersBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import pt.ulisboa.tecnico.socialsoftware.common.commands.execution.AddCourseExecutionCommand;
import pt.ulisboa.tecnico.socialsoftware.common.commands.execution.RemoveCourseExecutionCommand;
import pt.ulisboa.tecnico.socialsoftware.common.serviceChannels.ServiceChannels;
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.CourseExecutionService;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.command.UserServiceCommandHandlers;

import static io.eventuate.tram.commands.consumer.CommandHandlerReplyBuilder.withFailure;
import static io.eventuate.tram.commands.consumer.CommandHandlerReplyBuilder.withSuccess;

public class CourseExecutionServiceCommandHandlers {
    private Logger logger = LoggerFactory.getLogger(UserServiceCommandHandlers.class);

    @Autowired
    private CourseExecutionService courseExecutionService;

    /**
     * Create command handlers.
     *
     * <p>Map each command to different functions to handle.
     *
     * @return The {code CommandHandlers} object.
     */
    public CommandHandlers commandHandlers() {
        return SagaCommandHandlersBuilder
                .fromChannel(ServiceChannels.COURSE_EXECUTION_SERVICE_COMMAND_CHANNEL)
                .onMessage(AddCourseExecutionCommand.class, this::addCourseExecution)
                .onMessage(RemoveCourseExecutionCommand.class, this::removeCourseExecution)
                .build();
    }

    public Message addCourseExecution(CommandMessage<AddCourseExecutionCommand> cm) {
        logger.info("Received AddCourseExecutionCommand");

        Integer userId = cm.getCommand().getUserId();
        Integer courseExecutionId = cm.getCommand().getCourseExecutionId();

        try {
            courseExecutionService.addUserToTecnicoCourseExecution(userId, courseExecutionId);
            return withSuccess();
        } catch (Exception e) {
            return withFailure();
        }
    }

    public Message removeCourseExecution(CommandMessage<RemoveCourseExecutionCommand> cm) {
        logger.info("Received RemoveCourseExecutionCommand");

        Integer userId = cm.getCommand().getUserId();
        Integer courseExecutionId = cm.getCommand().getCourseExecutionId();

        try {
            courseExecutionService.removeUserFromTecnicoCourseExecution(userId, courseExecutionId);
            return withSuccess();
        } catch (Exception e) {
            return withFailure();
        }
    }
}
