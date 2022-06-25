package pt.ulisboa.tecnico.socialsoftware.auth.workflow.createUserWithAuth;

import java.util.List;

import com.uber.cadence.workflow.ActivityException;
import com.uber.cadence.workflow.Saga;
import com.uber.cadence.workflow.Workflow;

import pt.ulisboa.tecnico.socialsoftware.auth.activity.createUserWithAuth.CreateUserWithAuthActivities;
import pt.ulisboa.tecnico.socialsoftware.common.activity.CourseExecutionActivities;
import pt.ulisboa.tecnico.socialsoftware.common.activity.UserActivities;
import pt.ulisboa.tecnico.socialsoftware.common.dtos.execution.CourseExecutionDto;
import pt.ulisboa.tecnico.socialsoftware.common.dtos.user.Role;
import pt.ulisboa.tecnico.socialsoftware.common.dtos.user.UserDto;

public class CreateUserWithAuthWorkflowImpl implements CreateUserWithAuthWorkflow {

    private final CreateUserWithAuthActivities createUserWithAuthActivities = Workflow
            .newActivityStub(CreateUserWithAuthActivities.class);

    private final UserActivities userActivities = Workflow.newActivityStub(UserActivities.class);

    private final CourseExecutionActivities courseExecutionActivities = Workflow
            .newActivityStub(CourseExecutionActivities.class);

    @Override
    public void createUserWithAuth(Integer authUserId, String name, Role role, String username, boolean isActive,
            List<CourseExecutionDto> courseExecutionDtoList) {
        Saga.Options sagaOptions = new Saga.Options.Builder()
                .setParallelCompensation(false)
                .build();
        Saga saga = new Saga(sagaOptions);
        try {
            // Step 1
            saga.addCompensation(createUserWithAuthActivities::rejectAuthUser, authUserId);

            // Step 2
            UserDto userDto = userActivities.createUser(name, role, username, isActive);

            saga.addCompensation(userActivities::rejectUser, userDto.getId());

            // Step 3
            courseExecutionActivities.addCourseExecutions(userDto.getId(), courseExecutionDtoList);

            // Step 4
            createUserWithAuthActivities.approveAuthUser(authUserId, userDto.getId(), courseExecutionDtoList);
        } catch (ActivityException e) {
            saga.compensate();
            throw e;
        }
    }

}
