package pt.ulisboa.tecnico.socialsoftware.tournament.sagas.participants;

import io.eventuate.tram.commands.common.Success;
import io.eventuate.tram.commands.consumer.CommandWithDestination;
import io.eventuate.tram.sagas.simpledsl.CommandEndpoint;
import io.eventuate.tram.sagas.simpledsl.CommandEndpointBuilder;
import pt.ulisboa.tecnico.socialsoftware.common.commands.answer.GenerateQuizCommand;
import pt.ulisboa.tecnico.socialsoftware.common.commands.answer.SolveQuizCommand;
import pt.ulisboa.tecnico.socialsoftware.common.commands.quiz.DeleteQuizCommand;
import pt.ulisboa.tecnico.socialsoftware.common.commands.quiz.UpdateQuizCommand;
import pt.ulisboa.tecnico.socialsoftware.common.serviceChannels.ServiceChannels;
import pt.ulisboa.tecnico.socialsoftware.tournament.sagas.updateTournament.UpdateTournamentSagaData;

import java.util.function.Function;

public class AnswerServiceProxy {

    public final CommandEndpoint<SolveQuizCommand> solveQuiz = CommandEndpointBuilder
            .forCommand(SolveQuizCommand.class)
            .withChannel(ServiceChannels.ANSWER_SERVICE_COMMAND_CHANNEL)
            .withReply(Success.class)
            .build();

    public final CommandEndpoint<GenerateQuizCommand> generateTournamentQuiz = CommandEndpointBuilder
            .forCommand(GenerateQuizCommand.class)
            .withChannel(ServiceChannels.ANSWER_SERVICE_COMMAND_CHANNEL)
            .withReply(Integer.class)
            .build();

    public final CommandEndpoint<DeleteQuizCommand> deleteTournamentQuiz = CommandEndpointBuilder
            .forCommand(DeleteQuizCommand.class)
            .withChannel(ServiceChannels.ANSWER_SERVICE_COMMAND_CHANNEL)
            .withReply(Success.class)
            .build();
}
