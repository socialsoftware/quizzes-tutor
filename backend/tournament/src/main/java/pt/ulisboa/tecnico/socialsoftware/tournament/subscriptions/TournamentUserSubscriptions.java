package pt.ulisboa.tecnico.socialsoftware.tournament.subscriptions;

import io.eventuate.tram.events.subscriber.DomainEventEnvelope;
import io.eventuate.tram.events.subscriber.DomainEventHandlers;
import io.eventuate.tram.events.subscriber.DomainEventHandlersBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import pt.ulisboa.tecnico.socialsoftware.common.events.AnonymizeUserEvent;
import pt.ulisboa.tecnico.socialsoftware.tournament.domain.TournamentCreator;
import pt.ulisboa.tecnico.socialsoftware.tournament.domain.TournamentParticipant;
import pt.ulisboa.tecnico.socialsoftware.tournament.repository.TournamentRepository;

import static pt.ulisboa.tecnico.socialsoftware.common.events.EventAggregateTypes.USER_AGGREGATE_TYPE;

public class TournamentUserSubscriptions {

    private static final Logger logger = LoggerFactory.getLogger(TournamentUserSubscriptions.class);

    @Autowired
    private TournamentRepository tournamentRepository;

    public DomainEventHandlers domainEventHandlers() {
        return DomainEventHandlersBuilder
                .forAggregateType(USER_AGGREGATE_TYPE)
                .onEvent(AnonymizeUserEvent.class, this::anonymizeUser)
                .build();
    }

    public void anonymizeUser(DomainEventEnvelope<AnonymizeUserEvent> event) {
        logger.info("Received anonymizeUserEvent!");
        AnonymizeUserEvent anonymizeUserEvent = event.getEvent();

        tournamentRepository.findAll().forEach(tournament -> {
            TournamentCreator creator = tournament.getCreator();

            if (creator.getId().equals(anonymizeUserEvent.getId())) {
                creator.setName(anonymizeUserEvent.getName());
                creator.setUsername(anonymizeUserEvent.getUsername());
            }

            for (TournamentParticipant participant : tournament.getParticipants()) {
                if (participant.getId().equals(anonymizeUserEvent.getId())) {
                    participant.setName(anonymizeUserEvent.getName());
                    participant.setUsername(anonymizeUserEvent.getUsername());
                    break;
                }
            }
        });
    }
}
