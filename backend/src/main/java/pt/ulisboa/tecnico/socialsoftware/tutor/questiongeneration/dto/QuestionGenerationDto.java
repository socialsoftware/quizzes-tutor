package pt.ulisboa.tecnico.socialsoftware.tutor.questiongeneration.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.questiongeneration.domain.QuestionGeneration;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class QuestionGenerationDto implements Serializable {
    private Integer id;
    private Integer courseExecutionId;
    private Integer jobId;
    private QuestionDto question;
    private String status;
    private String modelId;
    private String promptVersion;
    private String groundingMode;
    private Integer verificationRetries;
    private boolean needsHumanAttention;
    private String explanation;
    private List<String> sourceChunkIds = new ArrayList<>();

    public QuestionGenerationDto() {
    }

    public QuestionGenerationDto(QuestionGeneration questionGeneration) {
        this.id = questionGeneration.getId();
        this.courseExecutionId = questionGeneration.getCourseExecution().getId();
        if (questionGeneration.getJob() != null)
            this.jobId = questionGeneration.getJob().getId();
        if (questionGeneration.getQuestion() != null)
            this.question = new QuestionDto(questionGeneration.getQuestion());
        this.status = questionGeneration.getStatus().name();
        this.modelId = questionGeneration.getModelId();
        this.promptVersion = questionGeneration.getPromptVersion();
        this.groundingMode = questionGeneration.getGroundingMode();
        this.verificationRetries = questionGeneration.getVerificationRetries();
        this.needsHumanAttention = questionGeneration.needsHumanAttention();
        this.explanation = questionGeneration.getExplanation();
        this.sourceChunkIds = new ArrayList<>(questionGeneration.getSourceChunkIds());
    }

    public Integer getId() { return id; }

    public void setId(Integer id) { this.id = id; }

    public Integer getCourseExecutionId() { return courseExecutionId; }

    public void setCourseExecutionId(Integer courseExecutionId) { this.courseExecutionId = courseExecutionId; }

    public Integer getJobId() { return jobId; }

    public void setJobId(Integer jobId) { this.jobId = jobId; }

    public QuestionDto getQuestion() { return question; }

    public void setQuestion(QuestionDto question) { this.question = question; }

    public String getStatus() { return status; }

    public void setStatus(String status) { this.status = status; }

    public String getModelId() { return modelId; }

    public void setModelId(String modelId) { this.modelId = modelId; }

    public String getPromptVersion() { return promptVersion; }

    public void setPromptVersion(String promptVersion) { this.promptVersion = promptVersion; }

    public String getGroundingMode() { return groundingMode; }

    public void setGroundingMode(String groundingMode) { this.groundingMode = groundingMode; }

    public Integer getVerificationRetries() { return verificationRetries; }

    public void setVerificationRetries(Integer verificationRetries) { this.verificationRetries = verificationRetries; }

    public boolean isNeedsHumanAttention() { return needsHumanAttention; }

    public void setNeedsHumanAttention(boolean needsHumanAttention) { this.needsHumanAttention = needsHumanAttention; }

    public String getExplanation() { return explanation; }

    public void setExplanation(String explanation) { this.explanation = explanation; }

    public List<String> getSourceChunkIds() { return sourceChunkIds; }

    public void setSourceChunkIds(List<String> sourceChunkIds) { this.sourceChunkIds = sourceChunkIds; }
}
