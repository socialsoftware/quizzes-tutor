package pt.ulisboa.tecnico.socialsoftware.tutor.questionsubmission;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsubmission.dto.ReviewDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsubmission.dto.QuestionSubmissionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsubmission.dto.UserQuestionSubmissionInfoDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.AUTHENTICATION_ERROR;

@RestController
public class QuestionSubmissionController {
    @Autowired
    private QuestionSubmissionService submissionService;

    @PostMapping(value = "/student/submissions")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public QuestionSubmissionDto createQuestionSubmission(Principal principal, @Valid @RequestBody QuestionSubmissionDto submissionDto) {
        User user = (User) ((Authentication) principal).getPrincipal();

        if (user == null)
            throw new TutorException(AUTHENTICATION_ERROR);
        
        submissionDto.setUserId(user.getId());

        return submissionService.createQuestionSubmission(submissionDto);
    }

    @PostMapping("/executions/{executionId}/reviews")
    @PreAuthorize("(hasRole('ROLE_TEACHER') or hasRole('ROLE_STUDENT')) and hasPermission(#executionId, 'EXECUTION.ACCESS')")
    public ReviewDto createReview(Principal principal, @PathVariable int executionId, @Valid @RequestBody ReviewDto reviewDto) {
        User user = (User) ((Authentication) principal).getPrincipal();

        if (user == null)
            throw new TutorException(AUTHENTICATION_ERROR);

        reviewDto.setUserId(user.getId());
        return submissionService.createReview(reviewDto);
    }

    @PutMapping("/submissions/{submissionId}")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public QuestionSubmissionDto updateQuestionSubmission(Principal principal, @PathVariable Integer submissionId, @Valid @RequestBody QuestionSubmissionDto submissionDto) {
        User user = (User) ((Authentication) principal).getPrincipal();

        if (user == null)
            throw new TutorException(AUTHENTICATION_ERROR);

        submissionDto.setUserId(user.getId());
        return this.submissionService.updateQuestionSubmission(submissionId, submissionDto);
    }

    @PutMapping("/management/reviews/{questionId}")
    @PreAuthorize("hasRole('ROLE_TEACHER')")
    public void toggleInReviewStatus(@PathVariable int questionId, @Valid @RequestParam boolean inReview) {
        submissionService.toggleInReviewStatus(questionId, inReview);
    }

    @GetMapping(value = "/student/submissions")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public List<QuestionSubmissionDto> getStudentQuestionSubmissions(Principal principal, @Valid @RequestParam int executionId) {
        User user = (User) ((Authentication) principal).getPrincipal();

        if (user == null)
            throw new TutorException(AUTHENTICATION_ERROR);

        return submissionService.getStudentQuestionSubmissions(user.getId(), executionId);
    }

    @GetMapping("/executions/{executionId}/submissions")
    @PreAuthorize("(hasRole('ROLE_TEACHER') or hasRole('ROLE_STUDENT')) and hasPermission(#executionId, 'EXECUTION.ACCESS')")
    public List<QuestionSubmissionDto> getCourseExecutionQuestionSubmissions(@PathVariable int executionId) {
        return submissionService.getCourseExecutionQuestionSubmissions(executionId);
    }

    @GetMapping("/submissions/{submissionId}/reviews")
    @PreAuthorize("hasRole('ROLE_TEACHER') or hasRole('ROLE_STUDENT')")
    public List<ReviewDto> getQuestionSubmissionReviews(@PathVariable int submissionId) {
        return submissionService.getQuestionSubmissionReviews(submissionId);
    }

    @GetMapping(value = "/student/submissions/all")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public List<UserQuestionSubmissionInfoDto> getAllStudentsQuestionSubmissionsInfo(@Valid @RequestParam int executionId) {
        return submissionService.getAllStudentsQuestionSubmissionsInfo(executionId);
    }
}
