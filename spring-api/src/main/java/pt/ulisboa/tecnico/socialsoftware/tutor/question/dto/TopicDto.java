package pt.ulisboa.tecnico.socialsoftware.tutor.question.dto;


import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic;

import java.io.Serializable;

public class TopicDto implements Serializable {
    private String name;
    private String parentTopic;

    public TopicDto() {
    }

    public TopicDto(Topic topic) {
        this.name = topic.getName();
        if (topic.getParentTopic() != null) {
            this.parentTopic = topic.getParentTopic().getName();
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getParentTopic() {
        return parentTopic;
    }

    public void setParentTopic(String parentTopic) {
        this.parentTopic = parentTopic;
    }
}
