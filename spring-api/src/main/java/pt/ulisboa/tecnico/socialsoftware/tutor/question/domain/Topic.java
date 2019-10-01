package pt.ulisboa.tecnico.socialsoftware.tutor.question.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.TopicDto;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "topics")
public class Topic implements Serializable {
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

    public Topic() {
    }

    public Topic(TopicDto topicDto) {
        this.name = topicDto.getName();
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

    public void setQuestions(Set<Question> questions) {
        this.questions = questions;
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

    public void setChildrenTopics(Set<Topic> childrenTopics) {
        this.childrenTopics = childrenTopics;
    }

    public void remove() {
        getQuestions().forEach(question -> question.getTopics().remove(this));
        getQuestions().clear();
    }


}
