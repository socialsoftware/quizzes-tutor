package pt.ulisboa.tecnico.socialsoftware.auth.sagas.createUserWithAuth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pt.ulisboa.tecnico.socialsoftware.common.commands.auth.ApproveAuthUserCommand;
import pt.ulisboa.tecnico.socialsoftware.common.commands.auth.RejectAuthUserCommand;
import pt.ulisboa.tecnico.socialsoftware.common.commands.execution.AddCourseExecutionCommand;
import pt.ulisboa.tecnico.socialsoftware.common.commands.execution.RemoveCourseExecutionCommand;
import pt.ulisboa.tecnico.socialsoftware.common.commands.user.CreateUserCommand;
import pt.ulisboa.tecnico.socialsoftware.common.commands.user.RejectUserCommand;
import pt.ulisboa.tecnico.socialsoftware.common.dtos.user.Role;
import pt.ulisboa.tecnico.socialsoftware.common.dtos.user.UserDto;

import java.util.Objects;

public class CreateUserWithAuthSagaData {

    private final Logger logger = LoggerFactory.getLogger(CreateUserWithAuthSagaData.class);

    private Integer authUserId;
    private String name;
    private Role role;
    private String username;
    private boolean isActive;
    private boolean isAdmin;
    private Integer userId;
    private Integer courseExecutionId;

    public CreateUserWithAuthSagaData() {}

    public CreateUserWithAuthSagaData(Integer authUserId, String name, Role role, String username, boolean isActive,
                                      boolean isAdmin, Integer courseExecutionId) {
        this.authUserId = authUserId;
        this.name = name;
        this.role = role;
        this.username = username;
        this.isActive = isActive;
        this.isAdmin = isAdmin;
        this.courseExecutionId = courseExecutionId;
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

    public Integer getCourseExecutionId() {
        return courseExecutionId;
    }

    RejectUserCommand rejectUser() {
        logger.info("Sent RejectUserCommand to userService channel");
        return new RejectUserCommand(getUserId());
    }

    ApproveAuthUserCommand approveAuthUser() {
        logger.info("Sent ApproveAuthUserCommand to authUserService channel");
        return new ApproveAuthUserCommand(getAuthUserId(), getUserId(), getCourseExecutionId(), isActive());
    }

    CreateUserCommand createUser() {
        logger.info("Sent CreateUserCommand to userService channel");
        return new CreateUserCommand(getName(), getRole(), getUsername(), isActive(), isAdmin());
    }

    RejectAuthUserCommand rejectAuthUser() {
        logger.info("Sent RejectAuthUserCommand to authUserService channel");
        return new RejectAuthUserCommand(getAuthUserId());
    }

    void handleCreateUserReply(UserDto reply) {
        logger.info("Received CreateUserReply userId: " + reply.getId());
        setUserId(reply.getId());
    }

    AddCourseExecutionCommand addCourseExecution() {
        logger.info("Sent AddCourseExecutionCommand to courseExecutionService channel");
        return new AddCourseExecutionCommand(getUserId(), getCourseExecutionId());
    }

    RemoveCourseExecutionCommand removeCourseExecution() {
        logger.info("Sent RemoveCourseExecutionCommand to courseExecutionService channel");
        return new RemoveCourseExecutionCommand(getUserId(), getCourseExecutionId());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CreateUserWithAuthSagaData that = (CreateUserWithAuthSagaData) o;
        return isActive == that.isActive && isAdmin == that.isAdmin && authUserId.equals(that.authUserId) && name.equals(that.name) && role == that.role && username.equals(that.username) && userId.equals(that.userId) && courseExecutionId.equals(that.courseExecutionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, role, username, isActive, isAdmin, userId, courseExecutionId);
    }

    @Override
    public String toString() {
        return "CreateUserWithAuthSagaData{" +
                "authUserId=" + authUserId +
                ", name='" + name + '\'' +
                ", role=" + role +
                ", username='" + username + '\'' +
                ", isActive=" + isActive +
                ", isAdmin=" + isAdmin +
                ", userId=" + userId +
                ", courseExecutionId=" + courseExecutionId +
                '}';
    }
}
