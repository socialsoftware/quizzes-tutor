package pt.ulisboa.tecnico.socialsoftware.tournament.command;

import io.eventuate.tram.commands.common.Command;

public class ConfirmSolveTournamentQuizCommand implements Command {

    private Integer tournamentId;

    public ConfirmSolveTournamentQuizCommand() {
    }

    public ConfirmSolveTournamentQuizCommand(Integer tournamentId) {
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
        return "ConfirmSolveTournamentQuizCommand{" +
                "tournamentId=" + tournamentId +
                '}';
    }
}
