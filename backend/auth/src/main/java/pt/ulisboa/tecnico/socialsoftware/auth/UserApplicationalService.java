package pt.ulisboa.tecnico.socialsoftware.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import pt.ulisboa.tecnico.socialsoftware.auth.domain.AuthExternalUser;
import pt.ulisboa.tecnico.socialsoftware.auth.repository.AuthUserRepository;
import pt.ulisboa.tecnico.socialsoftware.common.dtos.execution.CourseExecutionDto;
import pt.ulisboa.tecnico.socialsoftware.common.dtos.user.ExternalUserDto;
import pt.ulisboa.tecnico.socialsoftware.common.exceptions.NotificationResponse;
import pt.ulisboa.tecnico.socialsoftware.common.utils.LinkHandler;
import pt.ulisboa.tecnico.socialsoftware.common.utils.Mailer;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserService;

import java.io.InputStream;

@Service
public class UserApplicationalService {
    @Autowired
    private AuthUserService authUserService;

    @Autowired
    private Mailer mailer;

    @Autowired
    private AuthUserRepository authUserRepository;

    @Value("${spring.mail.username}")
    private String mailUsername;

    public ExternalUserDto registerExternalUser(Integer courseExecutionId, ExternalUserDto externalUserDto) {
        ExternalUserDto user = authUserService.registerExternalUserTransactional(courseExecutionId, externalUserDto);
        if (!user.isActive()) {
            sendConfirmationEmailTo(user.getUsername(), user.getEmail(), user.getConfirmationToken());
        }

        return user;
    }

    public NotificationResponse<CourseExecutionDto> registerListOfUsers(InputStream stream, int courseExecutionId) {
        NotificationResponse<CourseExecutionDto> courseDtoNotificationResponse = authUserService.registerListOfUsersTransactional(stream, courseExecutionId);

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
        ExternalUserDto user = authUserService.confirmRegistrationTransactional(externalUserDto);
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