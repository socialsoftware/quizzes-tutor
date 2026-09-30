package pt.ulisboa.tecnico.socialsoftware.tutor.questiongeneration.domain;

import jakarta.persistence.*;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.CourseExecution;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsubmission.domain.Review;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.INVALID_TYPE_FOR_REVIEW;

/**
 * A question drafted by the generation service, waiting for a teacher. It mirrors
 * QuestionSubmission: the Question stays SUBMITTED (hidden from quizzes) until a review approves it.
 */
@Entity
@Table(name = "question_generations")
public class QuestionGeneration {
    public enum Status {
        IN_REVIEW, IN_REVISION, APPROVED, REJECTED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name = "question_id")
    private Question question;

    @ManyToOne
    @JoinColumn(name = "course_execution_id")
    private CourseExecution courseExecution;

    @ManyToOne
    @JoinColumn(name = "job_id")
    private GenerationJob job;

    @Enumerated(EnumType.STRING)
    private Status status = Status.IN_REVIEW;

    @Column(name = "model_id")
    private String modelId;

    @Column(name = "prompt_version")
    private String promptVersion;

    @Column(name = "grounding_mode")
    private String groundingMode;

    @Column(name = "verification_retries")
    private Integer verificationRetries = 0;

    @Column(name = "needs_human_attention")
    private boolean needsHumanAttention;

    @Column(columnDefinition = "TEXT")
    private String explanation;

    @ElementCollection
    @CollectionTable(name = "question_generation_chunks", joinColumns = @JoinColumn(name = "question_generation_id"))
    @Column(name = "chunk_id")
    private List<String> sourceChunkIds = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "questionGeneration", fetch = FetchType.LAZY, orphanRemoval = true)
    private Set<Review> reviews = new HashSet<>();

    public QuestionGeneration() {
    }

    public QuestionGeneration(CourseExecution courseExecution, Question question, GenerationJob job) {
        setCourseExecution(courseExecution);
        setQuestion(question);
        this.job = job;
        courseExecution.addQuestionGeneration(this);
    }

    public Integer getId() {
        return id;
    }

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    public CourseExecution getCourseExecution() {
        return courseExecution;
    }

    public void setCourseExecution(CourseExecution courseExecution) {
        this.courseExecution = courseExecution;
    }

    public GenerationJob getJob() {
        return job;
    }

    public Status getStatus() {
        return status;
    }

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

    public String getModelId() {
        return modelId;
    }

    public String getPromptVersion() {
        return promptVersion;
    }

    public String getGroundingMode() {
        return groundingMode;
    }

    public Integer getVerificationRetries() {
        return verificationRetries;
    }

    public boolean needsHumanAttention() {
        return needsHumanAttention;
    }

    public String getExplanation() {
        return explanation;
    }

    public List<String> getSourceChunkIds() {
        return sourceChunkIds;
    }

    public void setGenerationDetails(String modelId, String promptVersion, String groundingMode, int verificationRetries,
                                     boolean needsHumanAttention, String explanation, List<String> sourceChunkIds) {
        this.modelId = modelId;
        this.promptVersion = promptVersion;
        this.groundingMode = groundingMode;
        this.verificationRetries = verificationRetries;
        this.needsHumanAttention = needsHumanAttention;
        this.explanation = explanation;
        this.sourceChunkIds = new ArrayList<>(sourceChunkIds);
    }

    public Set<Review> getReviews() {
        return reviews;
    }

    public void addReview(Review review) {
        this.reviews.add(review);
    }

    public void remove() {
        getCourseExecution().getQuestionGenerations().remove(this);
        this.courseExecution = null;

        question.remove();

        new ArrayList<>(reviews).forEach(Review::remove);
    }
}
