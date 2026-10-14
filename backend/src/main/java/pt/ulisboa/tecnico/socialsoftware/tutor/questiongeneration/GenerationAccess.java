package pt.ulisboa.tecnico.socialsoftware.tutor.questiongeneration;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.domain.AuthUser;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.GENERATION_NOT_IN_DEMO;

/**
 * Question generation, the pieces of documents under topics and suggested replies send the
 * teacher's material to a model that is paid for, so they are for teachers who log in with their
 * own account: the public demo users, whom anyone can be, do not get them.
 * generation.demo-enabled=true lets the demo users have them, for trying them on a machine where
 * the demo is the only way to log in.
 */
@Component
public class GenerationAccess implements HandlerInterceptor {
    /** Requests this guards, registered in WebConfiguration. */
    public static final String[] PATHS = {"/generation/**", "/discussions/*/replies/suggest", "/topics/*/sources"};

    private final boolean demoEnabled;

    public GenerationAccess(@Value("${generation.demo-enabled:false}") boolean demoEnabled) {
        this.demoEnabled = demoEnabled;
    }

    public boolean allowed(AuthUser authUser) {
        return demoEnabled || authUser.getType() != AuthUser.Type.DEMO;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof AuthUser authUser && !allowed(authUser))
            throw new TutorException(GENERATION_NOT_IN_DEMO);
        return true;
    }
}
