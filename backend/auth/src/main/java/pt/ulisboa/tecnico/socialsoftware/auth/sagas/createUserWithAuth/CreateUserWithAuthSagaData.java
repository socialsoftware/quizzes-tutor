package pt.ulisboa.tecnico.socialsoftware.auth.sagas.createUserWithAuth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pt.ulisboa.tecnico.socialsoftware.auth.command.ApproveAuthUserCommand;
import pt.ulisboa.tecnico.socialsoftware.auth.command.RejectAuthUserCommand;
import pt.ulisboa.tecnico.socialsoftware.common.commands.user.AddCourseExecutionsCommand;
import pt.ulisboa.tecnico.socialsoftware.common.commands.user.CreateUserCommand;
import pt.ulisboa.tecnico.socialsoftware.common.commands.user.RejectUserCommand;
import pt.ulisboa.tecnico.socialsoftware.common.dtos.execution.CourseExecutionDto;
import pt.ulisboa.tecnico.socialsoftware.common.dtos.user.Role;
import pt.ulisboa.tecnico.socialsoftware.common.dtos.user.UserDto;

import java.util.List;

public class CreateUserWithAuthSagaData {

    private final Logger logger = LoggerFactory.getLogger(CreateUserWithAuthSagaData.class);

    private Integer authUserId;
    private String name;
    private Role role;
    private String username;
    private boolean isActive;
    private boolean isAdmin;
    private Integer userId;
    private List<CourseExecutionDto> courseExecutionDtoList;

    public CreateUserWithAuthSagaData() {}

    public CreateUserWithAuthSagaData(Integer authUserId, String name, Role role, String username, boolean isActive,
                                      boolean isAdmin, List<CourseExecutionDto> courseExecutionDtoList) {
        this.authUserId = authUserId;
        this.name = name;
        this.role = role;
        this.username = username;
        this.isActive = isActive;
        this.isAdmin = isAdmin;
        this.courseExecutionDtoList = courseExecutionDtoList;
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

    public List<CourseExecutionDto> getCourseExecutionDtoList() {
        return courseExecutionDtoList;
    }

    RejectUserCommand rejectUser() {
        logger.info("Sent RejectUserCommand");
        return new RejectUserCommand(getUserId());
    }

    ApproveAuthUserCommand approveAuthUser() {
        logger.info("Sent ApproveAuthUserCommand");
        return new ApproveAuthUserCommand(getAuthUserId(), getUserId(), getCourseExecutionDtoList());
    }

    CreateUserCommand createUser() {
        logger.info("Sent CreateUserCommand");
        return new CreateUserCommand(getName(), getRole(), getUsername(), isActive(), isAdmin());
    }

    RejectAuthUserCommand rejectAuthUser() {
        logger.info("Sent RejectAuthUserCommand");
        return new RejectAuthUserCommand(getAuthUserId());
    }

    void handleCreateUserReply(UserDto reply) {
        logger.info("Received CreateUserReply userId: " + reply.getId());
        setUserId(reply.getId());
    }

    AddCourseExecutionsCommand addCourseExecution() {
        logger.info("Sent AddCourseExecutionCommand");
        return new AddCourseExecutionsCommand(getUserId(), getCourseExecutionDtoList());
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
                ", courseExecutionDtoList=" + courseExecutionDtoList +
                '}';
    }
}
