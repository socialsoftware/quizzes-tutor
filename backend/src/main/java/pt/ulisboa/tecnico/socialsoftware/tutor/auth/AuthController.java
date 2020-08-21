package pt.ulisboa.tecnico.socialsoftware.tutor.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.ExternalUserDto;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.INVALID_LOGIN_CREDENTIALS;

@RestController
public class AuthController {
    @Autowired
    private AuthService authService;

    @Autowired
    private AuthServiceApplicational authServiceApplicational;

    @Value("${base.url}")
    private String baseUrl;

    @Value("${oauth.consumer.key}")
    private String oauthConsumerKey;

    @Value("${oauth.consumer.secret}")
    private String oauthConsumerSecret;

    @Value("${callback.url}")
    private String callbackUrl;

    @GetMapping("/auth/fenix")
    public AuthDto fenixAuth(@RequestParam String code) {
        FenixEduInterface fenix = new FenixEduInterface(baseUrl, oauthConsumerKey, oauthConsumerSecret, callbackUrl);
        fenix.authenticate(code);
        return this.authService.fenixAuth(fenix);
    }

    @GetMapping("/auth/external")
    public AuthDto externalUserAuth(@RequestParam String email, @RequestParam String password) {
        try {
            return authService.externalUserAuth(email, password);
        } catch (TutorException e) {
            throw new TutorException(INVALID_LOGIN_CREDENTIALS);
        }
    }

    @GetMapping("/auth/demo/student")
    public AuthDto demoStudentAuth(@RequestParam Boolean createNew) {
        return this.authService.demoStudentAuth(createNew);
    }

    @GetMapping("/auth/demo/teacher")
    public AuthDto demoTeacherAuth() {
        return this.authService.demoTeacherAuth();
    }

    @GetMapping("/auth/demo/admin")
    public AuthDto demoAdminAuth() {
        return this.authService.demoAdminAuth();
    }

    @PostMapping("/auth/registration/confirm")
    public ExternalUserDto confirmRegistration(@RequestBody ExternalUserDto externalUserDto){
        ExternalUserDto user = authServiceApplicational.confirmRegistration(externalUserDto);
        return user;
    }
}