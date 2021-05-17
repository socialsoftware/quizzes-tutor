package pt.ulisboa.tecnico.socialsoftware.auth.sagas.createUserWithAuth;

import io.eventuate.tram.sagas.orchestration.SagaDefinition;
import io.eventuate.tram.sagas.simpledsl.SimpleSaga;
import org.springframework.util.Assert;
import pt.ulisboa.tecnico.socialsoftware.auth.sagas.participants.AuthUserServiceProxy;
import pt.ulisboa.tecnico.socialsoftware.auth.sagas.participants.UserServiceProxy;
import pt.ulisboa.tecnico.socialsoftware.common.dtos.user.UserDto;

public class CreateUserWithAuthSaga implements SimpleSaga<CreateUserWithAuthSagaData> {
    private SagaDefinition<CreateUserWithAuthSagaData> sagaDefinition;

    public CreateUserWithAuthSaga(AuthUserServiceProxy authUserService, UserServiceProxy userService) {
        this.sagaDefinition =
                step()
                    .withCompensation(authUserService.reject, CreateUserWithAuthSagaData::rejectAuthUser)
                .step()
                    .invokeParticipant(userService.create, CreateUserWithAuthSagaData::createUser)
                    .onReply(UserDto.class, CreateUserWithAuthSagaData::handleCreateUserReply)
                    .withCompensation(userService.reject, CreateUserWithAuthSagaData::rejectUser)
                .step()
                    .invokeParticipant(authUserService.approve, CreateUserWithAuthSagaData::approveAuthUser)
                .build();
    }

    @Override
    public SagaDefinition<CreateUserWithAuthSagaData> getSagaDefinition() {
        Assert.notNull(sagaDefinition, "sagaDefinition is null.");
        return sagaDefinition;
    }

}
