package pt.ulisboa.tecnico.socialsoftware.auth.sagas.updateCourseExecutions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pt.ulisboa.tecnico.socialsoftware.auth.command.BeginUpdateCourseExecutionsCommand;
import pt.ulisboa.tecnico.socialsoftware.auth.command.ConfirmUpdateCourseExecutionsCommand;
import pt.ulisboa.tecnico.socialsoftware.auth.command.UndoUpdateCourseExecutionsCommand;
import pt.ulisboa.tecnico.socialsoftware.common.commands.user.AddCourseExecutionsCommand;
import pt.ulisboa.tecnico.socialsoftware.common.commands.user.RemoveCourseExecutionsCommand;
import pt.ulisboa.tecnico.socialsoftware.common.dtos.execution.CourseExecutionDto;

import java.util.List;

public class UpdateCourseExecutionsSagaData {

    private final Logger logger = LoggerFactory.getLogger(UpdateCourseExecutionsSagaData.class);

    private Integer authUserId;
    private Integer userId;
    private String ids;
    private List<CourseExecutionDto> courseExecutionDtoList;
    private String email;

    public UpdateCourseExecutionsSagaData() {
    }

    public UpdateCourseExecutionsSagaData(Integer authUserId, Integer userId, String ids, List<CourseExecutionDto> courseExecutionDtoList, String email) {
        this.authUserId = authUserId;
        this.userId = userId;
        this.ids = ids;
        this.courseExecutionDtoList = courseExecutionDtoList;
        this.email = email;
    }

    public Integer getAuthUserId() {
        return authUserId;
    }

    public Integer getUserId() {
        return userId;
    }

    public String getIds() {
        return ids;
    }

    public List<CourseExecutionDto> getCourseExecutionDtoList() {
        return courseExecutionDtoList;
    }

    public String getEmail() {
        return email;
    }

    BeginUpdateCourseExecutionsCommand beginUpdateCourseExecutions() {
        logger.info("Sent BeginUpdateCourseExecutionsCommand");
        return new BeginUpdateCourseExecutionsCommand(getAuthUserId());
    }

    UndoUpdateCourseExecutionsCommand undoUpdateCourseExecutions() {
        logger.info("Sent UndoUpdateCourseExecutionsCommand");
        return new UndoUpdateCourseExecutionsCommand(getAuthUserId());
    }

    AddCourseExecutionsCommand addCourseExecution() {
        logger.info("Sent AddCourseExecutionsCommand");
        return new AddCourseExecutionsCommand(getUserId(), getCourseExecutionDtoList());
    }

    RemoveCourseExecutionsCommand removeCourseExecution() {
        logger.info("Sent RemoveCourseExecutionsCommand");
        return new RemoveCourseExecutionsCommand(getUserId(), getCourseExecutionDtoList());
    }

    ConfirmUpdateCourseExecutionsCommand confirmUpdateCourseExecutions() {
        logger.info("Sent ConfirmUpdateCourseExecutionsCommand");
        return new ConfirmUpdateCourseExecutionsCommand(getAuthUserId(), getIds(), getCourseExecutionDtoList(), getEmail());
    }
}
