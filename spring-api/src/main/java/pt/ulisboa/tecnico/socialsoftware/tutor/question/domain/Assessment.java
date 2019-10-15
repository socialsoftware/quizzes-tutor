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
@Table(name = "assessment_topics")
public class Assessment implements Serializable {
    private static Logger logger = LoggerFactory.getLogger(Assessment.class);

    @SuppressWarnings("unused")
    public enum Status {
        DISABLED, AVAILABLE
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
    private List<TopicConjuction> topicConjuctions = new ArrayList<>();

    public Assessment() {
    }

    public Assessment(AssessmentDto assessmentDto) {
        this.title = assessmentDto.getTitle();
        this.number = assessmentDto.getNumber();
        this.status = Status.valueOf(assessmentDto.getStatus());
        this.topicConjuctions = assessmentDto.getTopicConjuctions().stream().map(topicConjuctionsDto -> {
            TopicConjuction topicConjuction = new TopicConjuction(topicConjuctionsDto);
            topicConjuction.setAssessment(this);
            return topicConjuction;
        }).collect(Collectors.toList());
    }

    public void remove() {
        getTopicConjuctions().clear();
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

    public List<TopicConjuction> getTopicConjuctions() {
        return topicConjuctions;
    }

    public void setTopicConjuctions(List<TopicConjuction> topicConjuctions) {
        this.topicConjuctions = topicConjuctions;
    }

    public void update(AssessmentDto assessmentDto) {
        setTitle(assessmentDto.getTitle());
        setStatus(Status.valueOf(assessmentDto.getStatus()));
        setTopicConjuctions(assessmentDto.getTopicConjuctions().stream()
                .map(TopicConjuction::new).collect(Collectors.toList()));
    }
}