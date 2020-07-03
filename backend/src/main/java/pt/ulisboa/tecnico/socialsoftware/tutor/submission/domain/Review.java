package pt.ulisboa.tecnico.socialsoftware.tutor.submission.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.config.DateHandler;
import pt.ulisboa.tecnico.socialsoftware.tutor.submission.dto.ReviewDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "reviews")

public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String justification;

    @Column(columnDefinition = "TEXT")
    private String status;

    @Column(name = "creation_date")
    private LocalDateTime creationDate;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "submission_id")
    private Submission submission;

    public Review() {
    }

    public Review(User user, Submission submission, ReviewDto reviewDto) {
        setJustification(reviewDto.getJustification());
        setUser(user);
        setSubmission(submission);
        setStatus(reviewDto.getStatus());
        setCreationDate(DateHandler.toLocalDateTime(reviewDto.getCreationDate()));
    }

    @Override
    public String toString() {
        return "Review{" + "id=" + id + "', user=" + user + ", justification='" + justification + ", submission=" + submission.getQuestion() + "}";
    }

    public Integer getId() { return id; }

    public String getJustification() { return justification; }

    public void setJustification(String justification) { this.justification = justification; }

    public LocalDateTime getCreationDate() { return creationDate; }

    public void setCreationDate(LocalDateTime creationDate) {
        if (this.creationDate == null) this.creationDate = DateHandler.now();
        else this.creationDate = creationDate;
    }

    public User getUser() { return user; }

    public void setUser(User user) { this.user = user; }

    public Submission getSubmission() { return submission; }

    public void setSubmission(Submission submission) { this.submission = submission; }

    public String getStatus() { return status; }

    public void setStatus(String status) { this.status = status; }
}
