package pt.ulisboa.tecnico.socialsoftware.tutor.log;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDateTime;

@Service
public class LogService {
    @PersistenceContext
    EntityManager entityManager;


    /*@Retryable(
      value = { SQLException.class },
      maxAttempts = 2,
      backoff = @Backoff(delay = 5000))*/
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public Log create(User user, LocalDateTime time, String operation) {
        Log log = new Log(user, time, operation);
        entityManager.persist(log);
        return log;
    }

}
