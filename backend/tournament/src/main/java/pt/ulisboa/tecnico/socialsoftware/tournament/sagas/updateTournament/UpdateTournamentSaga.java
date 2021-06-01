package pt.ulisboa.tecnico.socialsoftware.tournament.sagas.updateTournament;

import io.eventuate.tram.sagas.orchestration.SagaDefinition;
import io.eventuate.tram.sagas.simpledsl.SimpleSaga;
import org.springframework.util.Assert;
import pt.ulisboa.tecnico.socialsoftware.tournament.sagas.participants.QuizServiceProxy;
import pt.ulisboa.tecnico.socialsoftware.tournament.sagas.participants.TournamentServiceProxy;

public class UpdateTournamentSaga implements SimpleSaga<UpdateTournamentSagaData> {
    private SagaDefinition<UpdateTournamentSagaData> sagaDefinition;

    public UpdateTournamentSaga(TournamentServiceProxy tournamentService, QuizServiceProxy quizService) {
        this.sagaDefinition =
            step()
                .invokeParticipant(tournamentService.beginUpdate, UpdateTournamentSagaData::beginUpdateTournamentQuiz)
                .withCompensation(tournamentService.undoUpdate, UpdateTournamentSagaData::undoUpdateTournamentQuiz)
            .step()
                .invokeParticipant(quizService.updateQuiz, UpdateTournamentSagaData::updateQuiz)
                .withCompensation(tournamentService.undoUpdate, UpdateTournamentSagaData::undoUpdateTournamentQuiz)
            .step()
                .invokeParticipant(tournamentService.confirmUpdate, UpdateTournamentSagaData::confirmUpdateTournamentQuiz)
            .build();
    }

    @Override
    public SagaDefinition<UpdateTournamentSagaData> getSagaDefinition() {
        Assert.notNull(sagaDefinition, "sagaDefinition is null.");
        return sagaDefinition;
    }
}
