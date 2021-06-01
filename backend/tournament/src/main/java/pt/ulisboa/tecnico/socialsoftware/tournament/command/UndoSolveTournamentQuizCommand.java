package pt.ulisboa.tecnico.socialsoftware.tournament.command;

import io.eventuate.tram.commands.common.Command;

public class UndoSolveTournamentQuizCommand implements Command {

    private Integer tournamentId;

    public UndoSolveTournamentQuizCommand() {
    }

    public UndoSolveTournamentQuizCommand(Integer tournamentId) {
        this.tournamentId = tournamentId;
    }

    public Integer getTournamentId() {
        return tournamentId;
    }

    public void setTournamentId(Integer tournamentId) {
        this.tournamentId = tournamentId;
    }

    @Override
    public String toString() {
        return "UndoSolveTournamentQuizCommand{" +
                "tournamentId=" + tournamentId +
                '}';
    }
}
