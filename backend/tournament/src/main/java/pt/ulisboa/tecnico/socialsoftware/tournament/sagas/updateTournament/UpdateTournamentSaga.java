package pt.ulisboa.tecnico.socialsoftware.tournament.sagas.updateTournament;

import io.eventuate.tram.sagas.orchestration.SagaDefinition;
import io.eventuate.tram.sagas.simpledsl.SimpleSaga;
import org.springframework.util.Assert;
import pt.ulisboa.tecnico.socialsoftware.common.dtos.tournament.FindTopicsDto;
import pt.ulisboa.tecnico.socialsoftware.tournament.sagas.participants.QuestionServiceProxy;
import pt.ulisboa.tecnico.socialsoftware.tournament.sagas.participants.QuizServiceProxy;
import pt.ulisboa.tecnico.socialsoftware.tournament.sagas.participants.TournamentServiceProxy;

public class UpdateTournamentSaga implements SimpleSaga<UpdateTournamentSagaData> {
    private SagaDefinition<UpdateTournamentSagaData> sagaDefinition;

    public UpdateTournamentSaga(TournamentServiceProxy tournamentService, QuizServiceProxy quizService,
                                QuestionServiceProxy questionService) {
        this.sagaDefinition =
            step()
                .withCompensation(tournamentService.undoUpdate, UpdateTournamentSagaData::undoUpdateTournament)
            .step()
                .invokeParticipant(questionService.getTopics, UpdateTournamentSagaData::getNewTopics)
                .onReply(FindTopicsDto.class, UpdateTournamentSagaData::saveTopics)
            .step()
                .invokeParticipant(tournamentService.updateTopics, UpdateTournamentSagaData::updateTopics)
                .withCompensation(tournamentService.undoUpdateTopics, UpdateTournamentSagaData::undoUpdateTopics)
            .step()
                .invokeParticipant(quizService.updateQuiz, UpdateTournamentSagaData::updateQuiz)
                .withCompensation(tournamentService.undoUpdateQuiz, UpdateTournamentSagaData::undoUpdateTournamentQuiz)
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
