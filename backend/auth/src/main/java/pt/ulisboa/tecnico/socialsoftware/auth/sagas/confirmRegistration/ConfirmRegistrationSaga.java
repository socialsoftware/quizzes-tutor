package pt.ulisboa.tecnico.socialsoftware.auth.sagas.confirmRegistration;

import io.eventuate.tram.sagas.orchestration.SagaDefinition;
import io.eventuate.tram.sagas.simpledsl.SimpleSaga;
import org.springframework.util.Assert;
import pt.ulisboa.tecnico.socialsoftware.auth.sagas.participants.AuthUserServiceProxy;
import pt.ulisboa.tecnico.socialsoftware.auth.sagas.participants.UserServiceProxy;

public class ConfirmRegistrationSaga implements SimpleSaga<ConfirmRegistrationSagaData> {
    private SagaDefinition<ConfirmRegistrationSagaData> sagaDefinition;

    public ConfirmRegistrationSaga(AuthUserServiceProxy authUserService, UserServiceProxy userService) {
        this.sagaDefinition =
                step()
                    .invokeParticipant(authUserService.beginConfirmRegistration, ConfirmRegistrationSagaData::beginConfirmRegistration)
                    .withCompensation(authUserService.undoConfirmRegistration, ConfirmRegistrationSagaData::undoConfirmRegistration)
                .step()
                    .invokeParticipant(userService.activateUser, ConfirmRegistrationSagaData::activateUser)
                .step()
                    .invokeParticipant(authUserService.confirmRegistration, ConfirmRegistrationSagaData::confirmRegistration)
                .build();
    }

    @Override
    public SagaDefinition<ConfirmRegistrationSagaData> getSagaDefinition() {
        Assert.notNull(sagaDefinition, "sagaDefinition is null.");
        return sagaDefinition;
    }

    @Override
    public String toString() {
        return "ConfirmRegistrationSaga{" +
                "sagaDefinition=" + sagaDefinition +
                '}';
    }
}
