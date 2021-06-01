package pt.ulisboa.tecnico.socialsoftware.common.commands.quiz;

import io.eventuate.tram.commands.common.Command;
import pt.ulisboa.tecnico.socialsoftware.common.dtos.quiz.QuizDto;

public class UpdateQuizCommand implements Command {
    private QuizDto quizDto;

    public UpdateQuizCommand() {
    }

    public UpdateQuizCommand(QuizDto quizDto) {
        this.quizDto = quizDto;
    }

    public QuizDto getQuizDto() {
        return quizDto;
    }

    public void setQuizDto(QuizDto quizDto) {
        this.quizDto = quizDto;
    }

    @Override
    public String toString() {
        return "UpdateQuizCommand{" +
                "quizDto=" + quizDto +
                '}';
    }
}
