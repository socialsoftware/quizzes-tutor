package pt.ulisboa.tecnico.socialsoftware.tutor.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserServiceApplicational;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.ExternalUserDto;

@Service
public class AuthServiceApplcational {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserServiceApplicational userServiceApplicational;

    public ExternalUserDto confirmRegistration(ExternalUserDto externalUserDto) {
        ExternalUserDto user = authService.confirmRegistrationTransactional(externalUserDto);
        if(!user.isActive()){
            userServiceApplicational.sendConfirmationEmailTo(user);
        }
        return user;
    }
}
