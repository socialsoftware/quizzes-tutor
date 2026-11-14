package pt.ulisboa.tecnico.socialsoftware.tutor.quiz;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HexFormat;
import java.util.List;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.QUIZ_SAFE_BROWSER_REQUIRED;

/**
 * Tells whether a request comes from the Safe Exam Browser set up for a quiz. The browser proves it
 * with a hash of the address it asked for and the Config Key of its setup, which only the teacher
 * and the students' configured browsers know. This cannot see which browser it is: it only checks
 * that the proof is there, so the Config Key must stay secret.
 *
 * Two forms of the proof are accepted:
 * <ul>
 *   <li>the header the browser adds to its requests, X-SafeExamBrowser-ConfigKeyHash, made from the
 *       address of the request (or of the page that made it);</li>
 *   <li>the pair of headers the Tutor's page sends from the values of the browser's JavaScript API
 *       (X-SEB-Page-Url and X-SEB-Config-Key-Hash), made from the address of the page. A page of
 *       the Tutor that makes its calls from script has this one to rely on.</li>
 * </ul>
 */
@Component
public class SafeBrowserCheck {
    private static final Logger logger = LoggerFactory.getLogger(SafeBrowserCheck.class);

    static final String SEB_HEADER = "X-SafeExamBrowser-ConfigKeyHash";
    static final String PAGE_URL_HEADER = "X-SEB-Page-Url";
    static final String PAGE_HASH_HEADER = "X-SEB-Config-Key-Hash";

    /** Refuses the request unless the quiz allows any browser or the request proves the Safe Exam Browser. */
    public void check(Quiz quiz, HttpServletRequest request) {
        if (quiz == null || !quiz.isSafeBrowserOnly())
            return;
        if (!isFromSafeBrowser(quiz.getSebConfigKey(), request)) {
            // What helps to find out why a real Safe Exam Browser was refused: which proofs came and the
            // addresses they would have been made for. Never the key, nor the hashes.
            logger.info("Safe Exam Browser proof refused for quiz {}: request {}, header {} {}, page {} {}, referer {}",
                    quiz.getId(), requestUrl(request), SEB_HEADER, request.getHeader(SEB_HEADER) != null ? "present" : "missing",
                    PAGE_HASH_HEADER, request.getHeader(PAGE_HASH_HEADER) != null ? "present" : "missing",
                    request.getHeader(PAGE_URL_HEADER), request.getHeader("Referer"));
            throw new TutorException(QUIZ_SAFE_BROWSER_REQUIRED);
        }
    }

    boolean isFromSafeBrowser(String configKey, HttpServletRequest request) {
        if (configKey == null || configKey.isBlank())
            return false;

        String direct = request.getHeader(SEB_HEADER);
        if (direct != null) {
            List<String> addresses = new ArrayList<>();
            addresses.add(requestUrl(request));
            String referer = withoutFragment(request.getHeader("Referer"));
            if (referer != null)
                addresses.add(referer);
            if (addresses.stream().anyMatch(address -> matches(address, configKey, direct)))
                return true;
        }

        String page = withoutFragment(request.getHeader(PAGE_URL_HEADER));
        String pageHash = request.getHeader(PAGE_HASH_HEADER);
        return page != null && pageHash != null && matches(page, configKey, pageHash);
    }

    static boolean matches(String address, String configKey, String given) {
        if (address == null || given == null)
            return false;
        byte[] expected = sha256Hex(address + configKey.trim().toLowerCase()).getBytes(StandardCharsets.US_ASCII);
        return MessageDigest.isEqual(expected, given.trim().toLowerCase().getBytes(StandardCharsets.US_ASCII));
    }

    static String sha256Hex(String text) {
        try {
            return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(text.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 is not available");
        }
    }

    /** The address the browser asked for, as a proxy in front of the server forwarded it. */
    static String requestUrl(HttpServletRequest request) {
        String scheme = firstOf(request.getHeader("X-Forwarded-Proto"), request.getScheme());
        String host = firstOf(request.getHeader("X-Forwarded-Host"), request.getHeader("Host"));
        if (host == null)
            host = request.getServerName() + (request.getServerPort() > 0 ? ":" + request.getServerPort() : "");
        String query = request.getQueryString();
        return scheme + "://" + host + request.getRequestURI() + (query == null ? "" : "?" + query);
    }

    private static String firstOf(String forwarded, String own) {
        if (forwarded == null || forwarded.isBlank())
            return own;
        return forwarded.split(",")[0].trim();
    }

    private static String withoutFragment(String url) {
        if (url == null || url.isBlank())
            return null;
        int hash = url.indexOf('#');
        return hash < 0 ? url : url.substring(0, hash);
    }
}
