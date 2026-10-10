package pt.ulisboa.tecnico.socialsoftware.tutor.questiongeneration;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.domain.AuthUser;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.TopicNodeDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.TopicTreeDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.questiongeneration.dto.GenerationJobDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.questiongeneration.dto.GenerationMaterialDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.questiongeneration.dto.GenerationRequestDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.questiongeneration.dto.MaterialChunkDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.questiongeneration.dto.QuestionGenerationDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsubmission.dto.ReviewDto;

import java.io.IOException;
import java.util.List;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.AQG_SERVICE_ERROR;

/**
 * Teacher-only endpoints. The browser only ever talks to the Tutor: the generation service
 * is reachable on the internal network alone, so authentication stays in one place.
 */
@RestController
public class QuestionGenerationController {
    @Autowired
    private QuestionGenerationService questionGenerationService;

    @PostMapping(value = "/generation/{executionId}/materials", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ROLE_TEACHER') and hasPermission(#executionId, 'EXECUTION.ACCESS')")
    public GenerationMaterialDto uploadMaterial(@PathVariable int executionId, @RequestParam("file") MultipartFile file) {
        try {
            String filename = file.getOriginalFilename() == null ? "material" : file.getOriginalFilename();
            return questionGenerationService.uploadMaterial(executionId, filename, file.getBytes());
        } catch (IOException e) {
            throw new TutorException(AQG_SERVICE_ERROR, "the file could not be read");
        }
    }

    @GetMapping("/generation/{executionId}/materials")
    @PreAuthorize("hasRole('ROLE_TEACHER') and hasPermission(#executionId, 'EXECUTION.ACCESS')")
    public List<GenerationMaterialDto> getMaterials(@PathVariable int executionId) {
        return questionGenerationService.getMaterials(executionId);
    }

    @GetMapping("/generation/{executionId}/materials/{materialId}/chunks")
    @PreAuthorize("hasRole('ROLE_TEACHER') and hasPermission(#executionId, 'EXECUTION.ACCESS')")
    public List<MaterialChunkDto> getMaterialChunks(@PathVariable int executionId, @PathVariable String materialId) {
        return questionGenerationService.getMaterialChunks(executionId, materialId);
    }

    @PutMapping("/generation/{executionId}/materials/{materialId}/distribution")
    @PreAuthorize("hasRole('ROLE_TEACHER') and hasPermission(#executionId, 'EXECUTION.ACCESS')")
    public List<TopicNodeDto> distributeMaterial(@PathVariable int executionId, @PathVariable String materialId,
                                                 @RequestBody TopicTreeDto tree) {
        return questionGenerationService.distributeMaterial(executionId, materialId, tree);
    }

    @PostMapping("/generation/{executionId}/materials/{materialId}/reprocess")
    @PreAuthorize("hasRole('ROLE_TEACHER') and hasPermission(#executionId, 'EXECUTION.ACCESS')")
    public GenerationMaterialDto reprocessMaterial(@PathVariable int executionId, @PathVariable String materialId) {
        return questionGenerationService.reprocessMaterial(executionId, materialId);
    }

    @PostMapping("/generation/{executionId}/jobs")
    @PreAuthorize("hasRole('ROLE_TEACHER') and hasPermission(#executionId, 'EXECUTION.ACCESS')")
    public GenerationJobDto requestGeneration(Authentication authentication, @PathVariable int executionId,
                                              @Valid @RequestBody GenerationRequestDto request) {
        AuthUser authUser = (AuthUser) authentication.getPrincipal();

        return questionGenerationService.requestGeneration(executionId, authUser.getUser().getId(), request);
    }

    @GetMapping("/generation/{executionId}/jobs")
    @PreAuthorize("hasRole('ROLE_TEACHER') and hasPermission(#executionId, 'EXECUTION.ACCESS')")
    public List<GenerationJobDto> getGenerationJobs(@PathVariable int executionId) {
        return questionGenerationService.getGenerationJobs(executionId);
    }

    @GetMapping("/generation/{executionId}/jobs/{jobId}")
    @PreAuthorize("hasRole('ROLE_TEACHER') and hasPermission(#executionId, 'EXECUTION.ACCESS')")
    public GenerationJobDto getGenerationJob(@PathVariable int executionId, @PathVariable int jobId) {
        return questionGenerationService.getGenerationJob(executionId, jobId);
    }

    @GetMapping("/generation/{executionId}/questions")
    @PreAuthorize("hasRole('ROLE_TEACHER') and hasPermission(#executionId, 'EXECUTION.ACCESS')")
    public List<QuestionGenerationDto> getQuestionGenerations(@PathVariable int executionId) {
        return questionGenerationService.getCourseExecutionQuestionGenerations(executionId);
    }

    @PostMapping("/generation/questions/{questionGenerationId}/reviews")
    @PreAuthorize("hasRole('ROLE_TEACHER') and hasPermission(#questionGenerationId, 'GENERATION.ACCESS')")
    public ReviewDto createReview(Authentication authentication, @PathVariable int questionGenerationId,
                                  @Valid @RequestBody ReviewDto reviewDto) {
        AuthUser authUser = (AuthUser) authentication.getPrincipal();

        // The reviewer is whoever is logged in, never what the request body claims
        reviewDto.setUserId(authUser.getUser().getId());
        reviewDto.setQuestionGenerationId(questionGenerationId);
        return questionGenerationService.createReview(reviewDto);
    }

    /** "Regenerate with review": a REQUEST_CHANGES review whose comment the model rewrites the question from. */
    @PostMapping("/generation/questions/{questionGenerationId}/regenerate")
    @PreAuthorize("hasRole('ROLE_TEACHER') and hasPermission(#questionGenerationId, 'GENERATION.ACCESS')")
    public QuestionGenerationDto regenerate(Authentication authentication, @PathVariable int questionGenerationId,
                                            @RequestBody ReviewDto reviewDto) {
        AuthUser authUser = (AuthUser) authentication.getPrincipal();

        return questionGenerationService.regenerate(questionGenerationId, authUser.getUser().getId(),
                reviewDto.getComment());
    }

    @GetMapping("/generation/questions/{questionGenerationId}/reviews")
    @PreAuthorize("hasRole('ROLE_TEACHER') and hasPermission(#questionGenerationId, 'GENERATION.ACCESS')")
    public List<ReviewDto> getReviews(@PathVariable int questionGenerationId) {
        return questionGenerationService.getQuestionGenerationReviews(questionGenerationId);
    }
}
