package pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic;

import javax.persistence.*;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "topic_conjunctions",
        indexes = {
                @Index(name = "topic_conjunctions_indx_0", columnList = "assessment_id")
        })
public class TopicConjunction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToMany(cascade = CascadeType.ALL, mappedBy = "topicConjunctions", fetch=FetchType.EAGER)
    private Set<Topic> topics = new HashSet<>();

    @ManyToOne(fetch=FetchType.LAZY, optional=false)
    @JoinColumn(name = "assessment_id")
    private Assessment assessment;

    public Integer getId() {
        return id;
    }

    public Set<Topic> getTopics() {
        return topics;
    }

    public Assessment getAssessment() {
        return assessment;
    }

    public void setAssessment(Assessment assessment) {
        this.assessment = assessment;
        if (assessment != null) {
            assessment.addTopicConjunction(this);
        }
    }

    public void addTopic(Topic topic) {
        topics.add(topic);
        topic.addTopicConjunction(this);
    }

    public void remove() {
        getTopics().forEach(topic -> topic.getTopicConjunctions().remove(this));
        getTopics().clear();
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

    public void updateTopics(Set<Topic> newTopics) {
        Set<Topic> toRemove = this.topics.stream().filter(topic -> !newTopics.contains(topic)).collect(Collectors.toSet());

        toRemove.forEach(topic -> {
            this.topics.remove(topic);
            topic.getTopicConjunctions().remove(this);
        });

        newTopics.stream().filter(topic -> !this.topics.contains(topic)).forEach(this::addTopic);
    }

    public List<Question> getQuestions() {
        return this.topics.stream()
                .flatMap(topic -> topic.getQuestions().stream())
                .filter(question -> this.topics.equals(question.getTopics()))
                .collect(Collectors.toList());
    }
}