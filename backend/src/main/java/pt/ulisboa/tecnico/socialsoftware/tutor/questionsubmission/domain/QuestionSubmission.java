package pt.ulisboa.tecnico.socialsoftware.tutor.questionsubmission.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.course.domain.CourseExecution;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "question_submissions")
public class QuestionSubmission {

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

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "questionSubmission", fetch = FetchType.LAZY, orphanRemoval = true)
    private Set<Review> reviews = new HashSet<>();

    public QuestionSubmission() {
    }

    public QuestionSubmission(CourseExecution courseExecution, Question question, User user) {
        this.courseExecution = courseExecution;
        this.question = question;
        this.user = user;
        user.addQuestionSubmission(this);
        courseExecution.addQuestionSubmission(this);
    }

    public String toString() {
        return "QuestionSubmission{" + "id=" + id + ", question=" + question + ", user=" + user + ", courseExecution=" + courseExecution + "}";
    }

    public Integer getId() { return id; }

    public Question getQuestion() { return question; }

    public void setQuestion(Question question) { this.question = question; }

    public User getUser() { return user; }

    public void setUser(User user) { this.user = user; }

    public CourseExecution getCourseExecution() { return courseExecution; }

    public void setCourseExecution(CourseExecution courseExecution) { this.courseExecution = courseExecution; }

    public Set<Review> getReviews() { return reviews; }

    public void addReview(Review review) { this.reviews.add(review); }

    public void remove() {
        getCourseExecution().getQuestionSubmissions().remove(this);
        getUser().getQuestionSubmissions().remove(this);

        this.courseExecution = null;
        this.user = null;

        question.remove();
        getReviews().forEach(Review::remove);
    }
}
