package pt.ulisboa.tecnico.socialsoftware.tournament.sagas.participants;

import io.eventuate.tram.sagas.simpledsl.CommandEndpoint;
import io.eventuate.tram.sagas.simpledsl.CommandEndpointBuilder;
import pt.ulisboa.tecnico.socialsoftware.common.commands.question.GetTopicsCommand;
import pt.ulisboa.tecnico.socialsoftware.common.dtos.tournament.FindTopicsDto;
import pt.ulisboa.tecnico.socialsoftware.common.serviceChannels.ServiceChannels;

public class QuestionServiceProxy {

    public final CommandEndpoint<GetTopicsCommand> getTopics = CommandEndpointBuilder
            .forCommand(GetTopicsCommand.class)
            .withChannel(ServiceChannels.QUESTION_SERVICE_COMMAND_CHANNEL)
            .withReply(FindTopicsDto.class)
            .build();
}
