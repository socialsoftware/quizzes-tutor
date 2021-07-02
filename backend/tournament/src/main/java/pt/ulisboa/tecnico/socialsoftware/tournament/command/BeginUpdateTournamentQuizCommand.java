package pt.ulisboa.tecnico.socialsoftware.tournament.command;

import io.eventuate.tram.commands.common.Command;

public class BeginUpdateTournamentQuizCommand implements Command {
    private Integer tournamentId;

    public BeginUpdateTournamentQuizCommand() {
    }

    public BeginUpdateTournamentQuizCommand(Integer tournamentId) {
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
        return "BeginUpdateTournamentQuizCommand{" +
                "tournamentId=" + tournamentId +
                '}';
    }
}
