package pt.ulisboa.tecnico.socialsoftware.auth.sagas.createUserWithAuth;

import pt.ulisboa.tecnico.socialsoftware.common.dtos.user.Role;

public class CreateUserWithAuthSagaData {

    private Integer authUserId;
    private String name;
    private Role role;
    private String username;
    private boolean isActive;
    private boolean isAdmin;
    private Integer userId;

    public CreateUserWithAuthSagaData() {}

    public CreateUserWithAuthSagaData(Integer authUserId, String name, Role role, String username, boolean isActive, boolean isAdmin) {
        this.authUserId = authUserId;
        this.name = name;
        this.role = role;
        this.username = username;
        this.isActive = isActive;
        this.isAdmin = isAdmin;
    }

    public String getName() {
        return name;
    }

    public Role getRole() {
        return role;
    }

    public String getUsername() {
        return username;
    }

    public boolean isActive() {
        return isActive;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getAuthUserId() {
        return authUserId;
    }
}
