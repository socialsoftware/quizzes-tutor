package pt.ulisboa.tecnico.socialsoftware.tutor.discussion;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.stereotype.Service;
import pt.ulisboa.tecnico.socialsoftware.tutor.discussion.dto.DiscussionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.discussion.dto.ReplyDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.CourseExecutionService;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserService;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.User;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.UserDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.utils.Mailer;

import java.util.ArrayList;
import java.util.List;

@Service
public class DiscussionApplicationalService {
    private static final Logger logger = LoggerFactory.getLogger(DiscussionApplicationalService.class);

    private static final String DISCUSSION_SUBJECT = "*Discussion* ";

    @Autowired
    private DiscussionService discussionService;

    @Autowired
    private CourseExecutionService courseExecutionService;

    @Autowired
    private UserService userService;

    @Autowired
    private Mailer mailer;

    @Value("${spring.mail.username}")
    private String mailUsername;

    public DiscussionDto createDiscussion(int questionAnswerId, DiscussionDto discussion) {
        List<UserDto> teachers = courseExecutionService.getTeachers(discussion.getCourseExecutionId());

        DiscussionDto discussionDto = discussionService.createDiscussion(questionAnswerId, discussion);
        
        teachers.forEach(teacher -> {
            try {
                mailer.sendSimpleMail(mailUsername,
                    teacher.getEmail(),
                    Mailer.QUIZZES_TUTOR_SUBJECT + DISCUSSION_SUBJECT,
                    "Discussion number " + discussionDto.getId() + " was created by " + discussionDto.getName() + " (" + discussionDto.getUsername() + "): " + discussion.getMessage());
            } catch (MailException me) {
                logger.debug("createDiscussion, fail to send email to {}", teacher.getEmail());
            }});

        return discussionDto;
    }

    public ReplyDto addReply(User user, int discussionId, ReplyDto replyDto) {
        DiscussionDto discussionDto = discussionService.findDiscussionById(discussionId, false);
        List<UserDto> users = getUsersToSendEmail(user, discussionDto);

        ReplyDto result = discussionService.addReply(user.getId(), discussionId, replyDto);

        users.forEach(userDto -> {
            try {
                mailer.sendSimpleMail(mailUsername,
                        userDto.getEmail(),
                        Mailer.QUIZZES_TUTOR_SUBJECT + DISCUSSION_SUBJECT,
                        "There is a reply to discussion number " + discussionDto.getId() + " by "
                                + result.getName() + " ("
                                + result.getUsername() + "): " + result.getMessage());
            } catch (MailException me) {
                logger.debug("addReply, fail to send email to {}", userDto.getEmail());
            }
        });

        return result;
    }

    private List<UserDto> getUsersToSendEmail(User user, DiscussionDto discussionDto) {
        List<UserDto> users;
        if (user.getRole().equals(User.Role.STUDENT)) {
            users = courseExecutionService.getTeachers(discussionDto.getCourseExecutionId());
        } else {
            UserDto userDto = userService.findUserByUsername(discussionDto.getUsername());
            users = new ArrayList<>();
            if (userDto != null) {
                users.add(userDto);
            }
        }
        return users;
    }
}
