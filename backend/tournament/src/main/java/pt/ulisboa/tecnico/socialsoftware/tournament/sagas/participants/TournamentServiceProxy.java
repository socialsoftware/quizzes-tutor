package pt.ulisboa.tecnico.socialsoftware.tournament.sagas.participants;

import io.eventuate.tram.commands.common.Success;
import io.eventuate.tram.sagas.simpledsl.CommandEndpoint;
import io.eventuate.tram.sagas.simpledsl.CommandEndpointBuilder;
import pt.ulisboa.tecnico.socialsoftware.common.serviceChannels.ServiceChannels;
import pt.ulisboa.tecnico.socialsoftware.tournament.command.*;

public class TournamentServiceProxy {

    public final CommandEndpoint<BeginRemoveTournamentCommand> beginRemove = CommandEndpointBuilder
            .forCommand(BeginRemoveTournamentCommand.class)
            .withChannel(ServiceChannels.TOURNAMENT_SERVICE_COMMAND_CHANNEL)
            .withReply(Success.class)
            .build();

    public final CommandEndpoint<UndoRemoveTournamentCommand> undoRemove = CommandEndpointBuilder
            .forCommand(UndoRemoveTournamentCommand.class)
            .withChannel(ServiceChannels.TOURNAMENT_SERVICE_COMMAND_CHANNEL)
            .withReply(Success.class)
            .build();

    public final CommandEndpoint<ConfirmRemoveTournamentCommand> confirmRemove = CommandEndpointBuilder
            .forCommand(ConfirmRemoveTournamentCommand.class)
            .withChannel(ServiceChannels.TOURNAMENT_SERVICE_COMMAND_CHANNEL)
            .withReply(Success.class)
            .build();

    public final CommandEndpoint<BeginSolveTournamentQuizCommand> beginSolve = CommandEndpointBuilder
            .forCommand(BeginSolveTournamentQuizCommand.class)
            .withChannel(ServiceChannels.TOURNAMENT_SERVICE_COMMAND_CHANNEL)
            .withReply(Success.class)
            .build();

    public final CommandEndpoint<UndoSolveTournamentQuizCommand> undoSolve = CommandEndpointBuilder
            .forCommand(UndoSolveTournamentQuizCommand.class)
            .withChannel(ServiceChannels.TOURNAMENT_SERVICE_COMMAND_CHANNEL)
            .withReply(Success.class)
            .build();

    public final CommandEndpoint<ConfirmSolveTournamentQuizCommand> confirmSolve = CommandEndpointBuilder
            .forCommand(ConfirmSolveTournamentQuizCommand.class)
            .withChannel(ServiceChannels.TOURNAMENT_SERVICE_COMMAND_CHANNEL)
            .withReply(Success.class)
            .build();

    public final CommandEndpoint<BeginUpdateTournamentQuizCommand> beginUpdate = CommandEndpointBuilder
            .forCommand(BeginUpdateTournamentQuizCommand.class)
            .withChannel(ServiceChannels.TOURNAMENT_SERVICE_COMMAND_CHANNEL)
            .withReply(Success.class)
            .build();

    public final CommandEndpoint<UndoUpdateTournamentQuizCommand> undoUpdate = CommandEndpointBuilder
            .forCommand(UndoUpdateTournamentQuizCommand.class)
            .withChannel(ServiceChannels.TOURNAMENT_SERVICE_COMMAND_CHANNEL)
            .withReply(Success.class)
            .build();

    public final CommandEndpoint<ConfirmUpdateTournamentQuizCommand> confirmUpdate = CommandEndpointBuilder
            .forCommand(ConfirmUpdateTournamentQuizCommand.class)
            .withChannel(ServiceChannels.TOURNAMENT_SERVICE_COMMAND_CHANNEL)
            .withReply(Success.class)
            .build();
}
