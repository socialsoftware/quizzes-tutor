package pt.ulisboa.tecnico.socialsoftware.auth.workflow.updateCourseExecutions;

import java.util.List;

import com.uber.cadence.workflow.ActivityException;
import com.uber.cadence.workflow.Saga;
import com.uber.cadence.workflow.Workflow;

import pt.ulisboa.tecnico.socialsoftware.auth.activity.updateCourseExecutions.UpdateCourseExecutionsActivities;
import pt.ulisboa.tecnico.socialsoftware.common.activity.CourseExecutionActivities;
import pt.ulisboa.tecnico.socialsoftware.common.dtos.execution.CourseExecutionDto;

public class UpdateCourseExectionsWorkflowImpl implements UpdateCourseExectionsWorkflow {

    private final UpdateCourseExecutionsActivities updateCourseExecutionsActivities = Workflow
            .newActivityStub(UpdateCourseExecutionsActivities.class);

    private final CourseExecutionActivities courseExecutionActivities = Workflow
            .newActivityStub(CourseExecutionActivities.class);

    @Override
    public void updateCourseExecutions(Integer authUserId, Integer userId, String ids,
            List<CourseExecutionDto> courseExecutionDtoList, String email) {
        Saga.Options sagaOptions = new Saga.Options.Builder()
                .setParallelCompensation(false)
                .build();
        Saga saga = new Saga(sagaOptions);
        try {
            // Step 1
            saga.addCompensation(updateCourseExecutionsActivities::undoUpdateCourseExecutions, authUserId);

            // Step 2
            courseExecutionActivities.addCourseExecutions(userId, courseExecutionDtoList);

            // Step 3
            updateCourseExecutionsActivities.confirmUpdateCourseExecutions(authUserId, ids, courseExecutionDtoList,
                    email);
        } catch (ActivityException e) {
            saga.compensate();
            throw e;
        }

    }

}
