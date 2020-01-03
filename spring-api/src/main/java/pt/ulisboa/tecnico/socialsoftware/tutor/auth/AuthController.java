package pt.ulisboa.tecnico.socialsoftware.tutor.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private AuthService authService;

    @Value("${base.url}")
    private String baseUrl;

    @Value("${oauth.consumer.key}")
    private String oauthConsumerKey;

    @Value("${oauth.consumer.secret}")
    private String oauthConsumerSecret;

    @Value("${callback.url}")
    private String callbackUrl;

    @PostMapping("/fenix")
    public AuthDto fenixAuth(@RequestBody FenixAuthenticationDto authentication) {
        FenixEduInterface fenix = new FenixEduInterface(baseUrl, oauthConsumerKey, oauthConsumerSecret, callbackUrl);
        fenix.authenticate(authentication);
        return this.authService.fenixAuth(fenix);
    }
}