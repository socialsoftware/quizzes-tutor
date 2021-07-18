package pt.ulisboa.tecnico.socialsoftware.auth.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import pt.ulisboa.tecnico.socialsoftware.auth.domain.AuthTecnicoUser;
import pt.ulisboa.tecnico.socialsoftware.auth.domain.AuthUser;
import pt.ulisboa.tecnico.socialsoftware.auth.repository.AuthUserRepository;
import pt.ulisboa.tecnico.socialsoftware.common.dtos.auth.AuthUserType;
import pt.ulisboa.tecnico.socialsoftware.common.dtos.user.Role;
import pt.ulisboa.tecnico.socialsoftware.common.security.UserInfo;

import javax.servlet.http.HttpServletRequest;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class JwtTokenProvider {
    private static final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);

    private AuthUserRepository authUserRepository;

    private static PublicKey publicKey;
    private static PrivateKey privateKey;

    public JwtTokenProvider() {
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
        claims.put("role", authUser.getUserSecurityInfo().getRole());
        claims.put("userId", authUser.getUserSecurityInfo().getId());
        claims.put("isAdmin", authUser.getUserSecurityInfo().isAdmin());
        claims.put("username", authUser.getUsername());
        claims.put("executions", authUser.getUserCourseExecutions());
        claims.put("courseAcronyms", authUser.getType().equals(AuthUserType.TECNICO) ?
                ((AuthTecnicoUser) authUser).getEnrolledCoursesAcronyms() : "");
        claims.put("name",authUser.getUserSecurityInfo().getName());

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + 1000*60*60*24);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date())
                .setExpiration(expiryDate)
                .signWith(privateKey)
                .compact();
    }

    public static String getToken(HttpServletRequest req) {
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

    public static Claims getAllClaimsFromToken(String token) {
        return Jwts.parserBuilder().setSigningKey(publicKey).build().parseClaimsJws(token).getBody();
    }

    public Authentication getAuthentication(String token) {
        Claims tokenClaims = getAllClaimsFromToken(token);
        int userId = tokenClaims.get("userId", Integer.class);
        Role role = Role.valueOf(tokenClaims.get("role", String.class));
        boolean isAdmin = tokenClaims.get("isAdmin", Boolean.class);
        String username = tokenClaims.get("username", String.class);
        List<Integer> executions = (ArrayList<Integer>) tokenClaims.get("executions");
        String enrolledCourseAcronyms = tokenClaims.get("courseAcronyms", String.class);
        String name = tokenClaims.get("name", String.class);

        UserInfo userInfo = new UserInfo(userId, role, isAdmin, username, executions, enrolledCourseAcronyms, name);
        return new UsernamePasswordAuthenticationToken(userInfo, "", userInfo.getAuthorities());
    }
}