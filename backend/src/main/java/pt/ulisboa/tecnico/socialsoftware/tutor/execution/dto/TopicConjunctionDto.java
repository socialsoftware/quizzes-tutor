package pt.ulisboa.tecnico.socialsoftware.tutor.execution.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.TopicConjunction;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.TopicDto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TopicConjunctionDto implements Serializable {
    private Integer id;
    private List<TopicDto> topics = new ArrayList<>();

    public TopicConjunctionDto() {}

    public TopicConjunctionDto(TopicConjunction topicConjunction) {
        this.id = topicConjunction.getId();
        this.topics = topicConjunction.getTopics().stream().map(TopicDto::new).collect(Collectors.toList());
    }

    public Integer getId() {
        return id;
    }

    public List<TopicDto> getTopics() {
        return topics;
    }

    public void setTopics(List<TopicDto> topics) {
        this.topics = topics;
    }

    @Override
    public String toString() {
        return "TopicConjunctionDto{" +
                "id=" + id +
                ", topics=" + topics +
                '}';
    }

    public void addTopic(TopicDto topic) {
        this.topics.add(topic);
    }
}