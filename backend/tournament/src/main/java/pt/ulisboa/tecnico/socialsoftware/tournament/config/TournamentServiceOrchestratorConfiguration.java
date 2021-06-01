package pt.ulisboa.tecnico.socialsoftware.tournament.config;

import io.eventuate.tram.sagas.spring.orchestration.SagaOrchestratorConfiguration;
import io.eventuate.tram.spring.consumer.kafka.EventuateTramKafkaMessageConsumerConfiguration;
import io.eventuate.tram.spring.messaging.producer.jdbc.TramMessageProducerJdbcConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import pt.ulisboa.tecnico.socialsoftware.tournament.sagas.participants.AnswerServiceProxy;
import pt.ulisboa.tecnico.socialsoftware.tournament.sagas.participants.QuizServiceProxy;
import pt.ulisboa.tecnico.socialsoftware.tournament.sagas.participants.TournamentServiceProxy;
import pt.ulisboa.tecnico.socialsoftware.tournament.sagas.removeTournament.RemoveTournamentSaga;
import pt.ulisboa.tecnico.socialsoftware.tournament.sagas.solveTournamentQuiz.CreateAndSolveTournamentQuizSaga;
import pt.ulisboa.tecnico.socialsoftware.tournament.sagas.updateTournament.UpdateTournamentSaga;

/**
 * The configuration class to instantiate and wire the domain service class.
 */
@Configuration
@Import({SagaOrchestratorConfiguration.class, TramMessageProducerJdbcConfiguration.class,
        EventuateTramKafkaMessageConsumerConfiguration.class})
public class TournamentServiceOrchestratorConfiguration {

    @Bean
    public TournamentServiceProxy tournamentServiceProxy() {
        return new TournamentServiceProxy();
    }

    @Bean
    public QuizServiceProxy quizServiceProxy() {
        return new QuizServiceProxy();
    }

    @Bean
    public AnswerServiceProxy answerServiceProxy() {
        return new AnswerServiceProxy();
    }

    @Bean
    public RemoveTournamentSaga confirmRegistrationSaga(TournamentServiceProxy tournamentService,
                                                        QuizServiceProxy quizService) {
        return new RemoveTournamentSaga(tournamentService, quizService);
    }

    @Bean
    public CreateAndSolveTournamentQuizSaga solveTournamentQuizSaga(TournamentServiceProxy tournamentService,
                                                                    AnswerServiceProxy answerService) {
        return new CreateAndSolveTournamentQuizSaga(tournamentService, answerService);
    }

    @Bean
    public UpdateTournamentSaga updateTournamentSaga(TournamentServiceProxy tournamentService,
                                                        QuizServiceProxy quizService) {
        return new UpdateTournamentSaga(tournamentService, quizService);
    }
}
