package pt.ulisboa.tecnico.socialsoftware.apigateway;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

import java.io.InputStream;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Date;

/*
    The code below has to be duplicated in the ApiGateway and in Common to solve maven dependencies conflicts between
    Webflux and Spring boot WebMvc (used in all services except Api Gateway)
*/

public class ApiGatewaySecurityUtils {

    private static final Logger logger = LoggerFactory.getLogger(ApiGatewaySecurityUtils.class);

    private static PublicKey publicKey;

    private static final String PUBLIC_KEY_FILENAME = "public_key.der";

    public static PublicKey getPublicKey() {
        if (publicKey == null) {
            try {
                InputStream resource = new ClassPathResource(PUBLIC_KEY_FILENAME).getInputStream();
                publicKey = getPublicKey(resource);
            } catch (Exception e) {
                logger.info("Failed reading key");
                logger.info(e.getMessage());
            }
        }
        return publicKey;
    }

    public static PublicKey getPublicKey(InputStream inputStream) throws Exception {

        byte[] keyBytes = inputStream.readAllBytes();

        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePublic(spec);
    }

    public static boolean isTokenExpired(String token) {
        Claims tokenClaims = Jwts.parserBuilder().setSigningKey(getPublicKey()).build().parseClaimsJws(token).getBody();
        return tokenClaims.getExpiration().before(new Date());
    }
}
