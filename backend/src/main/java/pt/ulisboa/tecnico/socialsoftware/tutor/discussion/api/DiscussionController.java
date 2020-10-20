package pt.ulisboa.tecnico.socialsoftware.tutor.discussion.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pt.ulisboa.tecnico.socialsoftware.tutor.discussion.DiscussionService;
import pt.ulisboa.tecnico.socialsoftware.tutor.discussion.dto.DiscussionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.discussion.dto.ReplyDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.api.TopicController;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.User;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;

@RestController
public class DiscussionController {
    private static final Logger logger = LoggerFactory.getLogger(TopicController.class);

    @Autowired
    private DiscussionService discussionService;

    @GetMapping("/discussions/{courseExecutionId}/user")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public List<DiscussionDto> getDiscussionsByUserId(Principal principal, @PathVariable int courseExecutionId) {
        User user = (User) ((Authentication) principal).getPrincipal();
        return this.discussionService.findByCourseExecutionIdAndUserId(courseExecutionId, user.getId());
    }

    @GetMapping("/discussions/{courseExecutionId}")
    @PreAuthorize("hasRole('ROLE_TEACHER')")
    public List<DiscussionDto> getCourseExecutionDiscussions(@PathVariable int courseExecutionId) {
        return this.discussionService.findDiscussionsByCourseExecutionId(courseExecutionId);
    }

    @GetMapping("/discussions/open/{courseExecutionId}")
    @PreAuthorize("hasRole('ROLE_TEACHER')")
    public List<DiscussionDto> getOpenCourseExecutionDiscussions(@PathVariable int courseExecutionId) {
        return this.discussionService.findOpenDiscussionsByCourseExecutionId(courseExecutionId);
    }

    @GetMapping(value = "/discussions/clarifications")
    @PreAuthorize("hasRole('ROLE_STUDENT') or hasRole('ROLE_TEACHER')")
    public List<ReplyDto> getClarificationsByQuestionId(@Valid @RequestParam Integer questionId) {
        return discussionService.findClarificationsByQuestionId(questionId);
    }

    @PostMapping(value = "/discussions/create/{questionAnswerId}")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public DiscussionDto createDiscussion(Principal principal, @PathVariable int questionAnswerId, @Valid @RequestBody DiscussionDto discussion){
        User user = (User) ((Authentication) principal).getPrincipal();
        return discussionService.createDiscussion(user.getId(), questionAnswerId, discussion);
    }

    @PutMapping(value = "/discussions/status")
    @PreAuthorize("hasRole('ROLE_TEACHER') or hasRole('ROLE_STUDENT')")
    public DiscussionDto changeDiscussionStatus(@Valid @RequestParam int discussionId) {
        return discussionService.changeStatus(discussionId);
    }

    @PutMapping(value = "/discussions/reply/availability")
    @PreAuthorize("hasRole('ROLE_TEACHER')")
    public DiscussionDto changeReplyAvailability(@Valid @RequestParam int replyId) {
        return discussionService.changeReplyAvailability(replyId);
    }

    @PostMapping(value = "/discussions/reply/add")
    @PreAuthorize("hasRole('ROLE_TEACHER') or hasRole('ROLE_STUDENT')")
    public ReplyDto addReply(Principal principal, @Valid @RequestBody ReplyDto reply, @Valid @RequestParam int discussionId) {
        User user = (User) ((Authentication) principal).getPrincipal();
        return discussionService.addReply(user.getId(), discussionId, reply);
    }
}
