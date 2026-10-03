package pt.ulisboa.tecnico.socialsoftware.tutor.questionsubmission.domain;

import jakarta.persistence.*;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.questiongeneration.domain.QuestionGeneration;
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsubmission.dto.ReviewDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.User;
import pt.ulisboa.tecnico.socialsoftware.tutor.utils.DateHandler;

import java.time.LocalDateTime;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.INVALID_TYPE_FOR_REVIEW;
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.REVIEW_MISSING_COMMENT;

@Entity
@Table(name = "reviews")
public class Review {
    public enum Type {
        APPROVE, REJECT, REQUEST_CHANGES, REQUEST_REVIEW, COMMENT
    }

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

    @ManyToOne
    @JoinColumn(name = "question_generation_id")
    private QuestionGeneration questionGeneration;

    @Enumerated(EnumType.STRING)
    private Review.Type type;

    public Review() {
    }

    public Review(User user, QuestionGeneration questionGeneration, ReviewDto reviewDto) {
        // The type first: whether a comment is required depends on it
        setType(reviewDto.getType());
        setComment(reviewDto.getComment());
        setUser(user);
        setQuestionGeneration(questionGeneration);
        setCreationDate(DateHandler.toLocalDateTime(reviewDto.getCreationDate()));
    }

    public Review(User user, QuestionSubmission questionSubmission, ReviewDto reviewDto) {
        // The type first: whether a comment is required depends on it
        setType(reviewDto.getType());
        setComment(reviewDto.getComment());
        setUser(user);
        setQuestionSubmission(questionSubmission);
        setCreationDate(DateHandler.toLocalDateTime(reviewDto.getCreationDate()));
    }

    @Override
    public String toString() {
        return "Review{" + "id=" + id + "', user=" + user + ", comment='" + comment + ", type='" + type.name() + ", questionSubmission=" + (questionSubmission == null ? null : questionSubmission.getQuestion()) + "}";
    }

    public Integer getId() {
        return id;
    }

    public String getComment() {
        return comment;
    }

    /** Approving or rejecting may go without a comment; asking for changes or commenting may not. */
    public void setComment(String comment) {
        if (comment == null || comment.isBlank()) {
            if (type != Type.APPROVE && type != Type.REJECT) {
                throw new TutorException(REVIEW_MISSING_COMMENT);
            }
            this.comment = "";
            return;
        }
        this.comment = comment;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        if (this.creationDate == null) {
            this.creationDate = DateHandler.now();
        } else {
            this.creationDate = creationDate;
        }
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public QuestionSubmission getQuestionSubmission() {
        return questionSubmission;
    }

    public void setQuestionSubmission(QuestionSubmission questionSubmission) {
        this.questionSubmission = questionSubmission;
    }

    public QuestionGeneration getQuestionGeneration() {
        return questionGeneration;
    }

    public void setQuestionGeneration(QuestionGeneration questionGeneration) {
        this.questionGeneration = questionGeneration;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public void setType(String type) {
        if (type == null || type.isBlank()) {
            throw new TutorException(INVALID_TYPE_FOR_REVIEW);
        }
        try {
            this.type = Review.Type.valueOf(type);
        } catch (IllegalArgumentException e) {
            throw new TutorException(INVALID_TYPE_FOR_REVIEW);
        }
    }

    public void remove() {
        user.getReviews().remove(this);
        user = null;
    }
}
