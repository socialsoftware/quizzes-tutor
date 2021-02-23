package pt.ulisboa.tecnico.socialsoftware.tutor.questionsubmission.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.CourseExecution;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.User;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.INVALID_TYPE_FOR_REVIEW;

@Entity
@Table(name = "question_submissions")
public class QuestionSubmission {
    public enum Status {
        APPROVED, REJECTED, IN_REVISION, IN_REVIEW
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private Question question;

    @ManyToOne
    @JoinColumn(name = "submitter_id")
    private User submitter;

    @ManyToOne
    @JoinColumn(name = "course_execution_id")
    private CourseExecution courseExecution;

    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(columnDefinition = "boolean default true", nullable = false)
    private boolean studentRead;

    @Column(columnDefinition = "boolean default true", nullable = false)
    private boolean teacherRead;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "questionSubmission", fetch = FetchType.LAZY, orphanRemoval = true)
    private Set<Review> reviews = new HashSet<>();

    public QuestionSubmission() {
    }

    public QuestionSubmission(CourseExecution courseExecution, Question question, User submitter) {
        setCourseExecution(courseExecution);
        setQuestion(question);
        setSubmitter(submitter);
        setStatus(Status.IN_REVISION);
        setStudentRead(true);
        setTeacherRead(true);
        submitter.addQuestionSubmission(this);
        courseExecution.addQuestionSubmission(this);
    }

    public String toString() {
        return "QuestionSubmission{" + "id=" + id + ", question=" + question + ", submitter=" + submitter + ", courseExecution=" + courseExecution + ", status=" + status.name() + "}";
    }

    public Integer getId() { return id; }

    public Question getQuestion() { return question; }

    public void setQuestion(Question question) { this.question = question; }

    public User getSubmitter() { return submitter; }

    public void setSubmitter(User submitter) { this.submitter = submitter; }

    public CourseExecution getCourseExecution() { return courseExecution; }

    public void setCourseExecution(CourseExecution courseExecution) { this.courseExecution = courseExecution; }

    public Set<Review> getReviews() { return reviews; }

    public void addReview(Review review) { this.reviews.add(review); }

    public boolean hasStudentRead() { return studentRead; }

    public void setStudentRead(boolean studentRead) { this.studentRead = studentRead; }

    public boolean hasTeacherRead() { return teacherRead; }

    public void setTeacherRead(boolean teacherRead) {
        this.teacherRead = teacherRead;
    }

    public Status getStatus() { return status; }

    public void setStatus(Status status) {
        this.status = status;
        if (status == Status.APPROVED) {
            this.question.setStatus(Question.Status.AVAILABLE);
        }
    }

    public void setStatus(String reviewType) {
        try {
            switch (Review.Type.valueOf(reviewType)) {
                case APPROVE:
                    setStatus(Status.APPROVED);
                    break;
                case REJECT:
                    setStatus(Status.REJECTED);
                    break;
                case REQUEST_CHANGES:
                    setStatus(Status.IN_REVISION);
                    break;
                case REQUEST_REVIEW:
                    setStatus(Status.IN_REVIEW);
                    break;
                case COMMENT:
                    break;
                default:
                    throw new TutorException(INVALID_TYPE_FOR_REVIEW);
            }
        } catch (IllegalArgumentException e) {
            throw new TutorException(INVALID_TYPE_FOR_REVIEW);
        }
    }

    public void remove() {
        getCourseExecution().getQuestionSubmissions().remove(this);
        this.courseExecution = null;

        getSubmitter().getQuestionSubmissions().remove(this);
        this.submitter = null;

        question.remove();

        reviews.forEach(Review::remove);
    }
}
