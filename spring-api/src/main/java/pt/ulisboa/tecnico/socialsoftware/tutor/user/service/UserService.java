package pt.ulisboa.tecnico.socialsoftware.tutor.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.User;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.repository.UserRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @PersistenceContext
    EntityManager entityManager;

    public List<User> getUsers() {
        return userRepository.findAll();
    }

    @Transactional
    public User create(String name, String username, User.Role role) {
        User user = new User(name, username, role);
        entityManager.persist(user);
        return user;
    }

    public User findByUsername(String username) {
        return this.userRepository.findByUsername(username);
    }
}
