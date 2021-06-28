package pt.ulisboa.tecnico.socialsoftware.common.events;

public class TopicUpdatedEvent {
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
