package pt.ulisboa.tecnico.socialsoftware.tutor.questionsubmission.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.config.DateHandler;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsubmission.dto.ReviewDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;

import javax.persistence.*;
import java.time.LocalDateTime;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.INVALID_STATUS_FOR_QUESTION;
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.REVIEW_MISSING_COMMENT;

@Entity
@Table(name = "reviews")
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String comment;

    @Column(name = "creation_date")
    private LocalDateTime creationDate;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "question_submission_id")
    private QuestionSubmission questionSubmission;

    @Column(columnDefinition = "TEXT")
    private String submissionStatus;

    public Review() {
    }

    public Review(User user, QuestionSubmission questionSubmission, ReviewDto reviewDto) {
        setComment(reviewDto.getComment());
        setUser(user);
        setQuestionSubmission(questionSubmission);
        setCreationDate(DateHandler.toLocalDateTime(reviewDto.getCreationDate()));
        setSubmissionStatus(reviewDto.getSubmissionStatus());
    }

    @Override
    public String toString() {
        return "Review{" + "id=" + id + "', user=" + user + ", comment='" + comment + ", questionSubmission=" + questionSubmission.getQuestion() + "}";
    }

    public Integer getId() { return id; }

    public String getComment() { return comment; }

    public void setComment(String comment) {
        if (comment == null || comment.isBlank()) {
            throw new TutorException(REVIEW_MISSING_COMMENT);
        }
        this.comment = comment;
    }

    public LocalDateTime getCreationDate() { return creationDate; }

    public void setCreationDate(LocalDateTime creationDate) {
        if (this.creationDate == null) {
            this.creationDate = DateHandler.now();
        }
        else {
            this.creationDate = creationDate;
        }
    }

    public User getUser() { return user; }

    public void setUser(User user) { this.user = user; }

    public QuestionSubmission getQuestionSubmission() { return questionSubmission; }

    public void setQuestionSubmission(QuestionSubmission questionSubmission) { this.questionSubmission = questionSubmission; }

    public String getSubmissionStatus() { return submissionStatus; }

    public void setSubmissionStatus(String submissionStatus) {
        try {
            if (submissionStatus != null) {
                this.submissionStatus = QuestionSubmission.Status.valueOf(submissionStatus).name();
            } else {
                this.submissionStatus = null;
            }
        } catch (IllegalArgumentException e) {
            throw new TutorException(INVALID_STATUS_FOR_QUESTION);
        }
    }

    public void remove() {
        this.questionSubmission = null;

        getUser().getReviews().remove(this);
        this.user = null;
    }
}
