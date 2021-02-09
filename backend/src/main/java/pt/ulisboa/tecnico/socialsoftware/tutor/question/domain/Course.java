package pt.ulisboa.tecnico.socialsoftware.tutor.question.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.CourseExecution;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.DomainEntity;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.Visitor;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.INVALID_NAME_FOR_COURSE;
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.INVALID_TYPE_FOR_COURSE;

@Entity
@Table(name = "courses")
public class Course implements DomainEntity {
    public enum Type {TECNICO, EXTERNAL}

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Enumerated(EnumType.STRING)
    private Type type;

    @Column(nullable = false)
    private String name;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "course", fetch=FetchType.LAZY, orphanRemoval=true)
    private final Set<CourseExecution> courseExecutions = new HashSet<>();

    @OneToMany(mappedBy = "course", fetch=FetchType.LAZY, orphanRemoval=true)
    private final Set<Question> questions = new HashSet<>();

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "course", fetch=FetchType.LAZY, orphanRemoval=true)
    private final Set<Topic> topics = new HashSet<>();

    public Course() {}

    public Course(String name, Course.Type type) {
        setType(type);
        setName(name);
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visitCourse(this);
    }


    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null || name.isBlank())
            throw new TutorException(INVALID_NAME_FOR_COURSE);

        this.name = name;
    }

    public Set<CourseExecution> getCourseExecutions() {
        return courseExecutions;
    }

    public Set<Question> getQuestions() {
        return questions;
    }

    public Set<Topic> getTopics() {
        return topics;
    }

    public void addCourseExecution(CourseExecution courseExecution) {
        courseExecutions.add(courseExecution);
    }

    public void addQuestion(Question question) {
        questions.add(question);
    }

    public void addTopic(Topic topic) {
        topics.add(topic);
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        if (type == null)
            throw new TutorException(INVALID_TYPE_FOR_COURSE);

        this.type = type;
    }

    @Override
    public String toString() {
        return "Course{" +
                "id=" + id +
                ", type=" + type +
                ", name='" + name + '\'' +
                ", courseExecutions=" + courseExecutions +
                '}';
    }

    public Optional<CourseExecution> getCourseExecution(String acronym, String academicTerm, Course.Type type) {
        return getCourseExecutions().stream()
                .filter(courseExecution -> courseExecution.getType().equals(type)
                        && courseExecution.getAcronym().equals(acronym)
                        && courseExecution.getAcademicTerm().equals(academicTerm))
                .findAny();
    }

    public boolean existsCourseExecution(String acronym, String academicTerm, Course.Type type) {
        return getCourseExecution(acronym, academicTerm, type).isPresent();
    }
}