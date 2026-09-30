package pt.ulisboa.tecnico.socialsoftware.tutor.questiongeneration.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.questiongeneration.domain.GenerationJob;
import pt.ulisboa.tecnico.socialsoftware.tutor.utils.DateHandler;

import java.io.Serializable;

public class GenerationJobDto implements Serializable {
    private Integer id;
    private Integer courseExecutionId;
    private String topic;
    private Integer topicId;
    private Integer requestedCount;
    private String difficulty;
    private String groundingMode;
    private String status;
    private String generationStatus;
    private Integer importedCount;
    private Integer skippedCount;
    private String error;
    private String creationDate;

    public GenerationJobDto() {
    }

    public GenerationJobDto(GenerationJob job) {
        this.id = job.getId();
        this.courseExecutionId = job.getCourseExecutionId();
        this.topic = job.getTopic();
        this.topicId = job.getTopicId();
        this.requestedCount = job.getRequestedCount();
        this.difficulty = job.getDifficulty();
        this.groundingMode = job.getGroundingMode();
        this.status = job.getStatus().name();
        this.generationStatus = job.getAqgStatus();
        this.importedCount = job.getImportedCount();
        this.skippedCount = job.getSkippedCount();
        this.error = job.getError();
        if (job.getCreationDate() != null)
            this.creationDate = DateHandler.toISOString(job.getCreationDate());
    }

    public Integer getId() { return id; }

    public void setId(Integer id) { this.id = id; }

    public Integer getCourseExecutionId() { return courseExecutionId; }

    public void setCourseExecutionId(Integer courseExecutionId) { this.courseExecutionId = courseExecutionId; }

    public String getTopic() { return topic; }

    public void setTopic(String topic) { this.topic = topic; }

    public Integer getTopicId() { return topicId; }

    public void setTopicId(Integer topicId) { this.topicId = topicId; }

    public Integer getRequestedCount() { return requestedCount; }

    public void setRequestedCount(Integer requestedCount) { this.requestedCount = requestedCount; }

    public String getDifficulty() { return difficulty; }

    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }

    public String getGroundingMode() { return groundingMode; }

    public void setGroundingMode(String groundingMode) { this.groundingMode = groundingMode; }

    public String getStatus() { return status; }

    public void setStatus(String status) { this.status = status; }

    public String getGenerationStatus() { return generationStatus; }

    public void setGenerationStatus(String generationStatus) { this.generationStatus = generationStatus; }

    public Integer getImportedCount() { return importedCount; }

    public void setImportedCount(Integer importedCount) { this.importedCount = importedCount; }

    public Integer getSkippedCount() { return skippedCount; }

    public void setSkippedCount(Integer skippedCount) { this.skippedCount = skippedCount; }

    public String getError() { return error; }

    public void setError(String error) { this.error = error; }

    public String getCreationDate() { return creationDate; }

    public void setCreationDate(String creationDate) { this.creationDate = creationDate; }
}
