package pt.ulisboa.tecnico.socialsoftware.tournament.sagas.participants;

import io.eventuate.tram.sagas.simpledsl.CommandEndpoint;
import io.eventuate.tram.sagas.simpledsl.CommandEndpointBuilder;
import pt.ulisboa.tecnico.socialsoftware.common.commands.answer.GenerateQuizCommand;
import pt.ulisboa.tecnico.socialsoftware.common.serviceChannels.ServiceChannels;

public class AnswerServiceProxy {

    public final CommandEndpoint<GenerateQuizCommand> generateTournamentQuiz = CommandEndpointBuilder
            .forCommand(GenerateQuizCommand.class)
            .withChannel(ServiceChannels.ANSWER_SERVICE_COMMAND_CHANNEL)
            .withReply(Integer.class)
            .build();
}
