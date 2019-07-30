package pt.ulisboa.tecnico.socialsoftware.tutor.topic.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.topic.domain.Topic;

import java.io.Serializable;


public class TopicDto implements Serializable {
    private Integer id;
    private TopicDto parent;
    private String name;

    public TopicDto(Topic topic) {
        this.id = topic.getId();
        this.parent = new TopicDto(topic.getParent());
        this.name = topic.getName();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public TopicDto getParent() {
        return parent;
    }

    public void setParent(TopicDto parent) {
        this.parent = parent;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}