package pt.ulisboa.tecnico.socialsoftware.auth.sagas.participants;

import io.eventuate.tram.commands.common.Success;
import io.eventuate.tram.sagas.simpledsl.CommandEndpoint;
import io.eventuate.tram.sagas.simpledsl.CommandEndpointBuilder;
import pt.ulisboa.tecnico.socialsoftware.common.commands.execution.AddCourseExecutionCommand;
import pt.ulisboa.tecnico.socialsoftware.common.commands.execution.RemoveCourseExecutionCommand;
import pt.ulisboa.tecnico.socialsoftware.common.serviceChannels.ServiceChannels;

public class CourseExecutionServiceProxy {

    public final CommandEndpoint<AddCourseExecutionCommand> addCourseExecution = CommandEndpointBuilder
            .forCommand(AddCourseExecutionCommand.class)
            .withChannel(ServiceChannels.COURSE_EXECUTION_SERVICE_COMMAND_CHANNEL)
            .withReply(Success.class)
            .build();

    public final CommandEndpoint<RemoveCourseExecutionCommand> removeCourseExecution = CommandEndpointBuilder
            .forCommand(RemoveCourseExecutionCommand.class)
            .withChannel(ServiceChannels.COURSE_EXECUTION_SERVICE_COMMAND_CHANNEL)
            .withReply(Success.class)
            .build();
}
