package pt.ulisboa.tecnico.socialsoftware.tutor.user.domain;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.DomainEntity;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.Visitor;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserService;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*;

@Entity
@Table(name = "auth_users",
        indexes = {
                @Index(name = "auth_users_indx_0", columnList = "username")
        })
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="auth_type",
        discriminatorType = DiscriminatorType.STRING)
public abstract class AuthUser implements DomainEntity, UserDetails {
    public enum Type { EXTERNAL, TECNICO, DEMO }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne
    private User user;

    private String email;
    private String password;

    @Column(unique=true)
    private String username;

    private String confirmationToken = "";

    private LocalDateTime tokenGenerationDate;

    @Column(name = "last_access")
    private LocalDateTime lastAccess;

    public AuthUser() {}

    public AuthUser(User user, String username, String email) {
        setUser(user);
        setUsername(username);
        setEmail(email);
    }

    public static AuthUser createAuthUser(User user, String username, String email, Type type) {
        switch (type) {
            case EXTERNAL:
                return new AuthExternalUser(user, username, email);
            case TECNICO:
                return new AuthTecnicoUser(user, username, email);
            case DEMO:
                return new AuthDemoUser(user, username, email);
            default:
                throw new TutorException(INVALID_TYPE_FOR_AUTH_USER);
        }
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

    @Override
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        if (email == null || !email.matches(UserService.MAIL_FORMAT))
            throw new TutorException(INVALID_EMAIL, email);

        this.email = email;
    }

    @Override
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmationToken() {
        return confirmationToken;
    }

    public void setConfirmationToken(String confirmationToken) {
        this.confirmationToken = confirmationToken;
    }

    public LocalDateTime getTokenGenerationDate() {
        return tokenGenerationDate;
    }

    public void setTokenGenerationDate(LocalDateTime tokenGenerationDate) {
        this.tokenGenerationDate = tokenGenerationDate;
    }

    public LocalDateTime getLastAccess() {
        return lastAccess;
    }

    public void setLastAccess(LocalDateTime lastAccess) {
        this.lastAccess = lastAccess;
    }

    public boolean isActive() {
        return true;
    }

    public abstract Type getType();

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
        if (isActive()) {
            throw new TutorException(USER_ALREADY_ACTIVE, getUsername());
        }
        if (!token.equals(getConfirmationToken()))
            throw new TutorException(INVALID_CONFIRMATION_TOKEN);
        if (getTokenGenerationDate().isBefore(LocalDateTime.now().minusDays(1)))
            throw new TutorException(EXPIRED_CONFIRMATION_TOKEN);
    }
 
    public boolean isGenerated() {
        return false;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> list = new ArrayList<>();

        list.add(new SimpleGrantedAuthority("ROLE_" + user.getRole()));

        if (user.isAdmin())
            list.add(new SimpleGrantedAuthority("ROLE_ADMIN"));

        return list;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public void remove() {
        user.setAuthUser(null);
    }
}