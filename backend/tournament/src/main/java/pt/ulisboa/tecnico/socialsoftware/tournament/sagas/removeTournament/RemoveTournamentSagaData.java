package pt.ulisboa.tecnico.socialsoftware.tournament.sagas.removeTournament;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pt.ulisboa.tecnico.socialsoftware.common.commands.answer.DeleteQuizCommand;
import pt.ulisboa.tecnico.socialsoftware.tournament.command.BeginRemoveTournamentCommand;
import pt.ulisboa.tecnico.socialsoftware.tournament.command.ConfirmRemoveTournamentCommand;
import pt.ulisboa.tecnico.socialsoftware.tournament.command.UndoRemoveTournamentCommand;

public class RemoveTournamentSagaData {
    private final Logger logger = LoggerFactory.getLogger(RemoveTournamentSagaData.class);

    private Integer tournamentId;
    private Integer quizId;

    public RemoveTournamentSagaData() {
    }

    public RemoveTournamentSagaData(Integer tournamentId, Integer quizId) {
        this.tournamentId = tournamentId;
        this.quizId = quizId;
    }

    public Integer getTournamentId() {
        return tournamentId;
    }

    public void setTournamentId(Integer tournamentId) {
        this.tournamentId = tournamentId;
    }

    public Integer getQuizId() {
        return quizId;
    }

    public void setQuizId(Integer quizId) {
        this.quizId = quizId;
    }

    BeginRemoveTournamentCommand beginRemoveTournament() {
        logger.info("Sent BeginRemoveTournamentCommand");
        return new BeginRemoveTournamentCommand(getTournamentId());
    }

    UndoRemoveTournamentCommand undoRemoveTournament() {
        logger.info("Sent UndoRemoveTournamentCommand");
        return new UndoRemoveTournamentCommand(getTournamentId());
    }

    DeleteQuizCommand deleteQuiz() {
        logger.info("Sent DeleteQuizCommand");
        return new DeleteQuizCommand(getQuizId());
    }

    ConfirmRemoveTournamentCommand confirmRemoveTournament() {
        logger.info("Sent ConfirmRemoveTournamentCommand");
        return new ConfirmRemoveTournamentCommand(getTournamentId());
    }
}
