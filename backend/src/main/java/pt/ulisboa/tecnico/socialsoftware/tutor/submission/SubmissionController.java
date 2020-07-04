package pt.ulisboa.tecnico.socialsoftware.tutor.submission;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.submission.dto.ReviewDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.submission.dto.SubmissionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.AUTHENTICATION_ERROR;

@RestController
public class SubmissionController {
    @Autowired
    private SubmissionService submissionService;

    @PostMapping(value = "/student/submissions")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public SubmissionDto createSubmission(Principal principal, @Valid @RequestBody SubmissionDto submissionDto) {
        User user = (User) ((Authentication) principal).getPrincipal();

        if (user == null)
            throw new TutorException(AUTHENTICATION_ERROR);

        return submissionService.createSubmission(submissionDto);
    }

    @PostMapping("/executions/{executionId}/reviews")
    @PreAuthorize("hasRole('ROLE_TEACHER') and hasPermission(#executionId, 'EXECUTION.ACCESS')")
    public ReviewDto createReview(@PathVariable int executionId, @Valid @RequestBody ReviewDto reviewDto) {
        return submissionService.createReview(reviewDto);
    }

    @PutMapping("/management/review/{questionId}")
    @PreAuthorize("hasRole('ROLE_TEACHER')")
    public void toggleInReviewStatus(@PathVariable int questionId, @Valid @RequestParam boolean inReview) {
        submissionService.toggleInReviewStatus(questionId, inReview);
    }

    @GetMapping(value = "/student/submissions")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public List<SubmissionDto> getStudentSubmissions(Principal principal, @Valid @RequestParam int executionId) {
        User user = (User) ((Authentication) principal).getPrincipal();

        if (user == null)
            throw new TutorException(AUTHENTICATION_ERROR);

        return submissionService.getStudentSubmissions(user.getId(), executionId);
    }

    @GetMapping("/executions/{executionId}/submissions")
    @PreAuthorize("hasRole('ROLE_TEACHER') and hasPermission(#executionId, 'EXECUTION.ACCESS')")
    public List<SubmissionDto> getCourseExecutionSubmissions(@PathVariable int executionId) {
        return submissionService.getCourseExecutionSubmissions(executionId);
    }

    @GetMapping("/submission/{submissionId}/reviews")
    @PreAuthorize("hasRole('ROLE_TEACHER') or hasRole('ROLE_STUDENT')")
    public List<ReviewDto> getSubmissionReviews(@PathVariable int submissionId) {
        return submissionService.getSubmissionReviews(submissionId);
    }

    @GetMapping(value = "/student/submissions/all")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public List<SubmissionDto> getAllStudentSubmissions(@Valid @RequestParam int executionId) {
        return submissionService.getAllStudentsSubmissions(executionId);
    }
}
