package pt.ulisboa.tecnico.socialsoftware.tutor.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.domain.AuthExternalUser;
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.dto.ExternalUserDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.repository.AuthUserRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.dto.CourseDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.NotificationResponse;
import pt.ulisboa.tecnico.socialsoftware.tutor.mailer.Mailer;
import pt.ulisboa.tecnico.socialsoftware.tutor.utils.LinkHandler;

import java.io.InputStream;

@Service
public class UserServiceApplicational {
    @Autowired
    private UserService userService;

    @Autowired
    private Mailer mailer;

    @Autowired
    private AuthUserRepository authUserRepository;

    @Value("${spring.profiles.active}")
    private String activeProfile;

    @Value("${spring.mail.username}")
    private String mailUsername;

    public ExternalUserDto registerExternalUser(Integer courseExecutionId, ExternalUserDto externalUserDto) {
        ExternalUserDto user = userService.registerExternalUserTransactional(courseExecutionId, externalUserDto);
        if (!user.isActive() && !activeProfile.equals("dev")) {
            sendConfirmationEmailTo(user.getEmail(), user.getConfirmationToken());
        }

        return user;
    }

    public NotificationResponse<CourseDto> registerListOfUsers(InputStream stream, int courseExecutionId) {
        NotificationResponse<CourseDto> courseDtoNotificationResponse = userService.registerListOfUsersTransactional(stream, courseExecutionId);

        courseDtoNotificationResponse.getResponse().getCourseExecutionUsers()
                .stream()
                .filter(userDto -> !userDto.isActive())
                .map(userDto -> authUserRepository.findAuthUserByUsername(userDto.getUsername()).get())
                .forEach(authUser -> {
                    this.sendConfirmationEmailTo(authUser.getEmail(), ((AuthExternalUser) authUser).getConfirmationToken());
                });

        return courseDtoNotificationResponse;
    }

    public void sendConfirmationEmailTo(String email, String token) {
        mailer.sendSimpleMail(mailUsername, email, UserService.PASSWORD_CONFIRMATION_MAIL_SUBJECT, buildMailBody(email, token));
    }

    private String buildMailBody(String email, String token) {
        String msg = "To confirm your registration click the following link";
        return String.format("%s: %s", msg, LinkHandler.createConfirmRegistrationLink(email, token));
    }
}
