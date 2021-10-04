package pt.ulisboa.tecnico.socialsoftware.tournament.sagas.participants;

import io.eventuate.tram.commands.common.Success;
import io.eventuate.tram.sagas.simpledsl.CommandEndpoint;
import io.eventuate.tram.sagas.simpledsl.CommandEndpointBuilder;
import pt.ulisboa.tecnico.socialsoftware.common.serviceChannels.ServiceChannels;
import pt.ulisboa.tecnico.socialsoftware.tournament.command.*;

public class TournamentServiceProxy {

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

    public final CommandEndpoint<BeginUpdateTournamentCommand> beginUpdate = CommandEndpointBuilder
            .forCommand(BeginUpdateTournamentCommand.class)
            .withChannel(ServiceChannels.TOURNAMENT_SERVICE_COMMAND_CHANNEL)
            .withReply(Success.class)
            .build();

    public final CommandEndpoint<ConfirmCreateTournamentCommand> confirmCreate = CommandEndpointBuilder
            .forCommand(ConfirmCreateTournamentCommand.class)
            .withChannel(ServiceChannels.TOURNAMENT_SERVICE_COMMAND_CHANNEL)
            .withReply(Success.class)
            .build();

    public final CommandEndpoint<StoreTournamentTopicsCommand> storeTopics = CommandEndpointBuilder
            .forCommand(StoreTournamentTopicsCommand.class)
            .withChannel(ServiceChannels.TOURNAMENT_SERVICE_COMMAND_CHANNEL)
            .withReply(Success.class)
            .build();

    public final CommandEndpoint<StoreTournamentCourseExecutionCommand> storeCourseExecution = CommandEndpointBuilder
            .forCommand(StoreTournamentCourseExecutionCommand.class)
            .withChannel(ServiceChannels.TOURNAMENT_SERVICE_COMMAND_CHANNEL)
            .withReply(Success.class)
            .build();

    public final CommandEndpoint<UndoUpdateTournamentCommand> undoUpdate = CommandEndpointBuilder
            .forCommand(UndoUpdateTournamentCommand.class)
            .withChannel(ServiceChannels.TOURNAMENT_SERVICE_COMMAND_CHANNEL)
            .withReply(Success.class)
            .build();

    public final CommandEndpoint<UpdateTopicsTournamentCommand> updateTopics = CommandEndpointBuilder
            .forCommand(UpdateTopicsTournamentCommand.class)
            .withChannel(ServiceChannels.TOURNAMENT_SERVICE_COMMAND_CHANNEL)
            .withReply(Success.class)
            .build();

    public final CommandEndpoint<UndoUpdateTopicsTournamentCommand> undoUpdateTopics = CommandEndpointBuilder
            .forCommand(UndoUpdateTopicsTournamentCommand.class)
            .withChannel(ServiceChannels.TOURNAMENT_SERVICE_COMMAND_CHANNEL)
            .withReply(Success.class)
            .build();
}
