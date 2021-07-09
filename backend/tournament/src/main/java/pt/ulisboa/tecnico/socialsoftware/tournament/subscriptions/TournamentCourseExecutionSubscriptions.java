package pt.ulisboa.tecnico.socialsoftware.tournament.subscriptions;

import io.eventuate.tram.events.common.DomainEvent;
import io.eventuate.tram.events.subscriber.DomainEventEnvelope;
import io.eventuate.tram.events.subscriber.DomainEventHandlers;
import io.eventuate.tram.events.subscriber.DomainEventHandlersBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import pt.ulisboa.tecnico.socialsoftware.common.events.RemoveCourseExecutionEvent;
import pt.ulisboa.tecnico.socialsoftware.tournament.domain.Tournament;
import pt.ulisboa.tecnico.socialsoftware.tournament.domain.TournamentState;
import pt.ulisboa.tecnico.socialsoftware.tournament.repository.TournamentRepository;

import java.util.List;

import static pt.ulisboa.tecnico.socialsoftware.common.events.EventAggregateTypes.COURSE_EXECUTION_AGGREGATE_TYPE;

public class TournamentCourseExecutionSubscriptions implements DomainEvent {
    private static final Logger logger = LoggerFactory.getLogger(TournamentCourseExecutionSubscriptions.class);

    @Autowired
    private TournamentRepository tournamentRepository;

    public DomainEventHandlers domainEventHandlers() {
        return DomainEventHandlersBuilder
                .forAggregateType(COURSE_EXECUTION_AGGREGATE_TYPE)
                .onEvent(RemoveCourseExecutionEvent.class, this::removeTournamentsFromCourseExecution)
                .build();
    }

    public void removeTournamentsFromCourseExecution(DomainEventEnvelope<RemoveCourseExecutionEvent> event) {
        logger.info("Received RemoveCourseExecutionEvent!");
        RemoveCourseExecutionEvent removeCourseExecutionEvent = event.getEvent();
        List<Tournament> tournamentList = tournamentRepository.findAll();

        tournamentList.forEach(tournament -> {
            if (tournament.getCourseExecution().getId().equals(removeCourseExecutionEvent.getCourseExecutionId())) {
                tournament.setState(TournamentState.REMOVED);
            }
        });
    }

}
