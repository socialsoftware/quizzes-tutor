package pt.ulisboa.tecnico.socialsoftware.tutor.question.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.AssessmentService;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Assessment;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.AssessmentDto;

import javax.validation.Valid;
import java.util.List;

@RestController
public class AssessmentController {
    private static Logger logger = LoggerFactory.getLogger(AssessmentController.class);

    private AssessmentService assessmentService;
    
    AssessmentController(AssessmentService assessmentService) {
        this.assessmentService = assessmentService;
    }

    @GetMapping("/executions/{executionId}/assessments")
    @PreAuthorize("hasRole('ROLE_TEACHER') and hasPermission(#executionId, 'EXECUTION.ACCESS')")
    public List<AssessmentDto> getExecutionCourseAssessments(@PathVariable int executionId){
        return this.assessmentService.findAssessments(executionId);
    }

    @GetMapping("/executions/{executionId}/assessments/available")
    @PreAuthorize("(hasRole('ROLE_TEACHER') and hasPermission(#executionId, 'EXECUTION.ACCESS')) " +
            "or (hasRole('ROLE_STUDENT') and hasPermission(#executionId, 'EXECUTION.ACCESS'))")
    public List<AssessmentDto> getAvailableAssessments(@PathVariable int executionId){
        return this.assessmentService.findAvailableAssessments(executionId);
    }

    @PostMapping("/executions/{executionId}/assessments")
    @PreAuthorize("hasRole('ROLE_TEACHER') and hasPermission(#executionId, 'EXECUTION.ACCESS')")
    public AssessmentDto createAssessment(@PathVariable int executionId, @Valid @RequestBody AssessmentDto assessment) {
        return this.assessmentService.createAssessment(executionId, assessment);
    }

    @PutMapping("/assessments/{assessmentId}")
    @PreAuthorize("hasRole('ROLE_TEACHER') and hasPermission(#assessmentId, 'ASSESSMENT.ACCESS')")
    public AssessmentDto updateAssessment(@PathVariable Integer assessmentId, @Valid @RequestBody AssessmentDto assessment) {
        return this.assessmentService.updateAssessment(assessmentId, assessment);
    }

    @DeleteMapping("/assessments/{assessmentId}")
    @PreAuthorize("hasRole('ROLE_TEACHER') and hasPermission(#assessmentId, 'ASSESSMENT.ACCESS')")
    public ResponseEntity removeAssessment(@PathVariable Integer assessmentId) {
        assessmentService.removeAssessment(assessmentId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/assessments/{assessmentId}/set-status")
    @PreAuthorize("hasRole('ROLE_TEACHER') and hasPermission(#assessmentId, 'ASSESSMENT.ACCESS')")
    public ResponseEntity assessmentSetStatus(@PathVariable Integer assessmentId, @Valid @RequestBody String status) {
        assessmentService.assessmentSetStatus(assessmentId, Assessment.Status.valueOf(status));
        return ResponseEntity.ok().build();
    }
}