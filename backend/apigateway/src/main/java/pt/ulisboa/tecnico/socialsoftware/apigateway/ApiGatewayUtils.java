package pt.ulisboa.tecnico.socialsoftware.apigateway;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;

/*
    The code below has to be duplicated in the ApiGateway and in Common to solve maven dependencies conflicts between
    Webflux and Spring boot WebMvc (used in all services except Api Gateway)
*/

public class ApiGatewayUtils {

    private static final Logger logger = LoggerFactory.getLogger(ApiGatewayUtils.class);

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
}
