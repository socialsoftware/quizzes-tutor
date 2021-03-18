package pt.ulisboa.tecnico.socialsoftware.tutor.discussion.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pt.ulisboa.tecnico.socialsoftware.tutor.discussion.DiscussionApplicationalService;
import pt.ulisboa.tecnico.socialsoftware.tutor.discussion.DiscussionService;
import pt.ulisboa.tecnico.socialsoftware.tutor.discussion.dto.DiscussionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.discussion.dto.ReplyDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.User;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;

@RestController
public class DiscussionController {
    @Autowired
    private DiscussionApplicationalService discussionApplicationalService;

    @Autowired
    private DiscussionService discussionService;

    @GetMapping("/discussions/courseexecutions/{courseExecutionId}/users")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#courseExecutionId, 'EXECUTION.ACCESS')")
    public List<DiscussionDto> getDiscussionsByUserId(Principal principal, @PathVariable int courseExecutionId) {
        User user = (User) ((Authentication) principal).getPrincipal();
        return this.discussionService.findByCourseExecutionIdAndUserId(courseExecutionId, user.getId());
    }

    @GetMapping("/discussions/courseexecutions/{courseExecutionId}")
    @PreAuthorize("hasRole('ROLE_TEACHER') and hasPermission(#courseExecutionId, 'EXECUTION.ACCESS')")
    public List<DiscussionDto> getCourseExecutionDiscussions(@PathVariable int courseExecutionId) {
        return this.discussionService.findDiscussionsByCourseExecutionId(courseExecutionId);
    }

    @GetMapping("/discussions/open/courseexecutions/{courseExecutionId}")
    @PreAuthorize("hasRole('ROLE_TEACHER') and hasPermission(#courseExecutionId, 'EXECUTION.ACCESS')")
    public List<DiscussionDto> getOpenCourseExecutionDiscussions(@PathVariable int courseExecutionId) {
        return this.discussionService.findOpenDiscussionsByCourseExecutionId(courseExecutionId);
    }

    @GetMapping(value = "/discussions/clarifications/questions/{questionId}")
    @PreAuthorize("(hasRole('ROLE_STUDENT') or hasRole('ROLE_TEACHER')) and hasPermission(#questionId, 'QUESTION.ACCESS')")
    public List<ReplyDto> getClarificationsByQuestionId(@PathVariable Integer questionId) {
        return discussionService.findClarificationsByQuestionId(questionId);
    }

    @PostMapping(value = "/discussions/create")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#questionAnswerId, 'QUESTION_ANSWER.ACCESS')")
    public DiscussionDto createDiscussion(@RequestParam int questionAnswerId, @Valid @RequestBody DiscussionDto discussion){
        return discussionApplicationalService.createDiscussion(questionAnswerId, discussion);
    }

    @PutMapping(value = "/discussions/{discussionId}/status")
    @PreAuthorize("(hasRole('ROLE_TEACHER') and hasPermission(#discussionId, 'DISCUSSION.ACCESS')) or (hasRole('ROLE_STUDENT') and hasPermission(#discussionId, 'DISCUSSION.OWNER'))")
    public DiscussionDto changeDiscussionStatus(@PathVariable int discussionId) {
        return discussionService.changeStatus(discussionId);
    }

    @PutMapping(value = "/discussions/replies/{replyId}/availability")
    @PreAuthorize("hasRole('ROLE_TEACHER') and hasPermission(#replyId, 'REPLY.ACCESS')")
    public DiscussionDto changeReplyAvailability(@PathVariable int replyId) {
        return discussionService.changeReplyAvailability(replyId);
    }

    @PostMapping(value = "/discussions/{discussionId}/replies/add")
    @PreAuthorize("(hasRole('ROLE_TEACHER') and hasPermission(#discussionId, 'DISCUSSION.ACCESS')) or (hasRole('ROLE_STUDENT') and hasPermission(#discussionId, 'DISCUSSION.OWNER'))")
    public ReplyDto addReply(Principal principal, @Valid @RequestBody ReplyDto reply, @PathVariable int discussionId) {
        User user = (User) ((Authentication) principal).getPrincipal();
        return discussionApplicationalService.addReply(user, discussionId, reply);
    }
}
