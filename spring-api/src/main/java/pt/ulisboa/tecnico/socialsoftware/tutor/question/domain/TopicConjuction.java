package pt.ulisboa.tecnico.socialsoftware.tutor.question.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.TopicConjuctionDto;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(name = "topic_conjuctions")
public class TopicConjuction implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToMany(mappedBy = "topicConjuctions")
    private List<Topic> topics;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "assessment_id")
    private Assessment assessment;

    public TopicConjuction(){}

    public TopicConjuction(List<Topic> topics) {
        this.topics = topics;
    }

    public TopicConjuction(TopicConjuctionDto topicConjuctionDto) {
        this.topics = topicConjuctionDto.getTopics().stream().map(topicDto -> {
            Topic topic = new Topic(topicDto);
            topic.addTopicConjuction(this);
            return topic;
        }).collect(Collectors.toList());
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public List<Topic> getTopics() {
        return topics;
    }

    public void setTopics(List<Topic> topics) {
        this.topics = topics;
    }

    public Assessment getAssessment() {
        return assessment;
    }

    public void setAssessment(Assessment assessment) {
        this.assessment = assessment;
    }
}