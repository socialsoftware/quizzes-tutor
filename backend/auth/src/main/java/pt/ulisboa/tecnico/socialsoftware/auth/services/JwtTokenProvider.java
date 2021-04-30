package pt.ulisboa.tecnico.socialsoftware.auth.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import pt.ulisboa.tecnico.socialsoftware.auth.domain.AuthUser;
import pt.ulisboa.tecnico.socialsoftware.common.security.RSAUtil;

import java.io.File;
import java.security.PrivateKey;
import java.util.Date;

@Component
public class JwtTokenProvider {

    private static final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);
    private static PrivateKey privateKey;

    private static final String PRIVATE_KEY_FILENAME = "private_key.der";

    public JwtTokenProvider() {
    }

    /*public static void generateKeys() {
        try {
            File resource = new ClassPathResource( PRIVATE_KEY_FILENAME).getFile();
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            KeyPair keyPair = keyPairGenerator.generateKeyPair();
            privateKey = keyPair.getPrivate();
            publicKey = keyPair.getPublic();
        } catch (Exception e) {
            logger.error("Unable to generate keys");
        }
    }*/

    static String generateToken(AuthUser authUser) {
        if (privateKey == null) {
            try {
                File resource = new ClassPathResource(PRIVATE_KEY_FILENAME).getFile();
                privateKey = RSAUtil.getPrivateKey(resource.toPath());
                logger.info("Private Key was read successfully: " + privateKey.toString());
            } catch (Exception e) {
                logger.info("Failed reading key");
                logger.info(e.getMessage());
            }
        }

        Claims claims = Jwts.claims().setSubject(String.valueOf(authUser.getId()));
        claims.put("role", authUser.getUserSecurityInfo().getRole());
        claims.put("userId", authUser.getUserSecurityInfo().getId());
        claims.put("isAdmin", authUser.getUserSecurityInfo().isAdmin());
        claims.put("username", authUser.getUsername());

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + 1000*60*60*24);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date())
                .setExpiration(expiryDate)
                .signWith(privateKey)
                .compact();
    }
}