package pt.ulisboa.tecnico.socialsoftware.tournament.config;

import io.eventuate.tram.sagas.participant.SagaCommandDispatcher;
import io.eventuate.tram.sagas.participant.SagaCommandDispatcherFactory;
import io.eventuate.tram.sagas.spring.participant.SagaParticipantConfiguration;
import io.eventuate.tram.spring.consumer.kafka.EventuateTramKafkaMessageConsumerConfiguration;
import io.eventuate.tram.spring.messaging.producer.jdbc.TramMessageProducerJdbcConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import pt.ulisboa.tecnico.socialsoftware.common.serviceChannels.ServiceChannels;
import pt.ulisboa.tecnico.socialsoftware.tournament.command.TournamentServiceCommandHandlers;

/**
 * The configuration class to instantiate and wire the command handler.
 */
@Configuration
@Import({ SagaParticipantConfiguration.class, TramMessageProducerJdbcConfiguration.class,
        EventuateTramKafkaMessageConsumerConfiguration.class})
@EnableAutoConfiguration
public class TournamentServiceParticipantConfiguration {

    @Bean
    public TournamentServiceCommandHandlers tournamentServiceCommandHandlers() {
        return new TournamentServiceCommandHandlers();
    }

    @Bean
    public SagaCommandDispatcher authCommandHandlersDispatcher(TournamentServiceCommandHandlers tournamentServiceCommandHandlers, SagaCommandDispatcherFactory sagaCommandDispatcherFactory) {
        return sagaCommandDispatcherFactory.make(ServiceChannels.TOURNAMENT_SERVICE_COMMAND_CHANNEL, tournamentServiceCommandHandlers.commandHandlers());
    }
}
