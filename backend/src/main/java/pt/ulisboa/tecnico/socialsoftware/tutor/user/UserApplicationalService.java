package pt.ulisboa.tecnico.socialsoftware.tutor.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.domain.AuthExternalUser;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.ExternalUserDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.repository.AuthUserRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.dto.CourseExecutionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.NotificationResponse;
import pt.ulisboa.tecnico.socialsoftware.tutor.utils.Mailer;
import pt.ulisboa.tecnico.socialsoftware.tutor.utils.LinkHandler;

import java.io.InputStream;

@Service
public class UserApplicationalService {
    @Autowired
    private UserService userService;

    @Autowired
    private Mailer mailer;

    @Autowired
    private AuthUserRepository authUserRepository;

    @Value("${spring.mail.username}")
    private String mailUsername;

    public ExternalUserDto registerExternalUser(Integer courseExecutionId, ExternalUserDto externalUserDto) {
        ExternalUserDto user = userService.registerExternalUserTransactional(courseExecutionId, externalUserDto);
        if (!user.isActive()) {
             sendConfirmationEmailTo(user.getUsername(), user.getEmail(), user.getConfirmationToken());
        }

        return user;
    }

    public NotificationResponse<CourseExecutionDto> registerListOfUsers(InputStream stream, int courseExecutionId) {
        NotificationResponse<CourseExecutionDto> courseDtoNotificationResponse = userService.registerListOfUsersTransactional(stream, courseExecutionId);

        courseDtoNotificationResponse.getResponse().getCourseExecutionUsers()
                .stream()
                .filter(userDto -> !userDto.isActive())
                .map(userDto -> authUserRepository.findAuthUserByUsername(userDto.getUsername()).get())
                .forEach(authUser ->
                    this.sendConfirmationEmailTo(authUser.getUsername(), authUser.getEmail(), ((AuthExternalUser) authUser).getConfirmationToken())
                );

        return courseDtoNotificationResponse;
    }

    public ExternalUserDto confirmRegistration(ExternalUserDto externalUserDto) {
        ExternalUserDto user = userService.confirmRegistrationTransactional(externalUserDto);
        if (!user.isActive()) {
            sendConfirmationEmailTo(user.getUsername(), user.getEmail(), user.getConfirmationToken());
        }
        return user;
    }

    public void sendConfirmationEmailTo(String username, String email, String token) {
        mailer.sendSimpleMail(mailUsername, email, Mailer.QUIZZES_TUTOR_SUBJECT + UserService.PASSWORD_CONFIRMATION_MAIL_SUBJECT, buildMailBody(username, token));
    }

    private String buildMailBody(String username, String token) {
        String msg = "To confirm your registration, as external user using username (" + username + ") click the following link";
        return String.format("%s: %s", msg, LinkHandler.createConfirmRegistrationLink(username, token));
    }
}
