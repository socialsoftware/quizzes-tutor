package pt.ulisboa.tecnico.socialsoftware.tutor.question.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.Importable;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.TopicDto;

import javax.persistence.*;
import java.util.*;

@Entity
@Table(name = "topics")
public class Topic implements Importable {
    @SuppressWarnings("unused")
    public enum Status {
        DISABLED, REMOVED, AVAILABLE
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    @ManyToMany
    private Set<Question> questions = new HashSet<>();

    @ManyToOne
    private Topic parentTopic;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "parentTopic", fetch=FetchType.EAGER)
    private Set<Topic> childrenTopics = new HashSet<>();

    @ManyToMany(cascade = CascadeType.ALL, fetch=FetchType.EAGER)
    private List<TopicConjunction> topicConjunctions = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;

    public Topic() {
    }

    public Topic(Course course, TopicDto topicDto) {
        this.name = topicDto.getName();
        this.course = course;
        course.addTopic(this);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Question> getQuestions() {
        return questions;
    }

    public Topic getParentTopic() {
        return parentTopic;
    }

    public void setParentTopic(Topic parentTopic) {
        this.parentTopic = parentTopic;
    }

    public Set<Topic> getChildrenTopics() {
        return childrenTopics;
    }

    public List<TopicConjunction> getTopicConjunctions() {
        return topicConjunctions;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public void addTopicConjunction(TopicConjunction topicConjunction) {
        this.topicConjunctions.add(topicConjunction);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Topic topic = (Topic) o;
        return name.equals(topic.name);
    }

    @Override
    public int hashCode() {

        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return "Topic{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", parentTopic=" + parentTopic +
                '}';
    }

    public void remove() {
        getCourse().getTopics().remove(this);
        course = null;

        getQuestions().forEach(question -> question.getTopics().remove(this));
        getQuestions().clear();

        if (this.parentTopic != null) {
            parentTopic.getChildrenTopics().remove(this);
            parentTopic.getChildrenTopics().addAll(this.getChildrenTopics());
        }

        this.childrenTopics.forEach(topic -> topic.parentTopic = this.parentTopic);
        this.topicConjunctions.forEach(topicConjunction -> topicConjunction.getTopics().remove(this));

        this.parentTopic = null;
        this.childrenTopics.clear();
    }
}
