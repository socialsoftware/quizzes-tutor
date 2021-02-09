package pt.ulisboa.tecnico.socialsoftware.tutor.user.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.execution.dto.CourseExecutionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.User;
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.domain.AuthExternalUser;

import java.io.Serializable;
import java.util.List;

public class ExternalUserDto implements Serializable {
    private Integer key;
    private Integer id;
    private String name;
    private String username;
    private String email;
    private String password;
    private User.Role role;
    private boolean active;
    private boolean isAdmin;
    private List<CourseExecutionDto> courseExecutions;
    private String confirmationToken;

    public ExternalUserDto(){ }

    public ExternalUserDto(User user){
        this.key = user.getKey();
        this.id = user.getId();
        this.name = user.getName();
        this.username = user.getUsername();
        this.email = user.getAuthUser().getEmail();
        this.password = user.getAuthUser().getPassword();
        this.role = user.getRole();
        this.active = user.getAuthUser().isActive();
        this.isAdmin = user.isAdmin();
        this.confirmationToken = ((AuthExternalUser)user.getAuthUser()).getConfirmationToken();
    }

    public ExternalUserDto(AuthExternalUser authUser){
        this.key = authUser.getUser().getKey();
        this.id = authUser.getId();
        this.name = authUser.getUser().getName();
        this.username = authUser.getUsername();
        this.email = authUser.getEmail();
        this.password = authUser.getPassword();
        this.role = authUser.getUser().getRole();
        this.active = authUser.isActive();
        this.isAdmin = authUser.getUser().isAdmin();
        this.confirmationToken = authUser.getConfirmationToken();
    }

    public Integer getKey() {
        return key;
    }

    public void setKey(Integer key) {
        this.key = key;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public User.Role getRole() {
        return role;
    }

    public void setRole(User.Role role) {
        this.role = role;
    }

    public boolean getActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isActive() {
        return this.active;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    public List<CourseExecutionDto> getCourseExecutions() {
        return courseExecutions;
    }

    public void setCourseExecutions(List<CourseExecutionDto> courseExecutions) {
        this.courseExecutions = courseExecutions;
    }

    public String getConfirmationToken() {
        return confirmationToken;
    }

    public void setConfirmationToken(String confirmationToken) {
        this.confirmationToken = confirmationToken;
    }
}
