package pt.ulisboa.tecnico.socialsoftware.tournament.subscriptions;

import io.eventuate.tram.events.subscriber.DomainEventDispatcher;
import io.eventuate.tram.events.subscriber.DomainEventDispatcherFactory;
import io.eventuate.tram.spring.events.subscriber.TramEventSubscriberConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import pt.ulisboa.tecnico.socialsoftware.tournament.config.TournamentServiceRepositoryConfiguration;

@Configuration
@Import({TournamentServiceRepositoryConfiguration.class, TramEventSubscriberConfiguration.class})
public class TournamentServiceEventConfiguration {

    @Bean
    public TournamentQuizSubscriptions tournamentSubscriptions() {
        return new TournamentQuizSubscriptions();
    }

    @Bean
    public TournamentTopicSubscriptions topicSubscriptions() {
        return new TournamentTopicSubscriptions();
    }

    @Bean
    public TournamentCourseExecutionSubscriptions tournamentCourseExecutionSubscriptions() {
        return new TournamentCourseExecutionSubscriptions();
    }

    @Bean
    public DomainEventDispatcher domainEventDispatcher(TournamentQuizSubscriptions tournamentSubscriptions, DomainEventDispatcherFactory domainEventDispatcherFactory) {
        return domainEventDispatcherFactory.make("tournament_quiz_events", tournamentSubscriptions.domainEventHandlers());
    }

    @Bean
    public DomainEventDispatcher domainEventDispatcherUser(TournamentTopicSubscriptions topicSubscriptions, DomainEventDispatcherFactory domainEventDispatcherFactory) {
        return domainEventDispatcherFactory.make("tournament_topic_events", topicSubscriptions.domainEventHandlers());
    }

    @Bean
    public DomainEventDispatcher domainEventDispatcherExecution(TournamentCourseExecutionSubscriptions tournamentCourseExecutionSubscriptions, DomainEventDispatcherFactory domainEventDispatcherFactory) {
        return domainEventDispatcherFactory.make("tournament_execution_events", tournamentCourseExecutionSubscriptions.domainEventHandlers());
    }

}
