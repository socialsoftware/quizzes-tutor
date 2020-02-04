package pt.ulisboa.tecnico.socialsoftware.tutor.course;

import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Assessment;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*;

@Entity
@Table(name = "course_executions")
public class CourseExecution {
    public enum Status {ACTIVE, INACTIVE, HISTORIC}

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String acronym;
    private String academicTerm;

    @Enumerated(EnumType.STRING)
    private Status status;

    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;

    @ManyToMany(mappedBy = "courseExecutions")
    private Set<User> users = new HashSet<>();

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "courseExecution", fetch=FetchType.LAZY, orphanRemoval=true)
    private Set<Quiz> quizzes = new HashSet<>();

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "courseExecution", fetch=FetchType.LAZY, orphanRemoval=true)
    private Set<Assessment> assessments = new HashSet<>();

    public CourseExecution() {
    }

    public CourseExecution(Course course, String acronym, String academicTerm) {
        if (acronym.trim().isEmpty()) {
            throw new TutorException(COURSE_EXECUTION_ACRONYM_IS_EMPTY);
        }
        if (academicTerm.trim().isEmpty()) {
            throw new TutorException(COURSE_EXECUTION_ACADEMIC_TERM_IS_EMPTY);
        }
        if (course.getCourseExecutions().stream()
                .anyMatch(courseExecution -> courseExecution.getAcronym().equals(acronym)
                        && courseExecution.getAcademicTerm().equals(academicTerm))) {
            throw new TutorException(DUPLICATE_COURSE_EXECUTION,acronym + academicTerm);
        }

        this.course = course;
        this.acronym = acronym;
        this.academicTerm = academicTerm;
        this.status = Status.ACTIVE;
        course.addCourseExecution(this);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getAcronym() {
        return acronym;
    }

    public void setAcronym(String acronym) {
        this.acronym = acronym;
    }

    public String getAcademicTerm() {
        return academicTerm;
    }

    public void setAcademicTerm(String academicTerm) {
        this.academicTerm = academicTerm;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public Set<User> getUsers() {
        return users;
    }

    public void setUsers(Set<User> users) {
        this.users = users;
    }

    public Set<Quiz> getQuizzes() {
        return quizzes;
    }

    public void setQuizzes(Set<Quiz> quizzes) {
        this.quizzes = quizzes;
    }

    public Set<Assessment> getAssessments() {
        return assessments;
    }

    public void setAssessments(Set<Assessment> assessments) {
        this.assessments = assessments;
    }

    public void addQuiz(Quiz quiz) {
        quizzes.add(quiz);
    }

    public void addAssessment(Assessment assessment) {
        assessments.add(assessment);
    }

    public void addUser(User user) {
        users.add(user);
    }
}