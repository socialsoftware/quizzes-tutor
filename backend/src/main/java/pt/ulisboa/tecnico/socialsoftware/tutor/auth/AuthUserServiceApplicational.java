package pt.ulisboa.tecnico.socialsoftware.tutor.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserServiceApplicational;
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.dto.ExternalUserDto;

@Service
public class AuthUserServiceApplicational {
    @Autowired
    private AuthUserService authUserService;

    @Autowired
    private UserServiceApplicational userServiceApplicational;

    public ExternalUserDto confirmRegistration(ExternalUserDto externalUserDto) {
        ExternalUserDto user = authUserService.confirmRegistrationTransactional(externalUserDto);
        if (!user.isActive()) {
            System.out.println("confirmRegistration " + user.getEmail() + " " + user.getConfirmationToken());
            System.out.println("confirmRegistration " + user.getEmail() + " " + user.getConfirmationToken());
            System.out.println("confirmRegistration " + user.getEmail() + " " + user.getConfirmationToken());
            System.out.println("confirmRegistration " + user.getEmail() + " " + user.getConfirmationToken());
            System.out.println("confirmRegistration " + user.getEmail() + " " + user.getConfirmationToken());
            System.out.println("confirmRegistration " + user.getEmail() + " " + user.getConfirmationToken());
            userServiceApplicational.sendConfirmationEmailTo(user.getEmail(), user.getConfirmationToken());
        }
        return user;
    }
}
