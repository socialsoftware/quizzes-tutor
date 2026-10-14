package pt.ulisboa.tecnico.socialsoftware.tutor.questiongeneration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.GENERATION_TOO_FAST;
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.SUGGESTION_TOO_FAST;

/**
 * Keeps one teacher from firing request after request at the model: a minimum time between two
 * requests of the same kind, and a cap on how many generation jobs a teacher can have waiting.
 * Kept in memory (per backend process), so it only guards against bursts; the per-course daily
 * quota and the queue limits live in the generation service, which is what talks to the provider.
 */
@Component
public class RequestThrottle {
    private long minGenerationMillis;
    private long minSuggestionMillis;
    private int maxPendingJobs;
    private final Map<String, Long> last = new ConcurrentHashMap<>();

    @Autowired
    public RequestThrottle(
            @Value("${generation.min-seconds-between-requests:10}") long minSecondsBetweenGenerations,
            @Value("${generation.min-seconds-between-suggestions:5}") long minSecondsBetweenSuggestions,
            @Value("${generation.max-pending-jobs-per-teacher:2}") int maxPendingJobs) {
        this.minGenerationMillis = minSecondsBetweenGenerations * 1000;
        this.minSuggestionMillis = minSecondsBetweenSuggestions * 1000;
        this.maxPendingJobs = maxPendingJobs;
    }

    public int getMaxPendingJobs() {
        return maxPendingJobs;
    }

    /** For tests: the limits of this bean are set from the properties, and a test needs to change them. */
    public void configure(long minSecondsBetweenGenerations, long minSecondsBetweenSuggestions, int maxPendingJobs) {
        this.minGenerationMillis = minSecondsBetweenGenerations * 1000;
        this.minSuggestionMillis = minSecondsBetweenSuggestions * 1000;
        this.maxPendingJobs = maxPendingJobs;
        this.last.clear();
    }

    /** Fails if this teacher asked for questions too recently. It does not count the request yet. */
    public void checkGeneration(Integer userId) {
        long wait = remaining("generate", userId, minGenerationMillis);
        if (wait > 0)
            throw new TutorException(GENERATION_TOO_FAST, (wait + 999) / 1000);
    }

    /** Counts a request that went through (a refused one does not make the teacher wait). */
    public void recordGeneration(Integer userId) {
        last.put(key("generate", userId), System.currentTimeMillis());
    }

    /** Checks and counts a suggestion request: the button calls the model every time it is pressed. */
    public void checkAndRecordSuggestion(Integer userId) {
        long wait = remaining("suggest", userId, minSuggestionMillis);
        if (wait > 0)
            throw new TutorException(SUGGESTION_TOO_FAST, (wait + 999) / 1000);
        last.put(key("suggest", userId), System.currentTimeMillis());
    }

    private long remaining(String action, Integer userId, long minMillis) {
        if (minMillis <= 0)
            return 0;
        Long previous = last.get(key(action, userId));
        return previous == null ? 0 : minMillis - (System.currentTimeMillis() - previous);
    }

    private String key(String action, Integer userId) {
        return action + ":" + userId;
    }
}
