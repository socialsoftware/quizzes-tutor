package pt.ulisboa.tecnico.socialsoftware.auth.sagas.participants;

import io.eventuate.tram.commands.common.Success;
import io.eventuate.tram.sagas.simpledsl.CommandEndpoint;
import io.eventuate.tram.sagas.simpledsl.CommandEndpointBuilder;
import pt.ulisboa.tecnico.socialsoftware.common.commands.user.AddCourseExecutionsCommand;
import pt.ulisboa.tecnico.socialsoftware.common.serviceChannels.ServiceChannels;

public class CourseExecutionServiceProxy {

    public final CommandEndpoint<AddCourseExecutionsCommand> addCourseExecutions = CommandEndpointBuilder
            .forCommand(AddCourseExecutionsCommand.class)
            .withChannel(ServiceChannels.COURSE_EXECUTION_SERVICE_COMMAND_CHANNEL)
            .withReply(Success.class)
            .build();
}
