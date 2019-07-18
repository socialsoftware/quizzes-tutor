package com.example.tutor.topic;

import java.io.Serializable;


public class TopicDTO implements Serializable {
    private Integer id;
    private TopicDTO parent;
    private String name;

    public TopicDTO(Topic topic) {
        this.id = topic.getId();
        this.parent = new TopicDTO(topic.getParent());
        this.name = topic.getName();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public TopicDTO getParent() {
        return parent;
    }

    public void setParent(TopicDTO parent) {
        this.parent = parent;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}