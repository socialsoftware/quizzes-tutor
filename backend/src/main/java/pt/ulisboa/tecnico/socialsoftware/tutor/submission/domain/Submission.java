package pt.ulisboa.tecnico.socialsoftware.tutor.submission.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;

import javax.persistence.*;

@Entity
@Table(name = "submissions")
public class Submission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private Question question;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "course_execution_id")
    private CourseExecution courseExecution;

    @Column(columnDefinition = "boolean default false", nullable = false)
    private boolean anonymous;

    public Submission() {
    }

    public Submission(CourseExecution courseExecution, Question question, User user) {
        this.courseExecution = courseExecution;
        this.question = question;
        this.user = user;
        user.addSubmission(this);
        courseExecution.addSubmission(this);
    }

    public String toString() {
        return "Submission{" + "id=" + id + ", question=" + question + ", user=" + user + ", anonymous="+ anonymous +", courseExecution=" + courseExecution + "}";
    }

    public Integer getId() { return id; }

    public Question getQuestion() { return question; }

    public void setQuestion(Question question) { this.question = question; }

    public User getUser() { return user; }

    public void setUser(User user) { this.user = user; }

    public CourseExecution getCourseExecution() { return courseExecution; }

    public void setCourseExecution(CourseExecution courseExecution) { this.courseExecution = courseExecution; }

    public boolean isAnonymous() { return anonymous; }

    public void setAnonymous(boolean anonymous) { this.anonymous = anonymous; }
}
