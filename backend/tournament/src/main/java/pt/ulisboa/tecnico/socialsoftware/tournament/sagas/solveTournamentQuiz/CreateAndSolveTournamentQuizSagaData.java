package pt.ulisboa.tecnico.socialsoftware.tournament.sagas.solveTournamentQuiz;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pt.ulisboa.tecnico.socialsoftware.common.commands.answer.GenerateQuizCommand;
import pt.ulisboa.tecnico.socialsoftware.common.commands.answer.SolveQuizCommand;
import pt.ulisboa.tecnico.socialsoftware.common.commands.quiz.DeleteQuizCommand;
import pt.ulisboa.tecnico.socialsoftware.tournament.command.BeginSolveTournamentQuizCommand;
import pt.ulisboa.tecnico.socialsoftware.tournament.command.ConfirmSolveTournamentQuizCommand;
import pt.ulisboa.tecnico.socialsoftware.tournament.command.UndoSolveTournamentQuizCommand;
import pt.ulisboa.tecnico.socialsoftware.common.dtos.tournament.ExternalStatementCreationDto;

public class CreateAndSolveTournamentQuizSagaData {
    private final Logger logger = LoggerFactory.getLogger(CreateAndSolveTournamentQuizSagaData.class);

    private Integer tournamentId;
    private Integer quizId;
    private Integer userId;
    private Integer courseExecutionId;
    private ExternalStatementCreationDto externalStatementCreationDto;

    public CreateAndSolveTournamentQuizSagaData() {
    }

    public CreateAndSolveTournamentQuizSagaData(Integer tournamentId, Integer quizId, Integer userId, Integer courseExecutionId,
                                                ExternalStatementCreationDto externalStatementCreationDto) {
        this.tournamentId = tournamentId;
        this.quizId = quizId;
        this.userId = userId;
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

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getCourseExecutionId() {
        return courseExecutionId;
    }

    public void setCourseExecutionId(Integer courseExecutionId) {
        this.courseExecutionId = courseExecutionId;
    }

    public ExternalStatementCreationDto getExternalStatementCreationDto() {
        return externalStatementCreationDto;
    }

    public void setExternalStatementCreationDto(ExternalStatementCreationDto externalStatementCreationDto) {
        this.externalStatementCreationDto = externalStatementCreationDto;
    }

    BeginSolveTournamentQuizCommand beginSolveTournamentQuiz() {
        logger.info("Sent BeginSolveTournamentQuizCommand");
        return new BeginSolveTournamentQuizCommand(getTournamentId());
    }

    UndoSolveTournamentQuizCommand undoSolveTournamentQuiz() {
        logger.info("Sent UndoSolveTournamentQuizCommand");
        return new UndoSolveTournamentQuizCommand(getTournamentId());
    }

    SolveQuizCommand solveQuiz() {
        logger.info("Sent SolveQuizCommand");
        return new SolveQuizCommand(getUserId(), getQuizId());
    }

    ConfirmSolveTournamentQuizCommand confirmSolveTournamentQuiz() {
        logger.info("Sent ConfirmSolveTournamentQuizCommand");
        return new ConfirmSolveTournamentQuizCommand(getTournamentId());
    }

    GenerateQuizCommand generateTournamentQuiz() {
        logger.info("Sent GenerateTournamentQuizCommand");
        return new GenerateQuizCommand(getUserId(), getCourseExecutionId(), getExternalStatementCreationDto());
    }

    DeleteQuizCommand deleteTournamentQuiz() {
        logger.info("Sent DeleteTournamentQuizCommand");
        return new DeleteQuizCommand(getQuizId());
    }

    void handleGenerateTournamentQuiz(Integer reply) {
        logger.info("Received handleGenerateTournamentQuiz quizId: " + reply);
        setQuizId(reply);
    }
}
