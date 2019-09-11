package pt.ulisboa.tecnico.socialsoftware.tutor.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.UsersXmlExport;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.UsersXmlImport;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Calendar;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @PersistenceContext
    EntityManager entityManager;

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

    @Transactional
    public User create(String name, String username, User.Role role) {
        if (findByUsername(username) != null) {
            throw new TutorException(TutorException.ExceptionError.DUPLICATE_USER, username);
        }
        Calendar calendar = Calendar.getInstance();

        int year = calendar.get(Calendar.YEAR);

        if (calendar.get(Calendar.MONTH) < Calendar.AUGUST) {
            year -= 1;
        }

        User user = new User(name, username, role, getMaxUserNumber() + 1, year);
        entityManager.persist(user);
        return user;
    }

    public String exportUsers() {
        UsersXmlExport xmlExporter = new UsersXmlExport();

       return xmlExporter.export(userRepository.findAll());
    }

    @Transactional
    public void importUsers(String usersXML) {
        UsersXmlImport xmlImporter = new UsersXmlImport();

        xmlImporter.importUsers(usersXML, this);
    }

}
