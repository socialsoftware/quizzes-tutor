package pt.ulisboa.tecnico.socialsoftware.tutor.course;

import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic;

import javax.persistence.*;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.COURSE_NAME_IS_EMPTY;

@Entity
@Table(name = "courses")
public class Course {
    public static final String DEMO_COURSE = "Demo Course";
    public static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public enum Type {TECNICO, EXTERNAL}

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Enumerated(EnumType.STRING)
    private Type type;

    private String name;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "course", fetch=FetchType.LAZY, orphanRemoval=true)
    private Set<CourseExecution> courseExecutions = new HashSet<>();

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "course", fetch=FetchType.LAZY, orphanRemoval=true)
    private Set<Question> questions = new HashSet<>();

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "course", fetch=FetchType.LAZY, orphanRemoval=true)
    private Set<Topic> topics = new HashSet<>();

    public Course() {}

    public Course(String name, Course.Type type) {
        if (name == null || name.trim().isEmpty()) {
            throw new TutorException(COURSE_NAME_IS_EMPTY);
        }

        this.type = type;
        this.name = name;
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
        this.type = type;
    }
}