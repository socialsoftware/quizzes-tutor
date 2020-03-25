package pt.ulisboa.tecnico.socialsoftware.tutor.question.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.AssessmentDto;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(name = "assessments")
public class Assessment {
    @SuppressWarnings("unused")
    public enum Status {
        DISABLED, AVAILABLE, REMOVED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String title;
    private Integer sequence = 0;

    @Enumerated(EnumType.STRING)
    private Status status = Status.DISABLED;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "course_execution_id")
    private CourseExecution courseExecution;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "assessment", fetch=FetchType.EAGER, orphanRemoval=true)
    private List<TopicConjunction> topicConjunctions = new ArrayList<>();

    public Assessment() {
    }

    public Assessment(CourseExecution courseExecution, List<TopicConjunction> topicConjunctions, AssessmentDto assessmentDto) {
        setTitle(assessmentDto.getTitle());
        setStatus(Assessment.Status.valueOf(assessmentDto.getStatus()));
        setSequence(assessmentDto.getSequence());
        setCourseExecution(courseExecution);

        courseExecution.addAssessment(this);

        this.topicConjunctions = topicConjunctions;
        topicConjunctions.forEach(topicConjunction -> topicConjunction.setAssessment(this));
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getSequence() {
        return sequence;
    }

    public void setSequence(Integer sequence) {
        this.sequence = sequence;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public CourseExecution getCourseExecution() {
        return courseExecution;
    }

    public void setCourseExecution(CourseExecution courseExecution) {
        this.courseExecution = courseExecution;
    }

    public List<TopicConjunction> getTopicConjunctions() {
        return topicConjunctions;
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

    public void addTopicConjunction(TopicConjunction topicConjunction) {
        this.topicConjunctions.add(topicConjunction);
    }

    public void remove() {
        new ArrayList<>(getTopicConjunctions()).forEach(TopicConjunction::remove);
        getTopicConjunctions().clear();
    }

    public List<Question> getQuestions() {
        return this.topicConjunctions.stream()
                .flatMap(topicConjunction -> topicConjunction.getQuestions().stream())
                .collect(Collectors.toList());
    }

}