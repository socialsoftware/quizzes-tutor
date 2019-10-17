package pt.ulisboa.tecnico.socialsoftware.tutor.question.domain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.AssessmentDto;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(name = "assessments")
public class Assessment implements Serializable {
    private static Logger logger = LoggerFactory.getLogger(Assessment.class);

    @SuppressWarnings("unused")
    public enum Status {
        DISABLED, AVAILABLE, REMOVED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "number")
    private Integer number;

    private String title;

    @Enumerated(EnumType.STRING)
    private Status status = Status.DISABLED;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "assessment", fetch=FetchType.EAGER)
    private List<TopicConjunction> topicConjunctions = new ArrayList<>();

    public Assessment() {
    }

    public Assessment(AssessmentDto assessmentDto) {
        this.title = assessmentDto.getTitle();
        this.number = assessmentDto.getNumber();
        this.status = Status.valueOf(assessmentDto.getStatus());
        this.topicConjunctions = assessmentDto.getTopicConjunctions().stream().map(topicConjunctionsDto -> {
            TopicConjunction topicConjunction = new TopicConjunction(topicConjunctionsDto);
            topicConjunction.setAssessment(this);
            return topicConjunction;
        }).collect(Collectors.toList());
    }

    public void remove() {
        getTopicConjunctions().forEach(TopicConjunction::remove);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<TopicConjunction> getTopicConjunctions() {
        return topicConjunctions;
    }

    public void setTopicConjunctions(List<TopicConjunction> topicConjunctions) {
        this.topicConjunctions = topicConjunctions;
    }

    public void addTopicConjunction(TopicConjunction topicConjunction) {
        this.topicConjunctions.add(topicConjunction);
    }

    @Override
    public String toString() {
        return "Assessment{" +
                "id=" + id +
                ", number=" + number +
                ", title='" + title + '\'' +
                ", status=" + status +
                ", topicConjunctions=" + topicConjunctions +
                '}';
    }

    public void update(AssessmentDto assessmentDto) {
        setTitle(assessmentDto.getTitle());
        setStatus(Status.valueOf(assessmentDto.getStatus()));
        setTopicConjunctions(assessmentDto.getTopicConjunctions().stream()
                .map(TopicConjunction::new).collect(Collectors.toList()));
    }
}