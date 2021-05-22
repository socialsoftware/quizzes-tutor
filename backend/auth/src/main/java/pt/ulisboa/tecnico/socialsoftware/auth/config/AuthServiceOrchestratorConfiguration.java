package pt.ulisboa.tecnico.socialsoftware.auth.config;

import io.eventuate.tram.sagas.spring.orchestration.SagaOrchestratorConfiguration;
import io.eventuate.tram.spring.commands.producer.TramCommandProducerConfiguration;
import io.eventuate.tram.spring.consumer.kafka.EventuateTramKafkaMessageConsumerConfiguration;
import io.eventuate.tram.spring.events.publisher.TramEventsPublisherConfiguration;
import io.eventuate.tram.spring.messaging.producer.jdbc.TramMessageProducerJdbcConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import pt.ulisboa.tecnico.socialsoftware.auth.sagas.createUserWithAuth.CreateUserWithAuthSaga;
import pt.ulisboa.tecnico.socialsoftware.auth.sagas.participants.AuthUserServiceProxy;
import pt.ulisboa.tecnico.socialsoftware.auth.sagas.participants.CourseExecutionServiceProxy;
import pt.ulisboa.tecnico.socialsoftware.auth.sagas.participants.UserServiceProxy;
import pt.ulisboa.tecnico.socialsoftware.auth.services.AuthUserService;

/**
 * The configuration class to instantiate and wire the domain service class.
 */
@Configuration
@Import({SagaOrchestratorConfiguration.class, TramMessageProducerJdbcConfiguration.class,
        EventuateTramKafkaMessageConsumerConfiguration.class})
public class AuthServiceOrchestratorConfiguration {


    @Bean
    public CreateUserWithAuthSaga createOrderSaga(AuthUserServiceProxy authUserService, UserServiceProxy userService,
                                                  CourseExecutionServiceProxy courseExecutionService) {
        return new CreateUserWithAuthSaga(authUserService, userService, courseExecutionService);
    }

    @Bean
    public AuthUserServiceProxy authUserServiceProxy() {
        return new AuthUserServiceProxy();
    }

    @Bean
    public UserServiceProxy userServiceProxy() {
        return new UserServiceProxy();
    }

    @Bean
    public CourseExecutionServiceProxy courseExecutionServiceProxy() {
        return new CourseExecutionServiceProxy();
    }
}
