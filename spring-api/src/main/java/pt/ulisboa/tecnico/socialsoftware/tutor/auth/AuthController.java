package pt.ulisboa.tecnico.socialsoftware.tutor.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private AuthService authService;

    @Autowired
    private FenixEduInterface fenix;

    @PostMapping("/fenix")
    public AuthDto fenixAuth(@RequestBody FenixAuthenticationDto authentication) {
        fenix.authenticate(authentication);
        return this.authService.fenixAuth(fenix);
    }
}