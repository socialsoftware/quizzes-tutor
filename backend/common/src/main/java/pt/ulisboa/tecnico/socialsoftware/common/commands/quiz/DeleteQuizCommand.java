package pt.ulisboa.tecnico.socialsoftware.common.commands.quiz;

import io.eventuate.tram.commands.common.Command;

public class DeleteQuizCommand implements Command {

    private Integer quizId;

    public DeleteQuizCommand() {
    }

    public DeleteQuizCommand(Integer quizId) {
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
        return "DeleteQuizCommand{" +
                "quizId=" + quizId +
                '}';
    }
}
