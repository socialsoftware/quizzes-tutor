package pt.ulisboa.tecnico.socialsoftware.auth.sagas.createUserWithAuth;

import io.eventuate.tram.commands.consumer.CommandWithDestination;
import io.eventuate.tram.sagas.orchestration.SagaDefinition;
import io.eventuate.tram.sagas.simpledsl.SimpleSaga;
import org.springframework.util.Assert;
import pt.ulisboa.tecnico.socialsoftware.auth.sagas.participants.AuthUserServiceProxy;
import pt.ulisboa.tecnico.socialsoftware.auth.sagas.participants.UserServiceProxy;
import pt.ulisboa.tecnico.socialsoftware.auth.sagas.participants.CourseExecutionServiceProxy;
import pt.ulisboa.tecnico.socialsoftware.common.commands.auth.ApproveAuthUserCommand;
import pt.ulisboa.tecnico.socialsoftware.common.dtos.user.UserDto;
import pt.ulisboa.tecnico.socialsoftware.common.serviceChannels.ServiceChannels;
import static io.eventuate.tram.commands.consumer.CommandWithDestinationBuilder.send;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CreateUserWithAuthSaga implements SimpleSaga<CreateUserWithAuthSagaData> {
    private SagaDefinition<CreateUserWithAuthSagaData> sagaDefinition;

    private Logger logger = LoggerFactory.getLogger(getClass());

    public CreateUserWithAuthSaga(AuthUserServiceProxy authUserService, UserServiceProxy userService, CourseExecutionServiceProxy courseExecutionService) {
        this.sagaDefinition =
                step()
                    .withCompensation(authUserService.reject, CreateUserWithAuthSagaData::rejectAuthUser)
                .step()
                    .invokeParticipant(userService.create, CreateUserWithAuthSagaData::createUser)
                    .onReply(UserDto.class, CreateUserWithAuthSagaData::handleCreateUserReply)
                    .withCompensation(userService.reject, CreateUserWithAuthSagaData::rejectUser)
                .step()
                    .invokeParticipant(courseExecutionService.addCourseExecution, CreateUserWithAuthSagaData::addCourseExecution)
                    .withCompensation(courseExecutionService.removeCourseExecution, CreateUserWithAuthSagaData::removeCourseExecution)
                .step()
                    //.invokeParticipant(authUserService.approve, CreateUserWithAuthSagaData::approveAuthUser)
                    .invokeParticipant(this::approveAuthUser)
                .build();
    }

    @Override
    public SagaDefinition<CreateUserWithAuthSagaData> getSagaDefinition() {
        Assert.notNull(sagaDefinition, "sagaDefinition is null.");
        return sagaDefinition;
    }

    /*private CommandWithDestination rejectOrder(CreateOrderSagaData data) {
        logger.debug("Send RejectOrderCommand to orderService channel");
        return send(new RejectOrderCommand(data.getOrderId()))
                .to(OrderServiceChannels.ORDER_SERVICE_COMMAND_CHANNEL).build();
    }*/

    private CommandWithDestination approveAuthUser(CreateUserWithAuthSagaData data) {
        logger.info("Sent ApproveAuthUserCommand to authUserService channel");
        return send(new ApproveAuthUserCommand(data.getAuthUserId(), data.getUserId()))
                .to(ServiceChannels.AUTH_USER_SERVICE_COMMAND_CHANNEL).build();
    }

}
