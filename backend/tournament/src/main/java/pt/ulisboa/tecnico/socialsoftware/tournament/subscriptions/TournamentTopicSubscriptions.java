package pt.ulisboa.tecnico.socialsoftware.tournament.subscriptions;

import io.eventuate.tram.events.subscriber.DomainEventEnvelope;
import io.eventuate.tram.events.subscriber.DomainEventHandlers;
import io.eventuate.tram.events.subscriber.DomainEventHandlersBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import pt.ulisboa.tecnico.socialsoftware.common.events.TopicDeletedEvent;
import pt.ulisboa.tecnico.socialsoftware.common.events.TopicUpdatedEvent;
import pt.ulisboa.tecnico.socialsoftware.tournament.domain.TournamentTopic;
import pt.ulisboa.tecnico.socialsoftware.tournament.repository.TournamentRepository;

import static pt.ulisboa.tecnico.socialsoftware.common.events.EventAggregateTypes.TOPIC_AGGREGATE_TYPE;

public class TournamentTopicSubscriptions {
    private static final Logger logger = LoggerFactory.getLogger(TournamentTopicSubscriptions.class);

    @Autowired
    private TournamentRepository tournamentRepository;

    public DomainEventHandlers domainEventHandlers() {
        return DomainEventHandlersBuilder
                .forAggregateType(TOPIC_AGGREGATE_TYPE)
                .onEvent(TopicUpdatedEvent.class, this::updateTopicName)
                .onEvent(TopicDeletedEvent.class, this::deleteTopic)
                .build();
    }

    public void updateTopicName(DomainEventEnvelope<TopicUpdatedEvent> event) {
        logger.info("Received TopicUpdatedEvent!");
        TopicUpdatedEvent topicUpdatedEvent = event.getEvent();
        tournamentRepository.findAll().forEach(tournament -> {
            for (TournamentTopic topic : tournament.getTopics()) {
                if (topic.getId().equals(topicUpdatedEvent.getTopicId())) {
                    topic.setName(topicUpdatedEvent.getNewName());
                    break;
                }
            }
        });
    }

    public void deleteTopic(DomainEventEnvelope<TopicDeletedEvent> event) {
        logger.info("Received TopicDeletedEvent!");
        TopicDeletedEvent topicDeletedEvent = event.getEvent();
        tournamentRepository.findAll().forEach(tournament -> tournament.getTopics()
                .removeIf(topic -> topic.getId().equals(topicDeletedEvent.getTopicId())));
    }
}
