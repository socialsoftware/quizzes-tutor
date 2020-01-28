package pt.ulisboa.tecnico.socialsoftware.tutor.question.domain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.AssessmentDto;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "assessments")
public class Assessment {
    private static Logger logger = LoggerFactory.getLogger(Assessment.class);

    @SuppressWarnings("unused")
    public enum Status {
        DISABLED, AVAILABLE, REMOVED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String title;

    @Enumerated(EnumType.STRING)
    private Status status = Status.DISABLED;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "course_execution_id")
    private CourseExecution courseExecution;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "assessment", fetch=FetchType.EAGER)
    private List<TopicConjunction> topicConjunctions = new ArrayList<>();

    public Assessment() {
    }

    public Assessment(CourseExecution courseExecution, List<TopicConjunction> topicConjunctions, AssessmentDto assessmentDto) {
        setTitle(assessmentDto.getTitle());
        setStatus(Assessment.Status.valueOf(assessmentDto.getStatus()));
        setCourseExecution(courseExecution);

        courseExecution.addAssessment(this);

        setTopicConjunctions(topicConjunctions);
        topicConjunctions.forEach(topicConjunction -> topicConjunction.setAssessment(this));
    }

    public void remove() {
        getTopicConjunctions().forEach(TopicConjunction::remove);
        getTopicConjunctions().clear();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getNumber() {
        return id;
    }

    public void setNumber(Integer id) {
        this.id = id;
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

    public CourseExecution getCourseExecution() {
        return courseExecution;
    }

    public void setCourseExecution(CourseExecution courseExecution) {
        this.courseExecution = courseExecution;
    }

    @Override
    public String toString() {
        return "Assessment{" +
                "id=" + id +
                ", id=" + id +
                ", title='" + title + '\'' +
                ", status=" + status +
                ", topicConjunctions=" + topicConjunctions +
                '}';
    }
}