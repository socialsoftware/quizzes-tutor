package pt.ulisboa.tecnico.socialsoftware.common.commands.answer;

import io.eventuate.tram.commands.common.Command;

public class SolveQuizCommand implements Command {

    private Integer userId;
    private Integer quizId;

    public SolveQuizCommand() {
    }

    public SolveQuizCommand(Integer userId, Integer quizId) {
        this.userId = userId;
        this.quizId = quizId;
    }

    public Integer getQuizId() {
        return quizId;
    }

    public void setQuizId(Integer quizId) {
        this.quizId = quizId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "SolveQuizCommand{" +
                "userId=" + userId +
                ", quizId=" + quizId +
                '}';
    }
}
