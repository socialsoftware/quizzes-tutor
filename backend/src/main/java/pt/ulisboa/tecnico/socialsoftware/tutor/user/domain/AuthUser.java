package pt.ulisboa.tecnico.socialsoftware.tutor.user.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.DomainEntity;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.Visitor;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;

import javax.persistence.*;
import java.time.LocalDateTime;

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

    public AuthUser() {}

    public AuthUser(User user) {
        setUser(user);
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
        return user.getEmail();
    }

    public void setEmail(String email) {
        user.setEmail(email);
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

    public LocalDateTime getTokenGeneratioDate() {
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

    public void checkConfirmationToken(String token) {
        user.checkConfirmationToken(token);
    }

        @Override
    public void accept(Visitor visitor) {
        visitor.visitAuthUser(this);
    }

}
