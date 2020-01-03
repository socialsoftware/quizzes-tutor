package pt.ulisboa.tecnico.socialsoftware.tutor.auth;

import org.fenixedu.sdk.ApplicationConfiguration;
import org.fenixedu.sdk.FenixEduClientImpl;
import org.fenixedu.sdk.exception.FenixEduClientException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ExceptionError.FENIX_CONFIGURATION_ERROR;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private AuthService authService;

    @PostMapping("/fenix")
    public AuthDto fenixAuth(@RequestBody FenixAuthenticationDto data) {
        return this.authService.fenixAuth(new FenixEduInterface(data));
    }

}