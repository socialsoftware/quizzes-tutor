package pt.ulisboa.tecnico.socialsoftware.auth.domain;

import org.springframework.security.crypto.keygen.KeyGenerators;
import pt.ulisboa.tecnico.socialsoftware.common.dtos.auth.AuthUserType;
import pt.ulisboa.tecnico.socialsoftware.common.dtos.execution.CourseExecutionDto;
import pt.ulisboa.tecnico.socialsoftware.common.dtos.user.ExternalUserDto;
import pt.ulisboa.tecnico.socialsoftware.common.exceptions.UnsupportedStateTransitionException;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.time.LocalDateTime;
import java.util.List;

import static pt.ulisboa.tecnico.socialsoftware.auth.domain.AuthUserState.APPROVED;
import static pt.ulisboa.tecnico.socialsoftware.auth.domain.AuthUserState.UPDATE_PENDING;

@Entity
@DiscriminatorValue("EXTERNAL")
public class AuthExternalUser extends AuthUser {

    @Column(columnDefinition = "boolean default false")
    private Boolean active;

    private String confirmationToken = "";

    private LocalDateTime tokenGenerationDate;

    public AuthExternalUser() {}

    public AuthExternalUser(UserSecurityInfo userSecurityInfo, String username, String email) {
        super(userSecurityInfo, username, email);
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

    public void confirmRegistration(String password) {
        setPassword(password);
        setActive(true);
    }

    @Override
    public AuthUserType getType() {return AuthUserType.EXTERNAL;}

    public void generateConfirmationToken() {
        String token = KeyGenerators.string().generateKey();
        setTokenGenerationDate(LocalDateTime.now());
        setConfirmationToken(token);
    }

    public ExternalUserDto getDto() {
        ExternalUserDto dto = new ExternalUserDto();
        dto.setId(getId());
        dto.setName(getUserSecurityInfo().getName());
        dto.setUsername(getUsername());
        dto.setEmail(getEmail());
        dto.setPassword(getPassword());
        dto.setRole(getUserSecurityInfo().getRole());
        dto.setActive(isActive());
        dto.setAdmin(getUserSecurityInfo().isAdmin());
        dto.setConfirmationToken(getConfirmationToken());
        return dto;
    }

    public void authUserBeginConfirmRegistration() {
        switch (getState()) {
            case READY_FOR_UPDATE:
                setState(UPDATE_PENDING);
                break;
            default:
                throw new UnsupportedStateTransitionException(getState());
        }
    }

    public void authUserUndoConfirmRegistration() {
        switch (getState()) {
            case UPDATE_PENDING:
                setState(APPROVED);
                break;
            default:
                throw new UnsupportedStateTransitionException(getState());
        }
    }


    public void authUserConfirmRegistration(String password) {
        switch (getState()) {
            case UPDATE_PENDING:
                confirmRegistration(password);
                setState(APPROVED);
                break;
            default:
                throw new UnsupportedStateTransitionException(getState());
        }
    }

    public void authUserApproved(Integer userId, List<CourseExecutionDto> courseExecutionList) {
        switch (getState()) {
            case APPROVAL_PENDING:
                generateConfirmationToken();
                super.authUserApproved(userId, courseExecutionList);
                break;
            default:
                throw new UnsupportedStateTransitionException(getState());
        }
    }
}
