package pt.ulisboa.tecnico.socialsoftware.tutor.question.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.TopicConjuction;

import java.util.List;
import java.util.stream.Collectors;

public class TopicConjuctionDto {
    private Integer id;
    private List<TopicDto> topics;

    public TopicConjuctionDto(){}

    public TopicConjuctionDto(TopicConjuction topicConjuction) {
        this.id = topicConjuction.getId();
        this.topics = topicConjuction.getTopics().stream().map(TopicDto::new).collect(Collectors.toList());
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