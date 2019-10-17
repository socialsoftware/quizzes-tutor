package pt.ulisboa.tecnico.socialsoftware.tutor.question.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.TopicConjunction;

import java.util.List;
import java.util.stream.Collectors;

public class TopicConjunctionDto {
    private Integer id;
    private List<TopicDto> topics;

    public TopicConjunctionDto(){}

    public TopicConjunctionDto(TopicConjunction topicConjunction) {
        this.id = topicConjunction.getId();
        this.topics = topicConjunction.getTopics().stream().map(TopicDto::new).collect(Collectors.toList());
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public List<TopicDto> getTopics() {
        return topics;
    }

    public void setTopics(List<TopicDto> topics) {
        this.topics = topics;
    }


}