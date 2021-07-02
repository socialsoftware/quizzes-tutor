package pt.ulisboa.tecnico.socialsoftware.common.events;

import io.eventuate.tram.events.common.DomainEvent;

public class TopicUpdatedEvent implements DomainEvent {
    private Integer topicId;
    private String newName;

    public TopicUpdatedEvent() {
    }

    public TopicUpdatedEvent(Integer topicId, String newName) {
        this.topicId = topicId;
        this.newName = newName;
    }

    public Integer getTopicId() {
        return topicId;
    }

    public String getNewName() {
        return newName;
    }
}
