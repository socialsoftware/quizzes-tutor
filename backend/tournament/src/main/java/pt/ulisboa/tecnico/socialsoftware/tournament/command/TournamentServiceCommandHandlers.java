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
                .onMessage(UndoUpdateTournamentCommand.class, this::undoUpdate)
                .onMessage(ConfirmUpdateTournamentQuizCommand.class, this::confirmUpdate)
                .onMessage(ConfirmCreateTournamentCommand.class, this::confirmCreate)
                .onMessage(RejectCreateTournamentCommand.class, this::rejectCreate)
                .onMessage(StoreTournamentTopicsCommand.class, this::storeTopics)
                .onMessage(StoreTournamentCourseExecutionCommand.class, this::storeCourseExecution)
                .onMessage(UpdateTopicsTournamentCommand.class, this::updateTopics)
                .onMessage(UndoUpdateTopicsTournamentCommand.class, this::undoUpdateTopics)
                .build();
    }

    private Message undoUpdateTopics(CommandMessage<UndoUpdateTopicsTournamentCommand> cm) {
        logger.info("Received UndoUpdateTopicsTournamentCommand");

        Integer tournamentId = cm.getCommand().getTournamentId();
        Set<TournamentTopic> topics = cm.getCommand().getOldTopics();

        try {
            tournamentService.undoUpdateTopics(tournamentId, topics);
            return withSuccess();
        } catch (Exception e) {
            return withFailure();
        }
    }

    private Message updateTopics(CommandMessage<UpdateTopicsTournamentCommand> cm) {
        logger.info("Received UpdateTopicsTournamentCommand");

        Integer tournamentId = cm.getCommand().getTournamentId();
        Set<TournamentTopic> topics = cm.getCommand().getTournamentTopics();

        try {
            tournamentService.updateTopics(tournamentId, topics);
            return withSuccess();
        } catch (Exception e) {
            return withFailure();
        }
    }

    private Message storeCourseExecution(CommandMessage<StoreTournamentCourseExecutionCommand> cm) {
        logger.info("Received StoreTournamentCourseExecutionCommand");

        Integer tournamentId = cm.getCommand().getTournamentId();
        TournamentCourseExecution tournamentCourseExecution = cm.getCommand().getTournamentCourseExecution();

        try {
            tournamentService.storeCourseExecution(tournamentId, tournamentCourseExecution);
            return withSuccess();
        } catch (Exception e) {
            return withFailure();
        }
    }

    private Message storeTopics(CommandMessage<StoreTournamentTopicsCommand> cm) {
        logger.info("Received StoreTournamentTopicsCommand");

        Integer tournamentId = cm.getCommand().getTournamentId();
        Set<TournamentTopic> tournamentTopics = cm.getCommand().getTournamentTopics();

        try {
            tournamentService.storeTopics(tournamentId, tournamentTopics);
            return withSuccess();
        } catch (Exception e) {
            return withFailure();
        }
    }

    private Message rejectCreate(CommandMessage<RejectCreateTournamentCommand> cm) {
        logger.info("Received RejectCreateTournamentCommand");

        Integer tournamentId = cm.getCommand().getTournamentId();

        try {
            tournamentService.rejectCreate(tournamentId);
            return withSuccess();
        } catch (Exception e) {
            return withFailure();
        }
    }

    private Message confirmCreate(CommandMessage<ConfirmCreateTournamentCommand> cm) {
        logger.info("Received ConfirmCreateTournamentCommand");

        Integer tournamentId = cm.getCommand().getTournamentId();
        Integer quizId = cm.getCommand().getQuizId();

        try {
            tournamentService.confirmCreate(tournamentId, quizId);
            return withSuccess();
        } catch (Exception e) {
            return withFailure();
        }
    }

    private Message confirmUpdate(CommandMessage<ConfirmUpdateTournamentQuizCommand> cm) {
        logger.info("Received ConfirmUpdateTournamentQuizCommand");

        Integer tournamentId = cm.getCommand().getTournamentId();
        TournamentDto tournamentDto = cm.getCommand().getTournamentDto();

        try {
            tournamentService.confirmUpdate(tournamentId, tournamentDto);
            return withSuccess();
        } catch (Exception e) {
            return withFailure();
        }
    }

    private Message undoUpdate(CommandMessage<UndoUpdateTournamentCommand> cm) {
        logger.info("Received UndoUpdateTournamentQuizCommand");

        Integer tournamentId = cm.getCommand().getTournamentId();

        try {
            tournamentService.undoUpdate(tournamentId);
            return withSuccess();
        } catch (Exception e) {
            return withFailure();
        }
    }

    private Message beginUpdate(CommandMessage<BeginUpdateTournamentQuizCommand> cm) {
        logger.info("Received BeginUpdateTournamentQuizCommand");

        Integer tournamentId = cm.getCommand().getTournamentId();

        try {
            tournamentService.beginUpdate(tournamentId);
            return withSuccess();
        } catch (Exception e) {
            return withFailure();
        }
    }

    private Message beginRemove(CommandMessage<BeginRemoveTournamentCommand> cm) {
        logger.info("Received BeginRemoveTournamentCommand");

        Integer tournamentId = cm.getCommand().getTournamentId();

        try {
            tournamentService.beginRemove(tournamentId);
            return withSuccess();
        } catch (Exception e) {
            return withFailure();
        }
    }

    private Message confirmRemove(CommandMessage<ConfirmRemoveTournamentCommand> cm) {
        logger.info("Received ConfirmRemoveTournamentCommand");

        Integer tournamentId = cm.getCommand().getTournamentId();

        try {
            tournamentService.confirmRemove(tournamentId);
            return withSuccess();
        } catch (Exception e) {
            return withFailure();
        }
    }

    private Message undoRemove(CommandMessage<UndoRemoveTournamentCommand> cm) {
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
