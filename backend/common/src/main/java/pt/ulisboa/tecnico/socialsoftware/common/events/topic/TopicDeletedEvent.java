package pt.ulisboa.tecnico.socialsoftware.common.events.topic;

public class TopicDeletedEvent{

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
