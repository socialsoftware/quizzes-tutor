package pt.ulisboa.tecnico.socialsoftware.tutor.questiongeneration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermissions;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.Base64;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.TEACHER_API_KEYS_NOT_CONFIGURED;
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.TEACHER_API_KEY_UNREADABLE;

/**
 * Encrypts the API keys teachers give for their own requests (AES-256-GCM, a new random nonce for
 * every key). The master key (32 bytes in base64) is never stored with the data, so a copy of the
 * database alone reveals no key. It comes from generation.keys.master-key if that is set; if not,
 * the first start makes a random one and keeps it in generation.keys.master-key-file, readable
 * only by the server's user, and later starts read it from there. That file must be on a volume
 * that survives the container: without it the keys saved so far cannot be read and the teachers
 * give them again. What is stored is bound to the teacher and the provider it was saved for, so a
 * row moved to another teacher cannot be read.
 */
@Component
public class ApiKeyCipher {
    private static final Logger logger = LoggerFactory.getLogger(ApiKeyCipher.class);
    private static final int KEY_BYTES = 32;
    private static final int NONCE_BYTES = 12;
    private static final int TAG_BITS = 128;

    private final SecretKeySpec masterKey;
    private final SecureRandom random = new SecureRandom();

    @Autowired
    public ApiKeyCipher(@Value("${generation.keys.master-key:}") String masterKey,
                        @Value("${generation.keys.master-key-file:}") String masterKeyFile) {
        String source = masterKey == null ? "" : masterKey.trim();
        String where = "generation.keys.master-key";
        if (source.isEmpty() && masterKeyFile != null && !masterKeyFile.isBlank()) {
            where = masterKeyFile.trim();
            source = readOrCreate(Path.of(where));
        }
        this.masterKey = source.isEmpty() ? null : parse(source, where);
    }

    /** With the key given directly, and no file to keep one in. */
    public ApiKeyCipher(String masterKey) {
        this(masterKey, "");
    }

    private static SecretKeySpec parse(String base64, String where) {
        byte[] bytes;
        try {
            bytes = Base64.getDecoder().decode(base64);
        } catch (IllegalArgumentException e) {
            throw new IllegalStateException(where + " does not hold base64");
        }
        if (bytes.length != KEY_BYTES)
            throw new IllegalStateException(where + " must hold " + KEY_BYTES + " bytes in base64");
        return new SecretKeySpec(bytes, "AES");
    }

    /**
     * The key in the file, or a new one written to it. A file that exists is never replaced, even
     * if it is wrong: replacing it would make every saved key unreadable. If the file cannot be
     * written the server starts without keys for teachers, and says so.
     */
    private String readOrCreate(Path file) {
        try {
            if (Files.exists(file))
                return Files.readString(file, StandardCharsets.UTF_8).trim();

            byte[] bytes = new byte[KEY_BYTES];
            random.nextBytes(bytes);
            String encoded = Base64.getEncoder().encodeToString(bytes);
            Path parent = file.toAbsolutePath().getParent();
            if (parent != null)
                Files.createDirectories(parent);
            try {
                try {
                    Files.createFile(file, PosixFilePermissions.asFileAttribute(PosixFilePermissions.fromString("rw-------")));
                } catch (UnsupportedOperationException e) {
                    Files.createFile(file);
                }
            } catch (FileAlreadyExistsException e) {
                // another start made it a moment ago: the key is the one it wrote
                return Files.readString(file, StandardCharsets.UTF_8).trim();
            }
            Files.writeString(file, encoded + System.lineSeparator(), StandardCharsets.UTF_8);
            logger.info("Made the key that encrypts teachers' API keys and saved it in {}: keep that file on a volume that survives the server", file);
            return encoded;
        } catch (IOException e) {
            logger.warn("Teachers cannot save API keys: the key file {} cannot be read or written ({})", file, e.getClass().getSimpleName());
            return "";
        }
    }

    /** Whether the server has a master key: without one teachers cannot save keys. */
    public boolean isConfigured() {
        return masterKey != null;
    }

    public String encrypt(Integer userId, String provider, String plain) {
        requireConfigured();
        try {
            byte[] nonce = new byte[NONCE_BYTES];
            random.nextBytes(nonce);
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE, masterKey, new GCMParameterSpec(TAG_BITS, nonce));
            cipher.updateAAD(bind(userId, provider));
            byte[] encrypted = cipher.doFinal(plain.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(ByteBuffer.allocate(nonce.length + encrypted.length).put(nonce).put(encrypted).array());
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("the API key could not be encrypted");
        }
    }

    public String decrypt(Integer userId, String provider, String stored) {
        requireConfigured();
        try {
            byte[] all = Base64.getDecoder().decode(stored);
            if (all.length <= NONCE_BYTES)
                throw new TutorException(TEACHER_API_KEY_UNREADABLE);
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.DECRYPT_MODE, masterKey, new GCMParameterSpec(TAG_BITS, all, 0, NONCE_BYTES));
            cipher.updateAAD(bind(userId, provider));
            return new String(cipher.doFinal(all, NONCE_BYTES, all.length - NONCE_BYTES), StandardCharsets.UTF_8);
        } catch (GeneralSecurityException | IllegalArgumentException e) {
            // a wrong master key, a changed row or a row of another teacher all end here
            throw new TutorException(TEACHER_API_KEY_UNREADABLE);
        }
    }

    private byte[] bind(Integer userId, String provider) {
        return ("teacher-api-key:" + userId + ":" + provider).getBytes(StandardCharsets.UTF_8);
    }

    private void requireConfigured() {
        if (masterKey == null)
            throw new TutorException(TEACHER_API_KEYS_NOT_CONFIGURED);
    }
}
