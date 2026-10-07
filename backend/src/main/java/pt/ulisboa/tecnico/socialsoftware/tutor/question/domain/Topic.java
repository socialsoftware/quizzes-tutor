package pt.ulisboa.tecnico.socialsoftware.tutor.question.domain;

import jakarta.persistence.*;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.TopicConjunction;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.DomainEntity;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.Visitor;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.TopicDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.TopicSourceDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.domain.Tournament;

import java.util.*;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.INVALID_NAME_FOR_TOPIC;

@Entity
@Table(name = "topics")
public class Topic implements DomainEntity {
    public enum Status {
        DISABLED, REMOVED, AVAILABLE
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String name;

    @ManyToMany
    private final Set<Question> questions = new HashSet<>();

    @ManyToMany
    private final List<TopicConjunction> topicConjunctions = new ArrayList<>();

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "course_id")
    private Course course;

    @ManyToMany(mappedBy = "topics")
    private Set<Tournament> tournaments = new HashSet<>();

    // The topic above this one, as a plain id: it keeps topics cheap to turn into DTOs outside a
    // transaction. Topics without one are at the top of the course's tree
    @Column(name = "parent_id")
    private Integer parentId;

    // Position among the topics with the same parent; null for topics created before the tree existed
    private Integer sequence;

    @OneToMany(mappedBy = "topic", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<TopicSource> sources = new ArrayList<>();

    public Topic() {
    }

    public Topic(Course course, TopicDto topicDto) {
        setName(topicDto.getName());
        setCourse(course);
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visitTopic(this);
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null || name.isBlank())
            throw new TutorException(INVALID_NAME_FOR_TOPIC);

        this.name = name;
    }

    public Set<Question> getQuestions() {
        return questions;
    }

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    public Integer getSequence() {
        return sequence;
    }

    public void setSequence(Integer sequence) {
        this.sequence = sequence;
    }

    public List<TopicSource> getSources() {
        return sources;
    }

    /** Adds a document section this topic is taught from; false if it was already there. */
    public boolean addSource(String materialId, String sectionPath) {
        if (sources.stream().anyMatch(source -> source.is(materialId, sectionPath)))
            return false;
        sources.add(new TopicSource(this, materialId, sectionPath));
        return true;
    }

    /**
     * A section of a document changed its path (or was cut in several): the link follows the text.
     * Links already there are not repeated; false if this topic had no link to the old path.
     */
    public boolean followSource(String materialId, String oldPath, List<String> newPaths) {
        TopicSource old = sources.stream().filter(source -> source.is(materialId, oldPath)).findFirst().orElse(null);
        if (old == null)
            return false;
        if (!newPaths.contains(oldPath))
            sources.remove(old);
        newPaths.forEach(path -> addSource(materialId, path));
        return true;
    }

    public void replaceSources(List<TopicSourceDto> newSources) {
        sources.clear();
        newSources.forEach(source -> addSource(source.getMaterialId(), source.getSectionPath()));
    }

    public Set<Tournament> getTournaments() {
        return tournaments;
    }

    public void addQuestion(Question question) {
        this.questions.add(question);
    }

    public List<TopicConjunction> getTopicConjunctions() {
        return topicConjunctions;
    }

    public void addTopicConjunction(TopicConjunction topicConjunction) {
        this.topicConjunctions.add(topicConjunction);
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
        course.addTopic(this);
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
                '}';
    }

    public void remove() {
        course.getTopics().remove(this);
        course = null;

        questions.forEach(question -> question.getTopics().remove(this));
        questions.clear();

        topicConjunctions.forEach(topicConjunction -> topicConjunction.getTopics().remove(this));
        topicConjunctions.clear();

        tournaments.forEach(tournament -> tournament.getTopics().remove(this));
        tournaments.clear();
    }
}
