package pt.ulisboa.tecnico.socialsoftware.tournament.command;

import io.eventuate.tram.commands.common.Command;
import pt.ulisboa.tecnico.socialsoftware.common.dtos.tournament.TournamentDto;

public class ConfirmUpdateTournamentQuizCommand implements Command {
    private Integer tournamentId;
    private TournamentDto tournamentDto;

    public ConfirmUpdateTournamentQuizCommand() {
    }

    public ConfirmUpdateTournamentQuizCommand(Integer tournamentId, TournamentDto tournamentDto) {
        this.tournamentId = tournamentId;
        this.tournamentDto = tournamentDto;
    }

    public Integer getTournamentId() {
        return tournamentId;
    }

    public void setTournamentId(Integer tournamentId) {
        this.tournamentId = tournamentId;
    }

    public TournamentDto getTournamentDto() {
        return tournamentDto;
    }

    public void setTournamentDto(TournamentDto tournamentDto) {
        this.tournamentDto = tournamentDto;
    }

    @Override
    public String toString() {
        return "ConfirmUpdateTournamentQuizCommand{" +
                "tournamentId=" + tournamentId +
                ", tournamentDto=" + tournamentDto +
                '}';
    }
}
