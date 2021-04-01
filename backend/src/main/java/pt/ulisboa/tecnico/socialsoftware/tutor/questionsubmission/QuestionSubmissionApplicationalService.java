package pt.ulisboa.tecnico.socialsoftware.tutor.questionsubmission;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.stereotype.Service;
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.CourseExecutionService;
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsubmission.dto.ReviewDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsubmission.repository.QuestionSubmissionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserService;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.User;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.UserDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.utils.Mailer;

import java.util.ArrayList;
import java.util.List;

@Service
public class QuestionSubmissionApplicationalService {
    private static final Logger logger = LoggerFactory.getLogger(QuestionSubmissionApplicationalService.class);

    private static final String QUESTION_SUBMISSION_SUBJECT = "*Question Submission* ";

    @Autowired
    private QuestionSubmissionService questionSubmissionService;

    @Autowired
    private QuestionSubmissionRepository questionSubmissionRepository;

    @Autowired
    private CourseExecutionService courseExecutionService;

    @Autowired
    private UserService userService;

    @Autowired
    private Mailer mailer;

    @Value("${spring.mail.username}")
    private String mailUsername;

    public ReviewDto createReview(User user, ReviewDto reviewDto) {
        List<UserDto> users = getUsersToSendEmail(user, reviewDto.getQuestionSubmissionId());

        ReviewDto result = questionSubmissionService.createReview(reviewDto);

        users.forEach(userDto -> {
            try {
                mailer.sendSimpleMail(mailUsername,
                        userDto.getEmail(),
                        Mailer.QUIZZES_TUTOR_SUBJECT + QUESTION_SUBMISSION_SUBJECT,
                        "There is a comment to question submission number " + reviewDto.getQuestionSubmissionId() + " by "
                                + result.getName() + " ("
                                + result.getUsername() + "): "
                                + result.getComment());
            } catch (MailException me) {
                logger.debug("createReview, fail to send email to {}", userDto.getEmail());
            }
        });

        return result;
    }

    private List<UserDto> getUsersToSendEmail(User user, Integer questionSubmissionId) {
        List<UserDto> users;
        if (user.getRole().equals(User.Role.STUDENT)) {
            Integer courseExecutionId = questionSubmissionRepository.findCourseExecutionIdByQuestionSubmissionId(questionSubmissionId);
            users = courseExecutionService.getTeachers(courseExecutionId);
        } else {
            Integer userId = questionSubmissionRepository.findSubmitterIdByQuestionSubmissionId(questionSubmissionId);
            UserDto userDto = userService.findUserById(userId);
            users = new ArrayList<>();
            if (userDto != null) {
                users.add(userDto);
            }
        }
        return users;
    }
}
