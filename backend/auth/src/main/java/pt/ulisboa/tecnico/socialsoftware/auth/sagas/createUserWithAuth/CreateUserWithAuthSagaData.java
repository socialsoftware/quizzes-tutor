package pt.ulisboa.tecnico.socialsoftware.auth.sagas.createUserWithAuth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pt.ulisboa.tecnico.socialsoftware.common.commands.auth.ApproveAuthUserCommand;
import pt.ulisboa.tecnico.socialsoftware.common.commands.auth.RejectAuthUserCommand;
import pt.ulisboa.tecnico.socialsoftware.common.commands.user.CreateUserCommand;
import pt.ulisboa.tecnico.socialsoftware.common.commands.user.RejectUserCommand;
import pt.ulisboa.tecnico.socialsoftware.common.dtos.user.Role;
import pt.ulisboa.tecnico.socialsoftware.common.dtos.user.UserDto;

public class CreateUserWithAuthSagaData {

    private final Logger logger = LoggerFactory.getLogger(CreateUserWithAuthSagaData.class);

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

    RejectUserCommand rejectUser() {
        System.out.println("Sent RejectUserCommand to userService channel");
        return new RejectUserCommand(getUserId());
    }

    ApproveAuthUserCommand approveAuthUser() {
        System.out.println("Sent ApproveAuthUserCommand to authUserService channel");
        return new ApproveAuthUserCommand(getAuthUserId(), getUserId());
    }

    CreateUserCommand createUser() {
        System.out.println("Sent CreateUserCommand to userService channel");
        return new CreateUserCommand(getName(), getRole(), getUsername(), isActive(), isAdmin());
    }

    RejectAuthUserCommand rejectAuthUser() {
        System.out.println("Sent RejectAuthUserCommand to authUserService channel");
        return new RejectAuthUserCommand(getAuthUserId());
    }

    void handleCreateUserReply(UserDto reply) {
        System.out.println("Received CreateUserReply userId: " + reply.getId());
        setUserId(reply.getId());
    }
}
