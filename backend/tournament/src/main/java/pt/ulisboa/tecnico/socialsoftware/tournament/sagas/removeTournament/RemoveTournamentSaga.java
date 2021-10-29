package pt.ulisboa.tecnico.socialsoftware.tournament.sagas.removeTournament;

import io.eventuate.tram.sagas.orchestration.SagaDefinition;
import io.eventuate.tram.sagas.simpledsl.SimpleSaga;
import org.springframework.util.Assert;
import pt.ulisboa.tecnico.socialsoftware.tournament.sagas.participants.QuizServiceProxy;
import pt.ulisboa.tecnico.socialsoftware.tournament.sagas.participants.TournamentServiceProxy;

public class RemoveTournamentSaga implements SimpleSaga<RemoveTournamentSagaData> {
    private SagaDefinition<RemoveTournamentSagaData> sagaDefinition;

    public RemoveTournamentSaga(TournamentServiceProxy tournamentService, QuizServiceProxy quizService) {
        this.sagaDefinition =
            step()
                .invokeParticipant(tournamentService.beginRemove, RemoveTournamentSagaData::beginRemoveTournament)
                .withCompensation(tournamentService.undoRemove, RemoveTournamentSagaData::undoRemoveTournament)
            .step()
                .invokeParticipant(quizService.deleteQuiz, RemoveTournamentSagaData::deleteQuiz)
            .step()
                .invokeParticipant(tournamentService.confirmRemove, RemoveTournamentSagaData::confirmRemoveTournament)
            .build();
    }

    @Override
    public SagaDefinition<RemoveTournamentSagaData> getSagaDefinition() {
        Assert.notNull(sagaDefinition, "sagaDefinition is null.");
        return sagaDefinition;
    }
}
