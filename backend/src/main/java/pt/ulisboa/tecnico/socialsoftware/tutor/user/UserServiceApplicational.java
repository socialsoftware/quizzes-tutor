package pt.ulisboa.tecnico.socialsoftware.tutor.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.NotificationResponse;
import pt.ulisboa.tecnico.socialsoftware.tutor.mailer.Mailer;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.ExternalUserDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.utils.LinkHandler;

import java.io.InputStream;

@Service
public class UserServiceApplicational {

    @Autowired
    private UserService userService;

    @Autowired
    private Mailer mailer;

    @Value("${spring.mail.username}")
    private String mailUsername;

    public ExternalUserDto createExternalUser(Integer courseExecutionId, ExternalUserDto externalUserDto) {
        ExternalUserDto user = userService.createExternalUserTransactional(courseExecutionId, externalUserDto);
        sendConfirmationEmailTo(user);
        return user;
    }

    public NotificationResponse<CourseDto> importListOfUsers(InputStream stream, int courseExecutionId) {
        NotificationResponse<CourseDto> courseDtoNotificationResponse = userService.importListOfUsersTransactional(stream, courseExecutionId);
        courseDtoNotificationResponse.getResponse().getCourseExecutionUsers()
                .forEach(this::sendConfirmationEmailTo);
        return courseDtoNotificationResponse;
    }



    public void sendConfirmationEmailTo(ExternalUserDto user) {
        mailer.sendSimpleMail(mailUsername, user.getEmail(), User.PASSWORD_CONFIRMATION_MAIL_SUBJECT, buildMailBody(user));
    }

    private String buildMailBody(ExternalUserDto user) {
        String msg = "To confirm your registration click the following link";
        return String.format("%s: %s", msg, LinkHandler.createConfirmRegistrationLink(user.getEmail(), user.getConfirmationToken()));
    }
}
