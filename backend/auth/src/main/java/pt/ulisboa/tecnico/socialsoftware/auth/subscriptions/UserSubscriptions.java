package pt.ulisboa.tecnico.socialsoftware.auth.subscriptions;

import io.eventuate.tram.events.subscriber.DomainEventEnvelope;
import io.eventuate.tram.events.subscriber.DomainEventHandlers;
import io.eventuate.tram.events.subscriber.DomainEventHandlersBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pt.ulisboa.tecnico.socialsoftware.auth.domain.AuthUser;
import pt.ulisboa.tecnico.socialsoftware.auth.repository.AuthUserRepository;
import pt.ulisboa.tecnico.socialsoftware.common.events.DeleteAuthUserEvent;

import static pt.ulisboa.tecnico.socialsoftware.common.events.EventAggregateTypes.USER_AGGREGATE_TYPE;

@Component
public class UserSubscriptions {
    private static final Logger logger = LoggerFactory.getLogger(UserSubscriptions.class);

    @Autowired
    private AuthUserRepository authUserRepository;

    public DomainEventHandlers domainEventHandlers() {
        return DomainEventHandlersBuilder
                .forAggregateType(USER_AGGREGATE_TYPE)
                .onEvent(DeleteAuthUserEvent.class, this::deleteAuthUser)
                .build();
    }

    public void deleteAuthUser(DomainEventEnvelope<DeleteAuthUserEvent> event) {
        logger.info("Received deleteAuthUser event!");
        DeleteAuthUserEvent deleteAuthUserEvent = event.getEvent();
        AuthUser authUser = authUserRepository.findAuthUserById(deleteAuthUserEvent.getUserId())
                // Does not throw exception because when we anonymize users,
                // events are sent even if authUser does not exist
                .orElse(null);

        if (authUser != null) {
            logger.info(authUser + "");
            authUser.remove();
            authUserRepository.delete(authUser);
        }
    }
}
