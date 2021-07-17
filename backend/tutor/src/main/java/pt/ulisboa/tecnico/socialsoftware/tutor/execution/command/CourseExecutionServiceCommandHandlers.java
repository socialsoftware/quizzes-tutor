package pt.ulisboa.tecnico.socialsoftware.tutor.execution.command;

import io.eventuate.tram.commands.consumer.CommandHandlers;
import io.eventuate.tram.commands.consumer.CommandMessage;
import io.eventuate.tram.messaging.common.Message;
import io.eventuate.tram.sagas.participant.SagaCommandHandlersBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import pt.ulisboa.tecnico.socialsoftware.common.commands.execution.GetCourseExecutionCommand;
import pt.ulisboa.tecnico.socialsoftware.common.commands.execution.RemoveCourseExecutionCommand;
import pt.ulisboa.tecnico.socialsoftware.common.commands.user.AddCourseExecutionsCommand;
import pt.ulisboa.tecnico.socialsoftware.common.dtos.execution.CourseExecutionDto;
import pt.ulisboa.tecnico.socialsoftware.common.serviceChannels.ServiceChannels;
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.CourseExecutionService;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.command.UserServiceCommandHandlers;

import java.util.List;

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
                .onMessage(AddCourseExecutionsCommand.class, this::addCourseExecutions)
                .onMessage(RemoveCourseExecutionCommand.class, this::removeCourseExecution)
                .onMessage(GetCourseExecutionCommand.class, this::getCourseExecution)
                .build();
    }

    private Message getCourseExecution(CommandMessage<GetCourseExecutionCommand> cm) {
        logger.info("Received GetCourseExecutionCommand");

        Integer courseExecutionId = cm.getCommand().getCourseExecutionId();

        try {
            CourseExecutionDto courseExecutionDto = courseExecutionService.getCourseExecutionById(courseExecutionId);
            return withSuccess(courseExecutionDto);
        } catch (Exception e) {
            return withFailure();
        }
    }

    public Message addCourseExecutions(CommandMessage<AddCourseExecutionsCommand> cm) {
        logger.info("Received AddCourseExecutionsCommand");

        Integer userId = cm.getCommand().getUserId();
        List<CourseExecutionDto> courseExecutionDtoList = cm.getCommand().getCourseExecutionDtoList();
        try {
            courseExecutionService.addCourseExecutions(userId, courseExecutionDtoList);
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
