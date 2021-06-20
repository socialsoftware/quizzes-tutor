package pt.ulisboa.tecnico.socialsoftware.auth.subscriptions;

import io.eventuate.tram.events.subscriber.DomainEventDispatcher;
import io.eventuate.tram.events.subscriber.DomainEventDispatcherFactory;
import io.eventuate.tram.spring.events.subscriber.TramEventSubscriberConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import pt.ulisboa.tecnico.socialsoftware.auth.config.AuthServiceRepositoryConfiguration;

@Configuration
@Import({AuthServiceRepositoryConfiguration.class, TramEventSubscriberConfiguration.class})
public class AuthUserServiceEventConfiguration {

    @Bean
    public UserSubscriptions authUserSubscriptions() {
        return new UserSubscriptions();
    }

    @Bean
    public CourseExecutionSubscriptions courseExecutionSubscriptions() {
        return new CourseExecutionSubscriptions();
    }

    @Bean
    public DomainEventDispatcher domainEventDispatcherUser(UserSubscriptions userSubscriptions, DomainEventDispatcherFactory domainEventDispatcherFactory) {
        return domainEventDispatcherFactory.make("authUser_user_events", userSubscriptions.domainEventHandlers());
    }

    @Bean
    public DomainEventDispatcher domainEventDispatcherExecution(CourseExecutionSubscriptions courseExecutionSubscriptions, DomainEventDispatcherFactory domainEventDispatcherFactory) {
        return domainEventDispatcherFactory.make("authUser_execution_events", courseExecutionSubscriptions.domainEventHandlers());
    }
}
