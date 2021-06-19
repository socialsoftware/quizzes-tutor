package pt.ulisboa.tecnico.socialsoftware.common.commands.quiz;

import io.eventuate.tram.commands.common.Command;

public class GetQuizCommand implements Command {

    private Integer quizId;

    public GetQuizCommand() {
    }

    public GetQuizCommand(Integer quizId) {
        this.quizId = quizId;
    }

    public Integer getQuizId() {
        return quizId;
    }

    public void setQuizId(Integer quizId) {
        this.quizId = quizId;
    }

    @Override
    public String toString() {
        return "GetQuizCommand{" +
                "quizId=" + quizId +
                '}';
    }
}
