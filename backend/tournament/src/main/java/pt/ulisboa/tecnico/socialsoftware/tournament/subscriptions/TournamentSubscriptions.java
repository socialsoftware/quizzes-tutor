package pt.ulisboa.tecnico.socialsoftware.tournament.subscriptions;

import com.google.common.eventbus.Subscribe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pt.ulisboa.tecnico.socialsoftware.common.events.answer.ExternalQuizSolvedEvent;
import pt.ulisboa.tecnico.socialsoftware.common.events.execution.AnonymizeUserEvent;
import pt.ulisboa.tecnico.socialsoftware.common.events.execution.RemoveCourseExecutionEvent;
import pt.ulisboa.tecnico.socialsoftware.common.events.topic.TopicDeletedEvent;
import pt.ulisboa.tecnico.socialsoftware.common.events.topic.TopicUpdatedEvent;
import pt.ulisboa.tecnico.socialsoftware.common.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tournament.domain.Tournament;
import pt.ulisboa.tecnico.socialsoftware.tournament.domain.TournamentCreator;
import pt.ulisboa.tecnico.socialsoftware.tournament.domain.TournamentParticipant;
import pt.ulisboa.tecnico.socialsoftware.tournament.domain.TournamentTopic;
import pt.ulisboa.tecnico.socialsoftware.tournament.repository.TournamentRepository;

import java.util.List;

import static pt.ulisboa.tecnico.socialsoftware.common.exceptions.ErrorMessage.NO_TOURNAMENT_WITH_QUIZ_ID;

@Component
public class TournamentSubscriptions {
    private static Logger logger = LoggerFactory.getLogger(TournamentSubscriptions.class);

    @Autowired
    private TournamentRepository tournamentRepository;

    @Subscribe
    public void collectQuizAnswerData(ExternalQuizSolvedEvent event) {
        logger.info("Received externalQuizSolvedEvent!");

        Tournament tournament = tournamentRepository.findTournamentByQuizId(event.getQuizId()).orElseThrow(() ->
                new TutorException(NO_TOURNAMENT_WITH_QUIZ_ID, event.getQuizId()));

        TournamentParticipant participant = tournament.findParticipant(event.getParticipantId());
        participant.setAnswered(true);
        participant.setNumberOfAnswered(event.getNumberOfAnswered());
        participant.setNumberOfCorrect(event.getNumberOfCorrect());
    }

    @Subscribe
    public void removeTournamentsFromCourseExecution(RemoveCourseExecutionEvent event) {
        logger.info("Received RemoveCourseExecutionEvent!");
        List<Tournament> tournamentList = tournamentRepository.findAll();

        tournamentList.forEach(tournament -> {
            if (tournament.getCourseExecution().getId().equals(event.getCourseExecutionId())) {
                tournament.remove();
                tournamentRepository.delete(tournament);
            }
        });
    }

    @Subscribe
    public void deleteTopic(TopicDeletedEvent event) {
        logger.info("Received TopicDeletedEvent!");
        tournamentRepository.findAll().forEach(tournament -> tournament.getTopics()
                .removeIf(topic -> topic.getId().equals(event.getTopicId())));
    }

    @Subscribe
    public void updateTopicName(TopicUpdatedEvent event) {
        logger.info("Received TopicUpdatedEvent!");

        tournamentRepository.findAll().forEach(tournament -> {
            for (TournamentTopic topic : tournament.getTopics()) {
                if (topic.getId().equals(event.getTopicId())) {
                    topic.setName(event.getNewName());
                    break;
                }
            }
        });
    }

    @Subscribe
    public void anonymizeUser(AnonymizeUserEvent event) {
        logger.info("Received anonymizeUserEvent!");
        tournamentRepository.findAll().forEach(tournament -> {
            TournamentCreator creator = tournament.getCreator();

            if (creator.getId().equals(event.getId())) {
                creator.setName(event.getName());
                creator.setUsername(event.getUsername());
            }

            for (TournamentParticipant participant : tournament.getParticipants()) {
                if (participant.getId().equals(event.getId())) {
                    participant.setName(event.getName());
                    participant.setUsername(event.getUsername());
                    break;
                }
            }
        });
    }
}
