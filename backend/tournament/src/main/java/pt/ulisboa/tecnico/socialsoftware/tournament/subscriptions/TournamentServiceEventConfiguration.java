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
    public TournamentSubscriptions tournamentSubscriptions() {
        return new TournamentSubscriptions();
    }

    @Bean
    public DomainEventDispatcher domainEventDispatcher(TournamentSubscriptions tournamentSubscriptions, DomainEventDispatcherFactory domainEventDispatcherFactory) {
        return domainEventDispatcherFactory.make("tournament_quiz_events", tournamentSubscriptions.domainEventHandlers());
    }
}
