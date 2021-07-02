package pt.ulisboa.tecnico.socialsoftware.tournament.subscriptions;

import io.eventuate.tram.events.subscriber.DomainEventEnvelope;
import io.eventuate.tram.events.subscriber.DomainEventHandlers;
import io.eventuate.tram.events.subscriber.DomainEventHandlersBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import pt.ulisboa.tecnico.socialsoftware.common.events.ExternalQuizSolvedEvent;
import pt.ulisboa.tecnico.socialsoftware.common.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tournament.domain.Tournament;
import pt.ulisboa.tecnico.socialsoftware.tournament.domain.TournamentParticipant;
import pt.ulisboa.tecnico.socialsoftware.tournament.repository.TournamentRepository;

import static pt.ulisboa.tecnico.socialsoftware.common.events.EventAggregateTypes.QUIZ_AGGREGATE_TYPE;
import static pt.ulisboa.tecnico.socialsoftware.common.exceptions.ErrorMessage.NO_TOURNAMENT_WITH_QUIZ_ID;

public class TournamentQuizSubscriptions {
    private static final Logger logger = LoggerFactory.getLogger(TournamentQuizSubscriptions.class);

    @Autowired
    private TournamentRepository tournamentRepository;

    public DomainEventHandlers domainEventHandlers() {
        return DomainEventHandlersBuilder
                .forAggregateType(QUIZ_AGGREGATE_TYPE)
                .onEvent(ExternalQuizSolvedEvent.class, this::collectQuizAnswerData)
                .build();
    }

    public void collectQuizAnswerData(DomainEventEnvelope<ExternalQuizSolvedEvent> event) {
        logger.info("Received externalQuizSolvedEvent!");
        ExternalQuizSolvedEvent externalQuizSolvedEvent = event.getEvent();
        Tournament tournament = tournamentRepository.findTournamentByQuizId(externalQuizSolvedEvent.getQuizId()).orElseThrow(() ->
                new TutorException(NO_TOURNAMENT_WITH_QUIZ_ID, externalQuizSolvedEvent.getQuizId()));

        TournamentParticipant participant = tournament.findParticipant(externalQuizSolvedEvent.getParticipantId());
        participant.setAnswered(true);
        participant.setNumberOfAnswered(externalQuizSolvedEvent.getNumberOfAnswered());
        participant.setNumberOfCorrect(externalQuizSolvedEvent.getNumberOfCorrect());
    }
}
