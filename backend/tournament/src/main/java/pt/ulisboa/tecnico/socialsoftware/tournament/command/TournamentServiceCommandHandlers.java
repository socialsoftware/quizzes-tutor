package pt.ulisboa.tecnico.socialsoftware.tournament.command;

import io.eventuate.tram.commands.consumer.CommandHandlers;
import io.eventuate.tram.commands.consumer.CommandMessage;
import io.eventuate.tram.messaging.common.Message;
import io.eventuate.tram.sagas.participant.SagaCommandHandlersBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import pt.ulisboa.tecnico.socialsoftware.common.dtos.tournament.TournamentDto;
import pt.ulisboa.tecnico.socialsoftware.common.serviceChannels.ServiceChannels;
import pt.ulisboa.tecnico.socialsoftware.tournament.domain.TournamentCourseExecution;
import pt.ulisboa.tecnico.socialsoftware.tournament.domain.TournamentTopic;
import pt.ulisboa.tecnico.socialsoftware.tournament.services.local.TournamentService;

import java.util.Set;

import static io.eventuate.tram.commands.consumer.CommandHandlerReplyBuilder.withFailure;
import static io.eventuate.tram.commands.consumer.CommandHandlerReplyBuilder.withSuccess;

public class TournamentServiceCommandHandlers {
    private Logger logger = LoggerFactory.getLogger(TournamentServiceCommandHandlers.class);

    @Autowired
    private TournamentService tournamentService;

    /**
     * Create command handlers.
     *
     * <p>Map each command to different functions to handle.
     *
     * @return The {code CommandHandlers} object.
     */
    public CommandHandlers commandHandlers() {
        return SagaCommandHandlersBuilder
                .fromChannel(ServiceChannels.TOURNAMENT_SERVICE_COMMAND_CHANNEL)
                .onMessage(BeginRemoveTournamentCommand.class, this::beginRemove)
                .onMessage(ConfirmRemoveTournamentCommand.class, this::confirmRemove)
                .onMessage(UndoRemoveTournamentCommand.class, this::undoRemove)
                .onMessage(BeginUpdateTournamentQuizCommand.class, this::beginUpdate)
                .onMessage(UndoUpdateTournamentQuizCommand.class, this::undoUpdate)
                .onMessage(ConfirmUpdateTournamentQuizCommand.class, this::confirmUpdate)
                .onMessage(ConfirmCreateTournamentCommand.class, this::confirmCreate)
                .onMessage(RejectCreateTournamentCommand.class, this::rejectCreate)
                .build();
    }

    public Message rejectCreate(CommandMessage<RejectCreateTournamentCommand> cm) {
        logger.info("Received RejectCreateTournamentCommand");

        Integer tournamentId = cm.getCommand().getTournamentId();

        try {
            tournamentService.rejectCreate(tournamentId);
            return withSuccess();
        } catch (Exception e) {
            return withFailure();
        }
    }

    public Message confirmCreate(CommandMessage<ConfirmCreateTournamentCommand> cm) {
        logger.info("Received ConfirmCreateTournamentCommand");

        Integer tournamentId = cm.getCommand().getTournamentId();
        Integer quizId = cm.getCommand().getQuizId();
        Set<TournamentTopic> topics = cm.getCommand().getTopics();
        TournamentCourseExecution courseExecution = cm.getCommand().getTournamentCourseExecution();

        try {
            tournamentService.confirmCreate(tournamentId, quizId, topics, courseExecution);
            return withSuccess();
        } catch (Exception e) {
            return withFailure();
        }
    }

    public Message confirmUpdate(CommandMessage<ConfirmUpdateTournamentQuizCommand> cm) {
        logger.info("Received ConfirmUpdateTournamentQuizCommand");

        Integer tournamentId = cm.getCommand().getTournamentId();
        TournamentDto tournamentDto = cm.getCommand().getTournamentDto();
        Set<TournamentTopic> topics = cm.getCommand().getTopics();

        try {
            tournamentService.confirmUpdate(tournamentId, tournamentDto, topics);
            return withSuccess();
        } catch (Exception e) {
            return withFailure();
        }
    }

    public Message undoUpdate(CommandMessage<UndoUpdateTournamentQuizCommand> cm) {
        logger.info("Received UndoUpdateTournamentQuizCommand");

        Integer tournamentId = cm.getCommand().getTournamentId();

        try {
            tournamentService.undoUpdate(tournamentId);
            return withSuccess();
        } catch (Exception e) {
            return withFailure();
        }
    }

    public Message beginUpdate(CommandMessage<BeginUpdateTournamentQuizCommand> cm) {
        logger.info("Received BeginUpdateTournamentQuizCommand");

        Integer tournamentId = cm.getCommand().getTournamentId();

        try {
            tournamentService.beginUpdate(tournamentId);
            return withSuccess();
        } catch (Exception e) {
            return withFailure();
        }
    }

    public Message beginRemove(CommandMessage<BeginRemoveTournamentCommand> cm) {
        logger.info("Received BeginRemoveTournamentCommand");

        Integer tournamentId = cm.getCommand().getTournamentId();

        try {
            tournamentService.beginRemove(tournamentId);
            return withSuccess();
        } catch (Exception e) {
            return withFailure();
        }
    }

    public Message confirmRemove(CommandMessage<ConfirmRemoveTournamentCommand> cm) {
        logger.info("Received ConfirmRemoveTournamentCommand");

        Integer tournamentId = cm.getCommand().getTournamentId();

        try {
            tournamentService.confirmRemove(tournamentId);
            return withSuccess();
        } catch (Exception e) {
            return withFailure();
        }
    }

    public Message undoRemove(CommandMessage<UndoRemoveTournamentCommand> cm) {
        logger.info("Received UndoRemoveTournamentCommand");

        Integer tournamentId = cm.getCommand().getTournamentId();

        try {
            tournamentService.undoRemove(tournamentId);
            return withSuccess();
        } catch (Exception e) {
            return withFailure();
        }
    }
}
