package pt.ulisboa.tecnico.socialsoftware.auth.subscriptions;

import io.eventuate.tram.events.subscriber.DomainEventEnvelope;
import io.eventuate.tram.events.subscriber.DomainEventHandlers;
import io.eventuate.tram.events.subscriber.DomainEventHandlersBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pt.ulisboa.tecnico.socialsoftware.auth.domain.AuthUser;
import pt.ulisboa.tecnico.socialsoftware.auth.domain.AuthUserState;
import pt.ulisboa.tecnico.socialsoftware.auth.repository.AuthUserRepository;
import pt.ulisboa.tecnico.socialsoftware.common.events.DeleteAuthUserEvent;
import pt.ulisboa.tecnico.socialsoftware.common.events.RemoveUserFromTecnicoCourseExecutionEvent;

import static pt.ulisboa.tecnico.socialsoftware.common.events.EventAggregateTypes.USER_AGGREGATE_TYPE;

@Component
public class AuthUserUserSubscriptions {
    private static final Logger logger = LoggerFactory.getLogger(AuthUserUserSubscriptions.class);

    @Autowired
    private AuthUserRepository authUserRepository;

    public DomainEventHandlers domainEventHandlers() {
        return DomainEventHandlersBuilder
                .forAggregateType(USER_AGGREGATE_TYPE)
                .onEvent(DeleteAuthUserEvent.class, this::deleteAuthUser)
                .onEvent(RemoveUserFromTecnicoCourseExecutionEvent.class, this::removeUserFromCourseExecution)
                .build();
    }

    public void removeUserFromCourseExecution(DomainEventEnvelope<RemoveUserFromTecnicoCourseExecutionEvent> event) {
        logger.info("Received removeUserFromCourseExecution event!");
        RemoveUserFromTecnicoCourseExecutionEvent removeUserFromTecnicoCourseExecutionEvent = event.getEvent();
        AuthUser authUser = authUserRepository.findAuthUserById(removeUserFromTecnicoCourseExecutionEvent.getUserId())
                .get();

        authUser.getUserCourseExecutions().remove(removeUserFromTecnicoCourseExecutionEvent.getCourseExecutionId());
    }

    public void deleteAuthUser(DomainEventEnvelope<DeleteAuthUserEvent> event) {
        logger.info("Received deleteAuthUser event!");
        DeleteAuthUserEvent deleteAuthUserEvent = event.getEvent();
        AuthUser authUser = authUserRepository.findAuthUserById(deleteAuthUserEvent.getUserId())
                // Does not throw exception because when we anonymize users,
                // events are sent even if authUser does not exist
                .orElse(null);

        if (authUser != null) {
            if (authUser.getState().equals(AuthUserState.APPROVED)) {
                authUser.remove();
                authUserRepository.delete(authUser);
            }
            else {
                authUser.setState(AuthUserState.REJECTED);
            }
        }
    }
}
