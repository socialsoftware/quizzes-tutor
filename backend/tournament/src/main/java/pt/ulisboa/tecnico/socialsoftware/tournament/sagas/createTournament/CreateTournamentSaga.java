package pt.ulisboa.tecnico.socialsoftware.tournament.sagas.createTournament;

import io.eventuate.tram.sagas.orchestration.SagaDefinition;
import io.eventuate.tram.sagas.simpledsl.SimpleSaga;
import org.springframework.util.Assert;
import pt.ulisboa.tecnico.socialsoftware.tournament.sagas.participants.AnswerServiceProxy;
import pt.ulisboa.tecnico.socialsoftware.tournament.sagas.participants.TournamentServiceProxy;

public class CreateTournamentSaga implements SimpleSaga<CreateTournamentSagaData> {
    private SagaDefinition<CreateTournamentSagaData> sagaDefinition;

    public CreateTournamentSaga(TournamentServiceProxy tournamentService, AnswerServiceProxy answerService) {
        this.sagaDefinition =
            step()
                .withCompensation(tournamentService.rejectCreate, CreateTournamentSagaData::undoCreateTournament)
            .step()
                .invokeParticipant(answerService.generateTournamentQuiz,CreateTournamentSagaData::generateTournamentQuiz)
                .onReply(Integer.class, CreateTournamentSagaData::handleGenerateTournamentQuiz)
            .step()
                .invokeParticipant(tournamentService.confirmCreate, CreateTournamentSagaData::confirmCreateTournament)
            .build();
    }

    @Override
    public SagaDefinition<CreateTournamentSagaData> getSagaDefinition() {
        Assert.notNull(sagaDefinition, "sagaDefinition is null.");
        return sagaDefinition;
    }
}
