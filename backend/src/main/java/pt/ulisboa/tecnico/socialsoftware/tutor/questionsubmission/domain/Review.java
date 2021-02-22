package pt.ulisboa.tecnico.socialsoftware.tutor.questionsubmission.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.utils.DateHandler;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsubmission.dto.ReviewDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.User;

import javax.persistence.*;
import java.time.LocalDateTime;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*;

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

    @Enumerated(EnumType.STRING)
    private Review.Type type;

    public Review() {
    }

    public Review(User user, QuestionSubmission questionSubmission, ReviewDto reviewDto) {
        setComment(reviewDto.getComment());
        setUser(user);
        setQuestionSubmission(questionSubmission);
        setCreationDate(DateHandler.toLocalDateTime(reviewDto.getCreationDate()));
        setType(reviewDto.getType());
    }

    @Override
    public String toString() {
        return "Review{" + "id=" + id + "', user=" + user + ", comment='" + comment + ", type='" + type.name() + ", questionSubmission=" + questionSubmission.getQuestion() + "}";
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

    public Type getType() { return type; }

    public void setType(Type type) { this.type = type; }

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
