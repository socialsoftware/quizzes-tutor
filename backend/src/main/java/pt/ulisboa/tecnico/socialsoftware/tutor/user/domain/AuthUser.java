package pt.ulisboa.tecnico.socialsoftware.tutor.user.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.DomainEntity;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.Visitor;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserService;

import javax.persistence.*;
import java.time.LocalDateTime;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*;

@Entity
@Table(name = "auth_users")
public class AuthUser implements DomainEntity {
    public enum Type { EXTERNAL, TECNICO}

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne
    private User user;

    @Enumerated(EnumType.STRING)
    private Type type;

    private String email;

    public AuthUser() {}

    public AuthUser(User user) {
        setUser(user);
    }

    public AuthUser(User user, String username, String email, Type type, Boolean isActive) {
        setUser(user);
        setUsername(username);
        setEmail(email);
        setType(type);
        setActive(isActive);
        checkRole(isActive);
    }

    public AuthUser(User user, String username, String email, Type type, Boolean isActive, String password) {
        this(user, username, email, type, isActive);
        setPassword(password);
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
        this.user.setAuthUser(this);
    }

    public String getUsername() {
        return user.getUsername();
    }

    public void setUsername(String username) {
        user.setUsername(username);
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        if (email == null || !email.matches(UserService.MAIL_FORMAT))
            throw new TutorException(INVALID_EMAIL, email);

        this.email = email;
    }

    public String getPassword() {
        return user.getPassword();
    }

    public void setPassword(String password) {
        user.setPassword(password);
    }

    public String getConfirmationToken() {
        return user.getConfirmationToken();
    }

    public void setConfirmationToken(String confirmationToken) {
        user.setConfirmationToken(confirmationToken);
    }

    public LocalDateTime getTokenGenerationDate() {
        return user.getTokenGenerationDate();
    }

    public void setTokenGenerationDate(LocalDateTime tokenGenerationDate) {
        user.setTokenGenerationDate(tokenGenerationDate);
    }

    public LocalDateTime getLastAccess() {
        return user.getLastAccess();
    }

    public void setLastAccess(LocalDateTime lastAccess) {
        user.setLastAccess(lastAccess);
    }

    public boolean isActive() {
        return user.isActive();
    }

    public void setActive(boolean active) {
        user.setActive(active);
    }

    public String getEnrolledCoursesAcronyms() {
        return user.getEnrolledCoursesAcronyms();
    }

    public void setEnrolledCoursesAcronyms(String enrolledCoursesAcronyms) {
        user.setEnrolledCoursesAcronyms(enrolledCoursesAcronyms);
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }


    public void checkRole(boolean isActive) {
        if (!isActive && !(user.getRole().equals(User.Role.STUDENT) || user.getRole().equals(User.Role.TEACHER))) {
            throw new TutorException(INVALID_ROLE, user.getRole().toString());
        }
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visitAuthUser(this);
    }

    public void checkConfirmationToken(String token) {
        if (!token.equals(getConfirmationToken()))
            throw new TutorException(INVALID_CONFIRMATION_TOKEN);
        if (getTokenGenerationDate().isBefore(LocalDateTime.now().minusDays(1)))
            throw new TutorException(EXPIRED_CONFIRMATION_TOKEN);
    }
}