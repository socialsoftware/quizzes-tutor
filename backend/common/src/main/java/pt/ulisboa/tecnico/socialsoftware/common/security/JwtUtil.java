package pt.ulisboa.tecnico.socialsoftware.common.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import pt.ulisboa.tecnico.socialsoftware.common.dtos.user.Role;

import javax.servlet.http.HttpServletRequest;
import java.io.InputStream;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;

@Component
public class JwtUtil {

    private static final Logger logger = LoggerFactory.getLogger(JwtUtil.class);

    private static PublicKey publicKey;

    private static final String PUBLIC_KEY_FILENAME = "public_key.der";

    public static PublicKey getPublicKey() {
        if (publicKey == null) {
            try {
                InputStream resource = new ClassPathResource(PUBLIC_KEY_FILENAME).getInputStream();
                publicKey = RSAUtil.getPublicKey(resource);
            } catch (Exception e) {
                logger.info("Failed reading key");
                logger.info(e.getMessage());
            }
        }
        return publicKey;
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
        return Jwts.parserBuilder().setSigningKey(getPublicKey()).build().parseClaimsJws(token).getBody();
    }

    public static Authentication getAuthentication(String token) {
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
