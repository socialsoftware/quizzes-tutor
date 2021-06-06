package pt.ulisboa.tecnico.socialsoftware.tournament.command;

import io.eventuate.tram.commands.common.Command;

public class ConfirmCreateTournamentCommand implements Command {

    private Integer tournamentId;
    private Integer quizId;

    public ConfirmCreateTournamentCommand() {
    }

    public ConfirmCreateTournamentCommand(Integer tournamentId, Integer quizId) {
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

    @Override
    public String toString() {
        return "ConfirmCreateTournamentCommand{" +
                "tournamentId=" + tournamentId +
                ", quizId=" + quizId +
                '}';
    }
}
