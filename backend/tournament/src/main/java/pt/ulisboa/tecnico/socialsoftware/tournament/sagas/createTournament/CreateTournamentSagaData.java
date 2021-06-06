package pt.ulisboa.tecnico.socialsoftware.tournament.sagas.createTournament;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pt.ulisboa.tecnico.socialsoftware.common.commands.answer.GenerateQuizCommand;
import pt.ulisboa.tecnico.socialsoftware.common.dtos.tournament.ExternalStatementCreationDto;
import pt.ulisboa.tecnico.socialsoftware.tournament.command.ConfirmCreateTournamentCommand;
import pt.ulisboa.tecnico.socialsoftware.tournament.command.RejectCreateTournamentCommand;

public class CreateTournamentSagaData {
    private final Logger logger = LoggerFactory.getLogger(CreateTournamentSagaData.class);

    private Integer tournamentId;
    private Integer quizId;
    private ExternalStatementCreationDto externalStatementCreationDto;
    private Integer creatorId;
    private Integer courseExecutionId;

    public CreateTournamentSagaData(Integer tournamentId, Integer creatorId, Integer courseExecutionId,
                                    ExternalStatementCreationDto externalStatementCreationDto) {
        this.tournamentId = tournamentId;
        this.creatorId = creatorId;
        this.courseExecutionId = courseExecutionId;
        this.externalStatementCreationDto = externalStatementCreationDto;
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

    public ExternalStatementCreationDto getExternalStatementCreationDto() {
        return externalStatementCreationDto;
    }

    public void setExternalStatementCreationDto(ExternalStatementCreationDto externalStatementCreationDto) {
        this.externalStatementCreationDto = externalStatementCreationDto;
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

    GenerateQuizCommand generateTournamentQuiz() {
        logger.info("Sent GenerateTournamentQuizCommand");
        return new GenerateQuizCommand(getCreatorId(), getCourseExecutionId(), getExternalStatementCreationDto());
    }

    void handleGenerateTournamentQuiz(Integer reply) {
        logger.info("Received handleGenerateTournamentQuiz quizId: " + reply);
        setQuizId(reply);
    }


    RejectCreateTournamentCommand undoCreateTournament() {
        logger.info("Sent RejectCreateTournamentCommand");
        return new RejectCreateTournamentCommand(getTournamentId());
    }

    ConfirmCreateTournamentCommand confirmCreateTournament() {
        logger.info("Sent ConfirmCreateTournamentCommand");
        return new ConfirmCreateTournamentCommand(getTournamentId(), getQuizId());
    }

    @Override
    public String toString() {
        return "CreateTournamentSagaData{" +
                "tournamentId=" + tournamentId +
                ", quizId=" + quizId +
                '}';
    }
}
