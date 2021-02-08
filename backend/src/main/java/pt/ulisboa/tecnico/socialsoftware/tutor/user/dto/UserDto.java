package pt.ulisboa.tecnico.socialsoftware.tutor.user.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import pt.ulisboa.tecnico.socialsoftware.tutor.utils.DateHandler;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.User;

import java.io.Serializable;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        defaultImpl = StudentDto.class,
        property = "role")
@JsonSubTypes({
        @JsonSubTypes.Type(value = StudentDto.class, name = "STUDENT")
})
public class UserDto implements Serializable {
    private Integer id;
    private String username;
    private String email;
    private String name;
    private String role;
    private boolean active;
    private String creationDate;
    private String lastAccess;

    public UserDto() { }

    public UserDto(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.email = user.getAuthUser().getEmail();
        this.name = user.getName();
        this.role = user.getRole().toString();
        this.active = user.getAuthUser().isActive();
        this.creationDate = DateHandler.toISOString(user.getCreationDate());
        this.lastAccess = DateHandler.toISOString(user.getAuthUser().getLastAccess());
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

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
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public String getLastAccess() {
        return lastAccess;
    }

    public void setLastAccess(String lastAccess) {
        this.lastAccess = lastAccess;
    }
}