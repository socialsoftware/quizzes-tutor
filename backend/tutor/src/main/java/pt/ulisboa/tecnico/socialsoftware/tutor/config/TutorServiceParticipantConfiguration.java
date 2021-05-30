package pt.ulisboa.tecnico.socialsoftware.tutor.config;

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
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.CourseExecutionService;
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.command.CourseExecutionServiceCommandHandlers;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.command.UserServiceCommandHandlers;

/**
 * The configuration class to instantiate and wire the command handler.
 */
@Configuration
@Import({ SagaParticipantConfiguration.class, TramMessageProducerJdbcConfiguration.class,
        EventuateTramKafkaMessageConsumerConfiguration.class})
@EnableAutoConfiguration
public class TutorServiceParticipantConfiguration {

    @Bean
    public CourseExecutionServiceCommandHandlers courseExecutionServiceCommandHandlers() {
        return new CourseExecutionServiceCommandHandlers();
    }

    @Bean
    public UserServiceCommandHandlers userServiceCommandHandlers() {
        return new UserServiceCommandHandlers();
    }

    @Bean
    public SagaCommandDispatcher userCommandHandlersDispatcher(UserServiceCommandHandlers userServiceCommandHandlers, SagaCommandDispatcherFactory sagaCommandDispatcherFactory) {
        return sagaCommandDispatcherFactory.make(ServiceChannels.USER_SERVICE_COMMAND_CHANNEL, userServiceCommandHandlers.commandHandlers());
    }

    @Bean
    public SagaCommandDispatcher courseExecutionCommandHandlersDispatcher(CourseExecutionServiceCommandHandlers courseExecutionServiceCommandHandlers, SagaCommandDispatcherFactory sagaCommandDispatcherFactory) {
        return sagaCommandDispatcherFactory.make(ServiceChannels.COURSE_EXECUTION_SERVICE_COMMAND_CHANNEL, courseExecutionServiceCommandHandlers.commandHandlers());
    }
}
