package pt.ulisboa.tecnico.socialsoftware.tutor.course;

import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.Importable;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.COURSE_NAME_IS_EMPTY;

@Entity
@Table(name = "courses")
public class Course implements Importable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "course", fetch=FetchType.LAZY, orphanRemoval=true)
    private Set<CourseExecution> courseExecutions = new HashSet<>();

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "course", fetch=FetchType.LAZY, orphanRemoval=true)
    private Set<Question> questions = new HashSet<>();

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "course", fetch=FetchType.LAZY, orphanRemoval=true)
    private Set<Topic> topics = new HashSet<>();

    public Course() {}

    public Course(String name) {
        if (name.trim().isEmpty()) {
            throw new TutorException(COURSE_NAME_IS_EMPTY);
        }

        this.name = name;
    }

    public Optional<CourseExecution> getCourseExecution(String acronym, String academicTerm) {
        return getCourseExecutions().stream()
                .filter(courseExecution -> courseExecution.getAcronym().equals(acronym)
                                            && courseExecution.getAcademicTerm().equals(academicTerm))
                .findAny();
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
}