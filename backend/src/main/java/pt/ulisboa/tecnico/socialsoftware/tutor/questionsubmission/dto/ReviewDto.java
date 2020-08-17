package pt.ulisboa.tecnico.socialsoftware.tutor.questionsubmission.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.config.DateHandler;
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsubmission.domain.Review;

import java.io.Serializable;

public class ReviewDto implements Serializable {
    private Integer id;
    private Integer userId;
    private Integer questionSubmissionId;
    private String comment;
    private String status;
    private String creationDate;
    private String name;
    private String username;

    public ReviewDto() {}

    public ReviewDto(Review review){
        this.id = review.getId();
        this.userId = review.getUser().getId();
        this.questionSubmissionId = review.getQuestionSubmission().getId();
        this.comment = review.getComment();
        this.status = review.getStatus().name();
        if (review.getCreationDate() != null)
            this.creationDate = DateHandler.toISOString(review.getCreationDate());
        this.name = review.getUser().getName();
        this.username = review.getUser().getUsername();
    }

    public Integer getId() { return id; }

    public void setId(Integer id) { this.id = id; }

    public Integer getUserId() { return userId; }

    public void setUserId(Integer userId) { this.userId = userId; }

    public Integer getQuestionSubmissionId() { return questionSubmissionId; }

    public void setQuestionSubmissionId(Integer questionSubmissionId) { this.questionSubmissionId = questionSubmissionId; }

    public String getComment() { return comment; }

    public void setComment(String comment) { this.comment = comment; }

    public String getStatus() { return status; }

    public void setStatus(String status) { this.status = status; }

    public String getCreationDate() { return creationDate; }

    public void setCreationDate(String creationDate) { this.creationDate = creationDate; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public String getUsername() { return username; }

    public void setUsername(String username) { this.username = username; }
}
