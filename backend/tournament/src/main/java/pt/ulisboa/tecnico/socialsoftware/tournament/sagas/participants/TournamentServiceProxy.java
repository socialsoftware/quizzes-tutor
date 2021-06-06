package pt.ulisboa.tecnico.socialsoftware.tournament.sagas.participants;

import io.eventuate.tram.commands.common.Success;
import io.eventuate.tram.sagas.simpledsl.CommandEndpoint;
import io.eventuate.tram.sagas.simpledsl.CommandEndpointBuilder;
import pt.ulisboa.tecnico.socialsoftware.common.serviceChannels.ServiceChannels;
import pt.ulisboa.tecnico.socialsoftware.tournament.command.*;
import pt.ulisboa.tecnico.socialsoftware.tournament.sagas.createTournament.CreateTournamentSagaData;

import java.util.function.Predicate;

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

    public final CommandEndpoint<RejectCreateTournamentCommand> rejectCreate = CommandEndpointBuilder
            .forCommand(RejectCreateTournamentCommand.class)
            .withChannel(ServiceChannels.TOURNAMENT_SERVICE_COMMAND_CHANNEL)
            .withReply(Success.class)
            .build();

    public final CommandEndpoint<ConfirmCreateTournamentCommand> confirmCreate = CommandEndpointBuilder
            .forCommand(ConfirmCreateTournamentCommand.class)
            .withChannel(ServiceChannels.TOURNAMENT_SERVICE_COMMAND_CHANNEL)
            .withReply(Success.class)
            .build();
}
