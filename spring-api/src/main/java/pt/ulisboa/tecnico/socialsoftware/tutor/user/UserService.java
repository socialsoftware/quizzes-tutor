package pt.ulisboa.tecnico.socialsoftware.tutor.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.UsersXmlExport;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.UsersXmlImport;
import pt.ulisboa.tecnico.socialsoftware.tutor.log.LogService;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Calendar;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ExceptionError.DUPLICATE_USER;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @PersistenceContext
    EntityManager entityManager;

    @Autowired
    private LogService logService;

    public User findByUsername(String username) {
        return this.userRepository.findByUsername(username);
    }

    public User findByNumber(Integer number) {
        return this.userRepository.findByNumber(number);
    }

    public Integer getMaxUserNumber() {
        Integer result = userRepository.getMaxUserNumber();
        return result != null ? result : 0;
    }

    public User createUser(String name, String username, User.Role student) {

        if (findByUsername(username) != null) {
            throw new TutorException(DUPLICATE_USER, username);
        }
        Calendar calendar = Calendar.getInstance();

        int year = calendar.get(Calendar.YEAR);

        if (calendar.get(Calendar.MONTH) < Calendar.AUGUST) {
            year -= 1;
        }

        User user = new User(name, username, getMaxUserNumber() + 1, year);
        entityManager.persist(user);
        logService.create(user, LocalDateTime.now(), "LOGIN");
        return user;
    }

    public String exportUsers() {
        UsersXmlExport xmlExporter = new UsersXmlExport();

       return xmlExporter.export(userRepository.findAll());
    }


    @Retryable(
      value = { SQLException.class },
      maxAttempts = 3,
      backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void importUsers(String usersXML) {
        UsersXmlImport xmlImporter = new UsersXmlImport();

        xmlImporter.importUsers(usersXML, this);
    }

}
