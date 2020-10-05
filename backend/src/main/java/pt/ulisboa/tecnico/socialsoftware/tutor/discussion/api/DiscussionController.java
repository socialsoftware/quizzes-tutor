package pt.ulisboa.tecnico.socialsoftware.tutor.discussion.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pt.ulisboa.tecnico.socialsoftware.tutor.config.DateHandler;
import pt.ulisboa.tecnico.socialsoftware.tutor.discussion.DiscussionService;
import pt.ulisboa.tecnico.socialsoftware.tutor.discussion.dto.DiscussionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.discussion.dto.ReplyDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.discussion.dto.UnansweredDiscussionsDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.api.TopicController;
import org.springframework.security.core.Authentication;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.User;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class DiscussionController {
    private static final Logger logger = LoggerFactory.getLogger(TopicController.class);

    @Autowired
    private DiscussionService discussionService;

    @GetMapping("/discussions/{courseExecutionId}/user")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public List<DiscussionDto> getDiscussionsByUserId(Principal principal, @PathVariable int courseExecutionId, @Valid @RequestParam Integer userId) {
        User user = (User) ((Authentication) principal).getPrincipal();

        if(user == null){
            throw new TutorException(ErrorMessage.AUTHENTICATION_ERROR);
        }

        return this.discussionService.findByCourseExecutionIdAndUserId(courseExecutionId, userId);
    }

    @GetMapping(value = "/discussions/question")
    @PreAuthorize("hasRole('ROLE_TEACHER')")
    public List<DiscussionDto> getDiscussionsByQuestionId(Principal principal, @Valid @RequestParam Integer questionId) {
        User user = (User) ((Authentication) principal).getPrincipal();

        if(user == null){
            throw new TutorException(ErrorMessage.AUTHENTICATION_ERROR);
        }

        return discussionService.findDiscussionsByQuestionId(questionId);
    }

    @GetMapping("/{courseExecutionId}/discussions")
    @PreAuthorize("hasRole('ROLE_TEACHER')")
    public List<DiscussionDto> getCourseExecutionDiscussions(@PathVariable int courseExecutionId) {
        return this.discussionService.findDiscussionsByCourseExecutionId(courseExecutionId);
    }

    @GetMapping("/{courseExecutionId}/discussions/open")
    @PreAuthorize("hasRole('ROLE_TEACHER')")
    public List<DiscussionDto> getOpenCourseExecutionDiscussions(@PathVariable int courseExecutionId) {
        return this.discussionService.findOpenDiscussionsByCourseExecutionId(courseExecutionId);
    }

    @GetMapping("/{courseExecutionId}/discussions/unanswered")
    @PreAuthorize("hasRole('ROLE_TEACHER')")
    public UnansweredDiscussionsDto getUnansweredDiscussionsNumber(@PathVariable int courseExecutionId) {
        return this.discussionService.getUnansweredDiscussionsNumber(courseExecutionId);
    }

    @GetMapping("/discussions/{courseExecutionId}/answered")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public List<DiscussionDto> getAnsweredDiscussions(@PathVariable int courseExecutionId, @Valid @RequestParam Integer userId) {
        return this.discussionService.getAnsweredDiscussions(courseExecutionId, userId);
    }

    @GetMapping(value = "/discussions/question/available")
    @PreAuthorize("hasRole('ROLE_STUDENT') or hasRole('ROLE_TEACHER')")
    public List<DiscussionDto> getAvailableDiscussionsByQuestionId(Principal principal, @Valid @RequestParam Integer questionId) {
        User user = (User) ((Authentication) principal).getPrincipal();

        if(user == null){
            throw new TutorException(ErrorMessage.AUTHENTICATION_ERROR);
        }

        return discussionService.findDiscussionsByQuestionId(questionId).stream().filter(DiscussionDto::isAvailable).collect(Collectors.toList());
    }

    @PostMapping(value = "/discussions/{quizAnswerId}/{questionOrder}")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public DiscussionDto createDiscussion(Principal principal, @PathVariable int quizAnswerId, @PathVariable int questionOrder, @Valid @RequestBody DiscussionDto discussion){
        User user = (User) ((Authentication) principal).getPrincipal();

        if (user == null) {
            throw new TutorException(ErrorMessage.AUTHENTICATION_ERROR);
        }

        discussion.setUserId(user.getId());
        discussion.setDate(DateHandler.toISOString(DateHandler.now()));

        return discussionService.createDiscussion(quizAnswerId, questionOrder, discussion);
    }

    @PutMapping(value = "/discussions/status")
    @PreAuthorize("hasRole('ROLE_TEACHER') or hasRole('ROLE_STUDENT')")
    public DiscussionDto changeDiscussionStatus(Principal principal, @Valid @RequestParam int discussionId) {
        User user = (User) ((Authentication) principal).getPrincipal();

        if (user == null) {
            throw new TutorException(ErrorMessage.AUTHENTICATION_ERROR);
        }

        return discussionService.changeStatus(discussionId);
    }

    @PutMapping(value = "/discussions/reply/availability")
    @PreAuthorize("hasRole('ROLE_TEACHER')")
    public DiscussionDto changeReplyAvailability(Principal principal, @Valid @RequestParam int replyId) {
        User user = (User) ((Authentication) principal).getPrincipal();

        if (user == null) {
            throw new TutorException(ErrorMessage.AUTHENTICATION_ERROR);
        }

        return discussionService.changeReplyAvailability(replyId);
    }

    @PostMapping(value = "/discussions/replies")
    @PreAuthorize("hasRole('ROLE_TEACHER') or hasRole('ROLE_STUDENT')")
    public ReplyDto createReply(Principal principal, @Valid @RequestParam String message, @Valid @RequestBody DiscussionDto discussion){
        User user = (User) ((Authentication) principal).getPrincipal();

        if(user == null){
            throw new TutorException(ErrorMessage.AUTHENTICATION_ERROR);
        }

        ReplyDto reply = new ReplyDto();
        reply.setMessage(message);
        reply.setUserId(user.getId());
        reply.setUserName(user.getUsername());
        reply.setDate(DateHandler.toISOString(DateHandler.now()));
        reply.setAvailable(false);

        return discussionService.createReply(discussion, reply);
    }
}
