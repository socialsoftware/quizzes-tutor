package pt.ulisboa.tecnico.socialsoftware.tournament.command;

import io.eventuate.tram.commands.common.Command;

public class ConfirmSolveTournamentQuizCommand implements Command {

    private Integer tournamentId;
    private Integer quizId;

    public ConfirmSolveTournamentQuizCommand() {
    }

    public ConfirmSolveTournamentQuizCommand(Integer tournamentId, Integer quizId) {
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
        return "ConfirmSolveTournamentQuizCommand{" +
                "tournamentId=" + tournamentId +
                ", quizId=" + quizId +
                '}';
    }
}
