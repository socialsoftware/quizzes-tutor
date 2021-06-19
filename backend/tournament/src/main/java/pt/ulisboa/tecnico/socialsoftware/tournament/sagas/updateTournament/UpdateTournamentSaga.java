package pt.ulisboa.tecnico.socialsoftware.tournament.sagas.updateTournament;

import io.eventuate.tram.sagas.orchestration.SagaDefinition;
import io.eventuate.tram.sagas.simpledsl.SimpleSaga;
import org.springframework.util.Assert;
import pt.ulisboa.tecnico.socialsoftware.common.dtos.quiz.QuizDto;
import pt.ulisboa.tecnico.socialsoftware.common.dtos.tournament.FindTopicsDto;
import pt.ulisboa.tecnico.socialsoftware.tournament.sagas.createTournament.CreateTournamentSagaData;
import pt.ulisboa.tecnico.socialsoftware.tournament.sagas.participants.QuestionServiceProxy;
import pt.ulisboa.tecnico.socialsoftware.tournament.sagas.participants.QuizServiceProxy;
import pt.ulisboa.tecnico.socialsoftware.tournament.sagas.participants.TournamentServiceProxy;

public class UpdateTournamentSaga implements SimpleSaga<UpdateTournamentSagaData> {
    private SagaDefinition<UpdateTournamentSagaData> sagaDefinition;

    public UpdateTournamentSaga(TournamentServiceProxy tournamentService, QuizServiceProxy quizService,
                                QuestionServiceProxy questionService) {
        this.sagaDefinition =
            step()
                .invokeParticipant(tournamentService.beginUpdate, UpdateTournamentSagaData::beginUpdateTournamentQuiz)
                .withCompensation(tournamentService.undoUpdate, UpdateTournamentSagaData::undoUpdateTournamentQuiz)
            .step()
                .invokeParticipant(questionService.getTopics, UpdateTournamentSagaData::getTopics)
                .onReply(FindTopicsDto.class, UpdateTournamentSagaData::saveTopics)
            .step()
                .invokeParticipant(quizService.getQuiz, UpdateTournamentSagaData::getQuiz)
                .onReply(QuizDto.class, UpdateTournamentSagaData::saveQuiz)
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
