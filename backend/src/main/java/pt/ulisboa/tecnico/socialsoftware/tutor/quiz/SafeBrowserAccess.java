package pt.ulisboa.tecnico.socialsoftware.tutor.quiz;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository.QuizRepository;

import java.util.Map;

/**
 * Keeps the requests a student makes to open, answer and conclude a quiz that is only for the Safe
 * Exam Browser from any other browser.
 */
@Component
public class SafeBrowserAccess implements HandlerInterceptor {
    /** Requests this guards, registered in WebConfiguration. */
    public static final String[] PATHS = {
            "/answers/*/start", "/answers/*/byqrcode", "/answers/*/question/*", "/answers/*/submit", "/answers/*/conclude"
    };

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private SafeBrowserCheck safeBrowserCheck;

    @Override
    @SuppressWarnings("unchecked")
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        Map<String, String> variables = (Map<String, String>) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
        String quizId = variables == null ? null : variables.get("quizId");
        if (quizId != null && quizId.matches("\\d{1,9}"))
            quizRepository.findById(Integer.parseInt(quizId)).ifPresent(quiz -> safeBrowserCheck.check(quiz, request));
        return true;
    }
}
