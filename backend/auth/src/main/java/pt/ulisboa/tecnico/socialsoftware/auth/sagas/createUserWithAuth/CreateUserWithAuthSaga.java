package pt.ulisboa.tecnico.socialsoftware.auth.sagas.createUserWithAuth;

import io.eventuate.tram.commands.consumer.CommandWithDestination;
import io.eventuate.tram.sagas.orchestration.SagaDefinition;
import io.eventuate.tram.sagas.simpledsl.SimpleSaga;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import pt.ulisboa.tecnico.socialsoftware.common.commands.auth.ApproveAuthUserCommand;
import pt.ulisboa.tecnico.socialsoftware.common.commands.user.CreateUserCommand;
import pt.ulisboa.tecnico.socialsoftware.common.commands.auth.RejectAuthUserCommand;
import pt.ulisboa.tecnico.socialsoftware.common.commands.user.RejectUserCommand;
import pt.ulisboa.tecnico.socialsoftware.common.dtos.user.UserDto;
import pt.ulisboa.tecnico.socialsoftware.common.serviceChannels.ServiceChannels;

import static io.eventuate.tram.commands.consumer.CommandWithDestinationBuilder.send;

public class CreateUserWithAuthSaga implements SimpleSaga<CreateUserWithAuthSagaData> {
    private SagaDefinition<CreateUserWithAuthSagaData> sagaDefinition;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    public CreateUserWithAuthSaga() {
        this.sagaDefinition =
                step()
                    .withCompensation(this::rejectAuthUser)
                .step()
                    .invokeParticipant(this::createUser)
                    .onReply(UserDto.class, this::handleCreateUserReply)
                    .withCompensation(this::rejectUser)
                .step()
                    .invokeParticipant(this::approveAuthUser)
                .build();
    }

    private CommandWithDestination approveAuthUser(CreateUserWithAuthSagaData data) {
        logger.debug("Sent ApproveAuthUserCommand to authUserService channel");
        return send(new ApproveAuthUserCommand(data.getAuthUserId(), data.getUserId()))
                .to(ServiceChannels.AUTH_USER_SERVICE_COMMAND_CHANNEL).build();
    }

    private CommandWithDestination createUser(CreateUserWithAuthSagaData data) {
        logger.debug("Sent CreateUserCommand to userService channel");
        return send(new CreateUserCommand(data.getName(), data.getRole(), data.getUsername(), data.isActive(), data.isAdmin()))
                .to(ServiceChannels.USER_SERVICE_COMMAND_CHANNEL).build();
    }

    private CommandWithDestination rejectUser(CreateUserWithAuthSagaData data) {
        logger.debug("Sent RejectUserCommand to userService channel");
        return send(new RejectUserCommand(data.getUserId()))
                .to(ServiceChannels.USER_SERVICE_COMMAND_CHANNEL).build();
    }

    private CommandWithDestination rejectAuthUser(CreateUserWithAuthSagaData data) {
        logger.debug("Sent RejectAuthUserCommand to authUserService channel");
        return send(new RejectAuthUserCommand(data.getAuthUserId()))
                .to(ServiceChannels.AUTH_USER_SERVICE_COMMAND_CHANNEL).build();
    }

    private void handleCreateUserReply(CreateUserWithAuthSagaData data, UserDto reply) {
        logger.debug("Received CreateUserReply {}", reply.getId());
        data.setUserId(reply.getId());
    }

    @Override
    public SagaDefinition<CreateUserWithAuthSagaData> getSagaDefinition() {
        Assert.notNull(sagaDefinition, "sagaDefinition is null.");
        return sagaDefinition;
    }

}
