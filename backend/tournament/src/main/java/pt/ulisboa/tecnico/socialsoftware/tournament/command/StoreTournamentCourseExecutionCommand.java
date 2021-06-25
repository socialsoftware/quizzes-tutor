package pt.ulisboa.tecnico.socialsoftware.tournament.command;

import io.eventuate.tram.commands.common.Command;
import pt.ulisboa.tecnico.socialsoftware.tournament.domain.TournamentCourseExecution;

public class StoreTournamentCourseExecutionCommand implements Command {

    private Integer tournamentId;
    private TournamentCourseExecution tournamentCourseExecution;

    public StoreTournamentCourseExecutionCommand() {
    }

    public StoreTournamentCourseExecutionCommand(Integer tournamentId, TournamentCourseExecution tournamentCourseExecution) {
        this.tournamentId = tournamentId;
        this.tournamentCourseExecution = tournamentCourseExecution;
    }

    public Integer getTournamentId() {
        return tournamentId;
    }

    public void setTournamentId(Integer tournamentId) {
        this.tournamentId = tournamentId;
    }

    public TournamentCourseExecution getTournamentCourseExecution() {
        return tournamentCourseExecution;
    }

    public void setTournamentCourseExecution(TournamentCourseExecution tournamentCourseExecution) {
        this.tournamentCourseExecution = tournamentCourseExecution;
    }

    @Override
    public String toString() {
        return "StoreTournamentCourseExecutionCommand{" +
                "tournamentId=" + tournamentId +
                ", tournamentCourseExecution=" + tournamentCourseExecution +
                '}';
    }
}
