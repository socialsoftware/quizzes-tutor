package pt.ulisboa.tecnico.socialsoftware.common.commands.quiz;

import io.eventuate.tram.commands.common.Command;
import pt.ulisboa.tecnico.socialsoftware.common.dtos.tournament.TournamentDto;

public class UpdateQuizCommand implements Command {
    private Integer userId;
    private Integer executionId;
    private Integer quizId;
    private TournamentDto tournamentDto;

    public UpdateQuizCommand() {
    }

    public UpdateQuizCommand(Integer userId, Integer executionId, Integer quizId,
                             TournamentDto tournamentDto) {
        this.userId = userId;
        this.executionId = executionId;
        this.quizId = quizId;
        this.tournamentDto = tournamentDto;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getExecutionId() {
        return executionId;
    }

    public void setExecutionId(Integer executionId) {
        this.executionId = executionId;
    }

    public Integer getQuizId() {
        return quizId;
    }

    public void setQuizId(Integer quizId) {
        this.quizId = quizId;
    }

    public TournamentDto getTournamentDto() {
        return tournamentDto;
    }

    public void setTournamentDto(TournamentDto tournamentDto) {
        this.tournamentDto = tournamentDto;
    }

    @Override
    public String toString() {
        return "UpdateQuizCommand{" +
                "userId=" + userId +
                ", executionId=" + executionId +
                ", quizId=" + quizId +
                ", tournamentDto=" + tournamentDto +
                '}';
    }
}
