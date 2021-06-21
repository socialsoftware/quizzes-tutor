package pt.ulisboa.tecnico.socialsoftware.auth.config;

import io.eventuate.tram.sagas.spring.orchestration.SagaOrchestratorConfiguration;
import io.eventuate.tram.spring.consumer.kafka.EventuateTramKafkaMessageConsumerConfiguration;
import io.eventuate.tram.spring.messaging.producer.jdbc.TramMessageProducerJdbcConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import pt.ulisboa.tecnico.socialsoftware.auth.sagas.confirmRegistration.ConfirmRegistrationSaga;
import pt.ulisboa.tecnico.socialsoftware.auth.sagas.createUserWithAuth.CreateUserWithAuthSaga;
import pt.ulisboa.tecnico.socialsoftware.auth.sagas.participants.AuthUserServiceProxy;
import pt.ulisboa.tecnico.socialsoftware.auth.sagas.participants.CourseExecutionServiceProxy;
import pt.ulisboa.tecnico.socialsoftware.auth.sagas.participants.UserServiceProxy;
import pt.ulisboa.tecnico.socialsoftware.auth.sagas.updateCourseExecutions.UpdateCourseExecutionsSaga;

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
    public UpdateCourseExecutionsSaga updateCourseExecutionsSaga(AuthUserServiceProxy authUserService,
                                                                 CourseExecutionServiceProxy courseExecutionService) {
        return new UpdateCourseExecutionsSaga(authUserService, courseExecutionService);
    }

    @Bean
    public ConfirmRegistrationSaga confirmRegistrationSaga(AuthUserServiceProxy authUserService,
                                                              UserServiceProxy userService) {
        return new ConfirmRegistrationSaga(authUserService, userService);
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
