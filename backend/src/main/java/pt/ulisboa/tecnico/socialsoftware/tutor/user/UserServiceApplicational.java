package pt.ulisboa.tecnico.socialsoftware.tutor.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.dto.CourseDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.NotificationResponse;
import pt.ulisboa.tecnico.socialsoftware.tutor.mailer.Mailer;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.AuthExternalUser;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.ExternalUserDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.UserDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.repository.AuthUserRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.utils.LinkHandler;

import java.io.InputStream;
import java.util.Optional;

@Service
public class UserServiceApplicational {

    @Autowired
    private UserService userService;

    @Autowired
    private Mailer mailer;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthUserRepository authUserRepository;

    @Value("${spring.mail.username}")
    private String mailUsername;

    public ExternalUserDto createExternalUser(Integer courseExecutionId, ExternalUserDto externalUserDto) {
        boolean userExists = authUserRepository.findAuthUserByUsername(externalUserDto.getEmail()).isPresent();
        ExternalUserDto user = userService.createExternalUserTransactional(courseExecutionId, externalUserDto);
        if (!userExists) {
            sendConfirmationEmailTo(user.getEmail(), user.getConfirmationToken());
        }
        return user;
    }

    public NotificationResponse<CourseDto> importListOfUsers(InputStream stream, int courseExecutionId) {
        NotificationResponse<CourseDto> courseDtoNotificationResponse = userService.importListOfUsersTransactional(stream, courseExecutionId);
        courseDtoNotificationResponse.getResponse().getCourseExecutionUsers()
                .stream()
                .map(UserDto::getUsername)
                .map(authUserRepository::findAuthUserByUsername)
                .map(Optional::get)
                .forEach(authUser -> this.sendConfirmationEmailTo(authUser.getEmail(), ((AuthExternalUser)authUser).getConfirmationToken()));
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
