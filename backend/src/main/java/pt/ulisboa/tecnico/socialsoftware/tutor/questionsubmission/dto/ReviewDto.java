package pt.ulisboa.tecnico.socialsoftware.tutor.questionsubmission.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.utils.DateHandler;
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsubmission.domain.Review;

import java.io.Serializable;

public class ReviewDto implements Serializable {
    private Integer id;
    private Integer userId;
    private Integer questionSubmissionId;
    private String comment;
    private String creationDate;
    private String name;
    private String username;
    private String type;

    public ReviewDto() {}

    public ReviewDto(Review review){
        setId(review.getId());
        setUserId(review.getUser().getId());
        setQuestionSubmissionId(review.getQuestionSubmission().getId());
        setComment(review.getComment());
        if (review.getCreationDate() != null)
            setCreationDate(DateHandler.toISOString(review.getCreationDate()));
        setName(review.getUser().getName());
        setUsername(review.getUser().getUsername());
        setType(review.getType().name());
    }

    public Integer getId() { return id; }

    public void setId(Integer id) { this.id = id; }

    public Integer getUserId() { return userId; }

    public void setUserId(Integer userId) { this.userId = userId; }

    public Integer getQuestionSubmissionId() { return questionSubmissionId; }

    public void setQuestionSubmissionId(Integer questionSubmissionId) { this.questionSubmissionId = questionSubmissionId; }

    public String getComment() { return comment; }

    public void setComment(String comment) { this.comment = comment; }

    public String getCreationDate() { return creationDate; }

    public void setCreationDate(String creationDate) { this.creationDate = creationDate; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public String getUsername() { return username; }

    public void setUsername(String username) { this.username = username; }

    public String getType() { return type; }

    public void setType(String type) { this.type = type; }
}
