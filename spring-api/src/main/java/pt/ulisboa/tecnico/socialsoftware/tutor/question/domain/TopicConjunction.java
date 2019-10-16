package pt.ulisboa.tecnico.socialsoftware.tutor.question.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.TopicConjunctionDto;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "topic_conjunctions")
public class TopicConjunction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToMany(mappedBy = "topicConjunctions")
    private Set<Topic> topics = new HashSet<>();

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "assessment_id")
    private Assessment assessment;

    public TopicConjunction(){}

    public TopicConjunction(TopicConjunctionDto topicConjunctionDto) {
        this.topics = topicConjunctionDto.getTopics().stream().map(topicDto -> {
            Topic topic = new Topic(topicDto);
            topic.addTopicConjunction(this);
            return topic;
        }).collect(Collectors.toSet());
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Set<Topic> getTopics() {
        return topics;
    }

    public void setTopics(Set<Topic> topics) {
        this.topics = topics;
    }

    public Assessment getAssessment() {
        return assessment;
    }

    public void setAssessment(Assessment assessment) {
        this.assessment = assessment;
    }

    public void addTopic(Topic topic) {
        topics.add(topic);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TopicConjunction that = (TopicConjunction) o;
        return id.equals(that.id) &&
                Objects.equals(topics, that.topics);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, topics);
    }

    @Override
    public String toString() {
        return "TopicConjunction{" +
                "id=" + id +
                ", topics=" + topics +
                '}';
    }
}