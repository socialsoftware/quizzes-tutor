package pt.ulisboa.tecnico.socialsoftware.common.events.topic;

import io.eventuate.tram.events.common.DomainEvent;

public class TopicDeletedEvent implements DomainEvent {

    private Integer topicId;

    public TopicDeletedEvent() {
    }

    public TopicDeletedEvent(Integer topicId) {
        this.topicId = topicId;
    }

    public Integer getTopicId() {
        return topicId;
    }
}
