package pt.ulisboa.tecnico.socialsoftware.tutor.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.domain.AuthUser;
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.repository.AuthUserRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;

import javax.servlet.http.HttpServletRequest;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Date;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.AUTHUSER_NOT_FOUND;

@Component
public class JwtTokenProvider {

    private static final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);
    private AuthUserRepository authUserRepository;
    private static PublicKey publicKey;
    private static PrivateKey privateKey;

    public JwtTokenProvider(AuthUserRepository authUserRepository) {
        this.authUserRepository = authUserRepository;
    }

    public static void generateKeys() {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            KeyPair keyPair = keyPairGenerator.generateKeyPair();
            privateKey = keyPair.getPrivate();
            publicKey = keyPair.getPublic();
        } catch (Exception e) {
            logger.error("Unable to generate keys");
        }
    }

    static String generateToken(AuthUser authUser) {
        if (publicKey == null) {
            generateKeys();
        }

        Claims claims = Jwts.claims().setSubject(String.valueOf(authUser.getId()));
        claims.put("role", authUser.getUser().getRole());

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + 1000*60*60*24);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date())
                .setExpiration(expiryDate)
                .signWith(privateKey)
                .compact();
    }

    static String getToken(HttpServletRequest req) {
        String authHeader = req.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        } else if (authHeader != null && authHeader.startsWith("AUTH")) {
            return authHeader.substring(4);
        } else if (authHeader != null) {
            return authHeader;
        }
        return "";
    }
    static int getAuthUserId(String token) {
        return Integer.parseInt(Jwts.parserBuilder().setSigningKey(publicKey).build().parseClaimsJws(token).getBody().getSubject());
    }

    Authentication getAuthentication(String token) {
        int authUserId = getAuthUserId(token);
        AuthUser authUser = this.authUserRepository.findById(authUserId).orElseThrow(() -> new TutorException(AUTHUSER_NOT_FOUND, authUserId));
        return new UsernamePasswordAuthenticationToken(authUser, "", authUser.getAuthorities());
    }
}