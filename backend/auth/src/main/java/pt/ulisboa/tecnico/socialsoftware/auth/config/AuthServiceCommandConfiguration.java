package pt.ulisboa.tecnico.socialsoftware.auth.config;

import io.eventuate.tram.sagas.participant.SagaCommandDispatcher;
import io.eventuate.tram.sagas.participant.SagaCommandDispatcherFactory;
import io.eventuate.tram.sagas.spring.participant.SagaParticipantConfiguration;
import io.eventuate.tram.spring.consumer.jdbc.TramConsumerJdbcAutoConfiguration;
import io.eventuate.tram.spring.events.publisher.TramEventsPublisherConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import pt.ulisboa.tecnico.socialsoftware.auth.command.AuthServiceCommandHandlers;
import pt.ulisboa.tecnico.socialsoftware.common.serviceChannels.ServiceChannels;

/**
 * The configuration class to instantiate and wire the command handler.
 */
@Configuration
@Import({ SagaParticipantConfiguration.class, TramEventsPublisherConfiguration.class/*, TramConsumerJdbcAutoConfiguration.class*/})
public class AuthServiceCommandConfiguration {

    @Bean
    public AuthServiceCommandHandlers authServiceCommandHandlers() {
        return new AuthServiceCommandHandlers();
    }

    @Bean
    public SagaCommandDispatcher orderCommandHandlersDispatcher(AuthServiceCommandHandlers authServiceCommandHandlers, SagaCommandDispatcherFactory sagaCommandDispatcherFactory) {
        return sagaCommandDispatcherFactory.make(ServiceChannels.AUTH_USER_SERVICE_COMMAND_CHANNEL, authServiceCommandHandlers.commandHandlers());
    }
}
