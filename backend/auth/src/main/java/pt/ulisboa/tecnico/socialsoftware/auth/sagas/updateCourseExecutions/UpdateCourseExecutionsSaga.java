package pt.ulisboa.tecnico.socialsoftware.auth.sagas.updateCourseExecutions;

import io.eventuate.tram.sagas.orchestration.SagaDefinition;
import io.eventuate.tram.sagas.simpledsl.SimpleSaga;
import org.springframework.util.Assert;
import pt.ulisboa.tecnico.socialsoftware.auth.sagas.participants.AuthUserServiceProxy;
import pt.ulisboa.tecnico.socialsoftware.auth.sagas.participants.UserServiceProxy;

public class UpdateCourseExecutionsSaga implements SimpleSaga<UpdateCourseExecutionsSagaData> {
    private SagaDefinition<UpdateCourseExecutionsSagaData> sagaDefinition;

    public UpdateCourseExecutionsSaga(AuthUserServiceProxy authUserService, UserServiceProxy userService) {
        this.sagaDefinition =
                step()
                    .invokeParticipant(authUserService.beginUpdateCourseExecutions, UpdateCourseExecutionsSagaData::beginUpdateCourseExecutions)
                    .withCompensation(authUserService.undoUpdateCourseExecutions, UpdateCourseExecutionsSagaData::undoUpdateCourseExecutions)
                .step()
                    .invokeParticipant(userService.addCourseExecution, UpdateCourseExecutionsSagaData::addCourseExecution)
                    //.withCompensation(userService.removeCourseExecution, UpdateCourseExecutionsSagaData::removeCourseExecution)
                .step()
                    .invokeParticipant(authUserService.confirmUpdateCourseExecutions, UpdateCourseExecutionsSagaData::confirmUpdateCourseExecutions)
                .build();
    }

    @Override
    public SagaDefinition<UpdateCourseExecutionsSagaData> getSagaDefinition() {
        Assert.notNull(sagaDefinition, "sagaDefinition is null.");
        return sagaDefinition;
    }

    @Override
    public String toString() {
        return "UpdateCourseExecutionsSaga{" +
                "sagaDefinition=" + sagaDefinition +
                '}';
    }
}
