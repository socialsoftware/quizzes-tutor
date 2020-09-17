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
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;

@RestController
public class DiscussionController {
    private static final Logger logger = LoggerFactory.getLogger(TopicController.class);

    @Autowired
    private DiscussionService discussionService;

    @GetMapping("/discussions")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public List<DiscussionDto> getDiscussions(Principal principal, @Valid @RequestParam Integer userId) {
        User user = (User)((Authentication) principal).getPrincipal();

        if (user == null){
            throw new TutorException(ErrorMessage.AUTHENTICATION_ERROR);
        }else if(!user.getId().equals(userId)){
            throw new TutorException(ErrorMessage.DISCUSSION_NOT_SUBMITTED_BY_REQUESTER, user.getId());
        }

        return discussionService.findDiscussionsByUserId(userId);
    }

    @GetMapping("/{courseExecutionId}/discussions")
    @PreAuthorize("hasRole('ROLE_TEACHER')")
    public List<DiscussionDto> getCourseExecutionDiscussions(@PathVariable int courseExecutionId) {
        return this.discussionService.findDiscussionsByCourseExecutionId(courseExecutionId);
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

    @GetMapping(value = "/discussions/question")
    @PreAuthorize("hasRole('ROLE_TEACHER')")
    public List<DiscussionDto> getDiscussionsByQuestions(Principal principal, @Valid @RequestParam Integer questionId) {
        User user = (User) ((Authentication) principal).getPrincipal();

        if(user == null){
            throw new TutorException(ErrorMessage.AUTHENTICATION_ERROR);
        }

        return discussionService.findDiscussionsByQuestionId(questionId);
    }

    @PostMapping(value = "/discussions")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public DiscussionDto createDiscussion(Principal principal, @Valid @RequestBody DiscussionDto discussion){
        User user = (User) ((Authentication) principal).getPrincipal();

        if (user == null) {
            throw new TutorException(ErrorMessage.AUTHENTICATION_ERROR);
        }

        discussion.setUserId(user.getId());
        discussion.setDate(DateHandler.toISOString(DateHandler.now()));

        return discussionService.createDiscussion(discussion);
    }

    @PutMapping(value = "/discussions/availability")
    @PreAuthorize("hasRole('ROLE_TEACHER')")
    public DiscussionDto changeAvailability(Principal principal, @Valid @RequestParam int discussionId) {
        User user = (User) ((Authentication) principal).getPrincipal();

        if (user == null) {
            throw new TutorException(ErrorMessage.AUTHENTICATION_ERROR);
        }

        return discussionService.changeAvailability(discussionId);
    }

    @PostMapping(value = "/discussions/replies")
    @PreAuthorize("hasRole('ROLE_TEACHER') or hasRole('ROLE_STUDENT')")
    public ReplyDto createReply(Principal principal, @Valid @RequestParam String message, @Valid @RequestBody DiscussionDto discussion){
        User user = (User) ((Authentication) principal).getPrincipal();

        ReplyDto reply = new ReplyDto();
        reply.setMessage(message);

        if(user == null){
            throw new TutorException(ErrorMessage.AUTHENTICATION_ERROR);
        }

        reply.setUserId(user.getId());
        reply.setDate(DateHandler.now());

        return discussionService.createReply(discussion, reply);
    }
}
