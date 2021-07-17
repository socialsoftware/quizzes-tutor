package pt.ulisboa.tecnico.socialsoftware.common.commands.answer;

import io.eventuate.tram.commands.common.Command;
import pt.ulisboa.tecnico.socialsoftware.common.dtos.tournament.ExternalStatementCreationDto;

public class GenerateQuizCommand implements Command {

    private Integer creatorId;
    private Integer courseExecutionId;
    private ExternalStatementCreationDto quizForm;

    public GenerateQuizCommand() {
    }

    public GenerateQuizCommand(Integer creatorId, Integer courseExecutionId, ExternalStatementCreationDto quizForm) {
        this.creatorId = creatorId;
        this.courseExecutionId = courseExecutionId;
        this.quizForm = quizForm;
    }

    public Integer getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(Integer creatorId) {
        this.creatorId = creatorId;
    }

    public Integer getCourseExecutionId() {
        return courseExecutionId;
    }

    public void setCourseExecutionId(Integer courseExecutionId) {
        this.courseExecutionId = courseExecutionId;
    }

    public ExternalStatementCreationDto getQuizForm() {
        return quizForm;
    }

    public void setQuizForm(ExternalStatementCreationDto quizForm) {
        this.quizForm = quizForm;
    }

    @Override
    public String toString() {
        return "GenerateTournamentQuizCommand{" +
                "creatorId=" + creatorId +
                ", courseExecutionId=" + courseExecutionId +
                ", quizForm=" + quizForm +
                '}';
    }
}
