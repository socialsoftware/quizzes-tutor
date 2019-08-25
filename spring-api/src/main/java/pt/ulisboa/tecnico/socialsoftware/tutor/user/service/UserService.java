package pt.ulisboa.tecnico.socialsoftware.tutor.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.UsersXmlExport;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.UsersXmlImport;
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

    @Transactional
    public User create(String name, String username, User.Role role) {
        if (findByUsername(username) != null) {
            throw new TutorException(TutorException.ExceptionError.DUPLICATE_USER, username);
        }

        User user = new User(name, username, role);
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
