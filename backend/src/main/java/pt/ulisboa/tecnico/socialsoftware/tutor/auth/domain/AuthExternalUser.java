package pt.ulisboa.tecnico.socialsoftware.tutor.auth.domain;

import org.springframework.security.crypto.keygen.KeyGenerators;
import org.springframework.security.crypto.password.PasswordEncoder;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.User;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.time.LocalDateTime;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*;


@Entity
@DiscriminatorValue("EXTERNAL")
public class AuthExternalUser extends AuthUser {

    @Column(columnDefinition = "boolean default false")
    private Boolean active;

    private String confirmationToken = "";

    private LocalDateTime tokenGenerationDate;

    public AuthExternalUser() {}

    public AuthExternalUser(User user, String username, String email) {
        super(user, username, email);
        setActive(false);
        checkRole(isActive());
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public boolean isActive() {
        return active;
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

    public void confirmRegistration(PasswordEncoder passwordEncoder, String confirmationToken, String password) {
        checkConfirmationToken(confirmationToken);
        setPassword(passwordEncoder.encode(password));
        setActive(true);
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

    @Override
    public Type getType() {return Type.EXTERNAL;}

    public String generateConfirmationToken() {
        String token = KeyGenerators.string().generateKey();
        setTokenGenerationDate(LocalDateTime.now());
        setConfirmationToken(token);
        return token;
    }
}
