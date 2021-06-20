package pt.ulisboa.tecnico.socialsoftware.auth.subscriptions;

import io.eventuate.tram.events.subscriber.DomainEventEnvelope;
import io.eventuate.tram.events.subscriber.DomainEventHandlers;
import io.eventuate.tram.events.subscriber.DomainEventHandlersBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import pt.ulisboa.tecnico.socialsoftware.auth.domain.AuthUser;
import pt.ulisboa.tecnico.socialsoftware.auth.repository.AuthUserRepository;
import pt.ulisboa.tecnico.socialsoftware.common.events.AddCourseExecutionEvent;
import pt.ulisboa.tecnico.socialsoftware.common.exceptions.TutorException;

import static pt.ulisboa.tecnico.socialsoftware.common.events.EventAggregateTypes.COURSE_EXECUTION_AGGREGATE_TYPE;
import static pt.ulisboa.tecnico.socialsoftware.common.exceptions.ErrorMessage.AUTHUSER_BY_USERID_NOT_FOUND;

public class CourseExecutionSubscriptions {
    private static final Logger logger = LoggerFactory.getLogger(CourseExecutionSubscriptions.class);

    @Autowired
    private AuthUserRepository authUserRepository;

    public DomainEventHandlers domainEventHandlers() {
        return DomainEventHandlersBuilder
                .forAggregateType(COURSE_EXECUTION_AGGREGATE_TYPE)
                .onEvent(AddCourseExecutionEvent.class, this::addCourseExecution)
                .build();
    }

    public void addCourseExecution(DomainEventEnvelope<AddCourseExecutionEvent> event) {
        logger.info("Received addCourseExecution event!");
        AddCourseExecutionEvent addCourseExecutionEvent = event.getEvent();
        AuthUser authUser = authUserRepository.findAuthUserById(addCourseExecutionEvent.getUserId())
                .orElseThrow(() -> new TutorException(AUTHUSER_BY_USERID_NOT_FOUND, addCourseExecutionEvent.getUserId()));

        logger.info(authUser.toString());

        authUser.addCourseExecution(addCourseExecutionEvent.getCourseExecutionId());
    }
}
