package pt.ulisboa.tecnico.socialsoftware.auth.config;

import io.eventuate.tram.sagas.participant.SagaCommandDispatcher;
import io.eventuate.tram.sagas.participant.SagaCommandDispatcherFactory;
import io.eventuate.tram.sagas.spring.participant.SagaParticipantConfiguration;
import io.eventuate.tram.spring.consumer.jdbc.TramConsumerJdbcAutoConfiguration;
import io.eventuate.tram.spring.consumer.kafka.EventuateTramKafkaMessageConsumerConfiguration;
import io.eventuate.tram.spring.events.publisher.TramEventsPublisherConfiguration;
import io.eventuate.tram.spring.messaging.producer.jdbc.TramMessageProducerJdbcConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import pt.ulisboa.tecnico.socialsoftware.auth.command.AuthServiceCommandHandlers;
import pt.ulisboa.tecnico.socialsoftware.common.serviceChannels.ServiceChannels;

/**
 * The configuration class to instantiate and wire the command handler.
 */
@Configuration
@Import({ SagaParticipantConfiguration.class, TramMessageProducerJdbcConfiguration.class,
        EventuateTramKafkaMessageConsumerConfiguration.class/*, TramConsumerJdbcAutoConfiguration.class*/})
@EnableAutoConfiguration
public class AuthServiceParticipantConfiguration {

    @Bean
    public AuthServiceCommandHandlers authServiceCommandHandlers() {
        return new AuthServiceCommandHandlers();
    }

    @Bean
    public SagaCommandDispatcher authCommandHandlersDispatcher(AuthServiceCommandHandlers authServiceCommandHandlers, SagaCommandDispatcherFactory sagaCommandDispatcherFactory) {
        return sagaCommandDispatcherFactory.make(ServiceChannels.AUTH_USER_SERVICE_COMMAND_CHANNEL, authServiceCommandHandlers.commandHandlers());
    }
}
