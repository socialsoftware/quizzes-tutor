package pt.ulisboa.tecnico.socialsoftware.tournament.sagas.participants;

import io.eventuate.tram.sagas.simpledsl.CommandEndpoint;
import io.eventuate.tram.sagas.simpledsl.CommandEndpointBuilder;
import pt.ulisboa.tecnico.socialsoftware.common.commands.execution.GetCourseExecutionCommand;
import pt.ulisboa.tecnico.socialsoftware.common.serviceChannels.ServiceChannels;

public class CourseExecutionServiceProxy {

    public final CommandEndpoint<GetCourseExecutionCommand> getCourseExecution = CommandEndpointBuilder
            .forCommand(GetCourseExecutionCommand.class)
            .withChannel(ServiceChannels.COURSE_EXECUTION_SERVICE_COMMAND_CHANNEL)
            .withReply(Integer.class)
            .build();
}
