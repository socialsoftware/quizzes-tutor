package pt.ulisboa.tecnico.socialsoftware.tutor.questionsubmission;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.TopicDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsubmission.dto.ReviewDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsubmission.dto.QuestionSubmissionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsubmission.dto.UserQuestionSubmissionInfoDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;

import javax.validation.Valid;
import java.io.IOException;
import java.nio.file.Files;
import java.security.Principal;
import java.util.List;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.AUTHENTICATION_ERROR;

@RestController
public class QuestionSubmissionController {
    @Autowired
    private QuestionSubmissionService questionSubmissionService;

    @PostMapping(value = "/submissions/{executionId}")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#executionId, 'EXECUTION.ACCESS')")
    public QuestionSubmissionDto createQuestionSubmission(Principal principal, @PathVariable int executionId, @Valid @RequestBody QuestionSubmissionDto questionSubmissionDto) {
        User user = (User) ((Authentication) principal).getPrincipal();

        if (user == null)
            throw new TutorException(AUTHENTICATION_ERROR);
        
        return questionSubmissionService.createQuestionSubmission(questionSubmissionDto);
    }

    @PostMapping("/submissions/{executionId}/reviews")
    @PreAuthorize("(hasRole('ROLE_TEACHER') or (hasRole('ROLE_STUDENT') and hasPermission(#reviewDto.getQuestionSubmissionId(),'SUBMISSION.ACCESS'))) and hasPermission(#executionId, 'EXECUTION.ACCESS')")
    public ReviewDto createReview(Principal principal, @PathVariable int executionId, @Valid @RequestBody ReviewDto reviewDto) {
        User user = (User) ((Authentication) principal).getPrincipal();

        if (user == null)
            throw new TutorException(AUTHENTICATION_ERROR);

        return questionSubmissionService.createReview(reviewDto);
    }

    @PutMapping("/submissions/{executionId}")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#questionSubmissionDto.getId(),'SUBMISSION.ACCESS') and hasPermission(#executionId, 'EXECUTION.ACCESS')")
    public QuestionSubmissionDto updateQuestionSubmission(Principal principal, @PathVariable Integer executionId, @Valid @RequestBody QuestionSubmissionDto questionSubmissionDto) {
        User user = (User) ((Authentication) principal).getPrincipal();

        if (user == null)
            throw new TutorException(AUTHENTICATION_ERROR);

        questionSubmissionDto.setUserId(user.getId());
        return this.questionSubmissionService.updateQuestionSubmission(questionSubmissionDto.getId(), questionSubmissionDto);
    }

    @DeleteMapping("/submissions/{questionSubmissionId}")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#questionSubmissionId, 'SUBMISSION.ACCESS')")
    public void removeSubmittedQuestion(@PathVariable Integer questionSubmissionId) {
        questionSubmissionService.removeSubmittedQuestion(questionSubmissionId);
    }

    @PutMapping("/submissions/{questionSubmissionId}/topics")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#questionSubmissionId, 'SUBMISSION.ACCESS')")
    public void updateQuestionSubmissionTopics(@PathVariable Integer questionSubmissionId, @RequestBody TopicDto[] topics) {
        questionSubmissionService.updateQuestionSubmissionTopics(questionSubmissionId, topics);
    }

    @PutMapping("/submissions/{questionSubmissionId}/reviews")
    @PreAuthorize("hasRole('ROLE_TEACHER') and hasPermission(#executionId, 'EXECUTION.ACCESS')")
    public void toggleInReviewStatus(@PathVariable int questionSubmissionId, @Valid @RequestParam int executionId, @Valid @RequestParam boolean inReview) {
        questionSubmissionService.toggleInReviewStatus(questionSubmissionId, inReview);
    }

    @GetMapping(value = "/submissions/{executionId}/student")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#executionId, 'EXECUTION.ACCESS')")
    public List<QuestionSubmissionDto> getStudentQuestionSubmissions(Principal principal, @Valid @PathVariable int executionId) {
        User user = (User) ((Authentication) principal).getPrincipal();

        if (user == null)
            throw new TutorException(AUTHENTICATION_ERROR);

        return questionSubmissionService.getStudentQuestionSubmissions(user.getId(), executionId);
    }

    @GetMapping("/submissions/{executionId}/execution")
    @PreAuthorize("(hasRole('ROLE_TEACHER') or hasRole('ROLE_STUDENT')) and hasPermission(#executionId, 'EXECUTION.ACCESS')")
    public List<QuestionSubmissionDto> getCourseExecutionQuestionSubmissions(@Valid @PathVariable int executionId) {
        return questionSubmissionService.getCourseExecutionQuestionSubmissions(executionId);
    }

    @GetMapping("/submissions/{questionSubmissionId}/reviews")
    @PreAuthorize("(hasRole('ROLE_TEACHER') or (hasRole('ROLE_STUDENT') and hasPermission(#questionSubmissionId, 'SUBMISSION.ACCESS'))) and hasPermission(#executionId, 'EXECUTION.ACCESS')")
    public List<ReviewDto> getQuestionSubmissionReviews(@PathVariable int questionSubmissionId, @Valid @RequestParam int executionId) {
        return questionSubmissionService.getQuestionSubmissionReviews(questionSubmissionId);
    }

    @GetMapping(value = "/submissions/{executionId}/all")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#executionId, 'EXECUTION.ACCESS')")
    public List<UserQuestionSubmissionInfoDto> getAllStudentsQuestionSubmissionsInfo(@Valid @PathVariable int executionId) {
        return questionSubmissionService.getAllStudentsQuestionSubmissionsInfo(executionId);
    }
}
