package pt.ulisboa.tecnico.socialsoftware.tutor.access;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDateTime;

@Service
public class AccessService {
    @Autowired
    private AccessRepository accessRepository;

    @PersistenceContext
    EntityManager entityManager;

    @Transactional
    public Access create(User user, LocalDateTime time, String operation) {
        Access access = new Access(user, time, operation);
        entityManager.persist(access);
        return access;
    }

}
