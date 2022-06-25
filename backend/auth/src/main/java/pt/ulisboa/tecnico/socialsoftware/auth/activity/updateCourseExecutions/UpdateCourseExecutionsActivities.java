package pt.ulisboa.tecnico.socialsoftware.auth.activity.updateCourseExecutions;

import java.util.List;

import pt.ulisboa.tecnico.socialsoftware.common.dtos.execution.CourseExecutionDto;

public interface UpdateCourseExecutionsActivities {

    void undoUpdateCourseExecutions(Integer authUserId);

    void confirmUpdateCourseExecutions(Integer authUserId, String ids,
            List<CourseExecutionDto> courseExecutionDtoList, String email);
}
