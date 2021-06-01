package pt.ulisboa.tecnico.socialsoftware.tournament.sagas.solveTournamentQuiz;

import io.eventuate.tram.sagas.orchestration.SagaDefinition;
import io.eventuate.tram.sagas.simpledsl.SimpleSaga;
import org.springframework.util.Assert;
import pt.ulisboa.tecnico.socialsoftware.tournament.sagas.participants.AnswerServiceProxy;
import pt.ulisboa.tecnico.socialsoftware.tournament.sagas.participants.TournamentServiceProxy;

public class CreateAndSolveTournamentQuizSaga implements SimpleSaga<CreateAndSolveTournamentQuizSagaData> {
    private SagaDefinition<CreateAndSolveTournamentQuizSagaData> sagaDefinition;

    public CreateAndSolveTournamentQuizSaga(TournamentServiceProxy tournamentService, AnswerServiceProxy answerService) {
        this.sagaDefinition =
            step()
                .invokeParticipant(tournamentService.beginSolve, CreateAndSolveTournamentQuizSagaData::beginSolveTournamentQuiz)
                .withCompensation(tournamentService.undoSolve, CreateAndSolveTournamentQuizSagaData::undoSolveTournamentQuiz)
            .step()
                .invokeParticipant(answerService.generateTournamentQuiz, CreateAndSolveTournamentQuizSagaData::generateTournamentQuiz)
                .onReply(Integer.class, CreateAndSolveTournamentQuizSagaData::handleGenerateTournamentQuiz)
                .withCompensation(answerService.deleteTournamentQuiz, CreateAndSolveTournamentQuizSagaData::deleteTournamentQuiz)
            .step()
                .invokeParticipant(answerService.solveQuiz, CreateAndSolveTournamentQuizSagaData::solveQuiz)
            .step()
                .invokeParticipant(tournamentService.confirmSolve, CreateAndSolveTournamentQuizSagaData::confirmSolveTournamentQuiz)
            .build();
    }

    @Override
    public SagaDefinition<CreateAndSolveTournamentQuizSagaData> getSagaDefinition() {
        Assert.notNull(sagaDefinition, "sagaDefinition is null.");
        return sagaDefinition;
    }
}
