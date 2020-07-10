package pt.ulisboa.tecnico.socialsoftware.tutor.submission.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.config.DateHandler;
import pt.ulisboa.tecnico.socialsoftware.tutor.submission.domain.Review;

import java.io.Serializable;

public class ReviewDto implements Serializable {
    private Integer id;
    private Integer userId;
    private Integer submissionId;
    private String justification;
    private String status;
    private String creationDate;
    private String username;

    public ReviewDto() {}

    public ReviewDto(Review review){
        this.id = review.getId();
        this.userId = review.getUser().getId();
        this.submissionId = review.getSubmission().getId();
        this.justification = review.getJustification();
        this.status = review.getStatus();
        if (review.getCreationDate() != null)
            this.creationDate = DateHandler.toISOString(review.getCreationDate());
        this.username = review.getUser().getUsername();
    }

    public Integer getId() { return id; }

    public void setId(Integer id) { this.id = id; }

    public Integer getUserId() { return userId; }

    public void setUserId(Integer userId) { this.userId = userId; }

    public Integer getSubmissionId() { return submissionId; }

    public void setSubmissionId(Integer submissionId) { this.submissionId = submissionId; }

    public String getJustification() { return justification; }

    public void setJustification(String justification) { this.justification = justification; }

    public String getStatus() { return status; }

    public void setStatus(String status) { this.status = status; }

    public String getCreationDate() { return creationDate; }

    public void setCreationDate(String creationDate) { this.creationDate = creationDate; }

    public String getUsername() { return username; }

    public void setUsername(String username) { this.username = username; }
}
