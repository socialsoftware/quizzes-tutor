package pt.ulisboa.tecnico.socialsoftware.tutor.questiongeneration.domain;

import jakarta.persistence.*;
import pt.ulisboa.tecnico.socialsoftware.tutor.utils.DateHandler;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * A request sent to the question generation service. It keeps plain ids instead of entity
 * references: the service owns the job, this row only remembers how to fetch its result
 * and that the result has been imported once.
 */
@Entity
@Table(name = "generation_jobs")
public class GenerationJob {
    public enum Status {
        REQUESTED, IMPORTED, FAILED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "aqg_job_id", nullable = false, unique = true)
    private String aqgJobId;

    @Column(name = "course_execution_id", nullable = false)
    private Integer courseExecutionId;

    @Column(name = "requester_id")
    private Integer requesterId;

    private String topic;

    // The first topic asked for: what jobs kept before they could have several (see getTopicIds)
    @Column(name = "topic_id")
    private Integer topicId;

    @ElementCollection
    @CollectionTable(name = "generation_job_topics", joinColumns = @JoinColumn(name = "job_id"))
    @Column(name = "topic_id")
    private List<Integer> topicIds = new ArrayList<>();

    @Column(name = "requested_count")
    private Integer requestedCount;

    private String difficulty;

    @Column(name = "grounding_mode")
    private String groundingMode;

    @Column(name = "aqg_status")
    private String aqgStatus;

    @Enumerated(EnumType.STRING)
    private Status status = Status.REQUESTED;

    @Column(name = "imported_count")
    private Integer importedCount = 0;

    @Column(name = "skipped_count")
    private Integer skippedCount = 0;

    @Column(columnDefinition = "TEXT")
    private String error;

    @ElementCollection
    @CollectionTable(name = "generation_job_materials", joinColumns = @JoinColumn(name = "job_id"))
    @Column(name = "material_id")
    private List<String> materialIds = new ArrayList<>();

    private String language;

    @Column(columnDefinition = "TEXT")
    private String focus;

    // The QuestionGeneration this job rewrites ("Regenerate with review"), null for new questions
    @Column(name = "revision_of_id")
    private Integer revisionOfId;

    @Column(name = "creation_date")
    private LocalDateTime creationDate = DateHandler.now();

    public GenerationJob() {
    }

    /** `topic` is the name of the topic, or the names of the topics, shown in the list of requests. */
    public GenerationJob(String aqgJobId, Integer courseExecutionId, Integer requesterId, String topic, List<Integer> topicIds,
                         Integer requestedCount, String difficulty, String groundingMode, String aqgStatus) {
        this.aqgJobId = aqgJobId;
        this.courseExecutionId = courseExecutionId;
        this.requesterId = requesterId;
        this.topic = topic;
        this.topicIds = new ArrayList<>(topicIds);
        this.topicId = topicIds.isEmpty() ? null : topicIds.get(0);
        this.requestedCount = requestedCount;
        this.difficulty = difficulty;
        this.groundingMode = groundingMode;
        this.aqgStatus = aqgStatus;
    }

    /** The topics the questions are about; jobs from before there could be several only have the first one. */
    public List<Integer> getTopicIds() {
        if (!topicIds.isEmpty())
            return topicIds;
        return topicId == null ? List.of() : List.of(topicId);
    }

    public void setSource(List<String> materialIds, String language) {
        this.materialIds = new ArrayList<>(materialIds);
        this.language = language;
    }

    public void setFocus(String focus) {
        this.focus = focus;
    }

    public String getFocus() {
        return focus;
    }

    public void setRevisionOfId(Integer revisionOfId) {
        this.revisionOfId = revisionOfId;
    }

    public List<String> getMaterialIds() {
        return materialIds;
    }

    public String getLanguage() {
        return language;
    }

    public Integer getRevisionOfId() {
        return revisionOfId;
    }

    public void markImported(int imported, int skipped) {
        this.status = Status.IMPORTED;
        this.importedCount = imported;
        this.skippedCount = skipped;
    }

    public void markFailed(String error) {
        this.status = Status.FAILED;
        this.error = error;
    }

    public Integer getId() {
        return id;
    }

    public String getAqgJobId() {
        return aqgJobId;
    }

    public Integer getCourseExecutionId() {
        return courseExecutionId;
    }

    public Integer getRequesterId() {
        return requesterId;
    }

    public String getTopic() {
        return topic;
    }

    public Integer getTopicId() {
        return topicId;
    }

    public Integer getRequestedCount() {
        return requestedCount;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public String getGroundingMode() {
        return groundingMode;
    }

    public String getAqgStatus() {
        return aqgStatus;
    }

    public void setAqgStatus(String aqgStatus) {
        this.aqgStatus = aqgStatus;
    }

    public Status getStatus() {
        return status;
    }

    public Integer getImportedCount() {
        return importedCount;
    }

    public Integer getSkippedCount() {
        return skippedCount;
    }

    public String getError() {
        return error;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }
}
