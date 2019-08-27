package pt.ulisboa.tecnico.socialsoftware.tutor.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.UsersXmlExport;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.UsersXmlImport;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.User;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.repository.UserRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

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

        User user = new User(name, username, role, getMaxUserNumber() + 1);
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
