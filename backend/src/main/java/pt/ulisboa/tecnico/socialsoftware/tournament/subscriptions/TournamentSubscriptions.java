package pt.ulisboa.tecnico.socialsoftware.tournament.subscriptions;

import com.google.common.eventbus.Subscribe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pt.ulisboa.tecnico.socialsoftware.tournament.domain.Tournament;
import pt.ulisboa.tecnico.socialsoftware.tutor.events.ExternalQuizSolvedEvent;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tournament.domain.TournamentParticipant;
import pt.ulisboa.tecnico.socialsoftware.tournament.repository.TournamentRepository;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.NO_TOURNAMENT_WITH_QUIZ_ID;

@Component
public class TournamentSubscriptions {

    private static final Logger logger = LoggerFactory.getLogger(TournamentSubscriptions.class);

    @Autowired
    private TournamentRepository tournamentRepository;

    @Subscribe
    public void collectQuizAnswerData(ExternalQuizSolvedEvent event) {
        logger.info("TournamentQuizSolvedEvent: QuizId: " + event.getQuizId() + ", ParticipantId: " + event.getParticipantId() +
                ", NumberOfAnswered: " + event.getNumberOfAnswered() + ",NumberOfCorrect: " + event.getNumberOfCorrect() + "\n");

        Tournament tournament = tournamentRepository.findTournamentByQuizId(event.getQuizId()).orElseThrow(() ->
                new TutorException(NO_TOURNAMENT_WITH_QUIZ_ID, event.getQuizId()));

        logger.info("Tournament:" + tournament);

        TournamentParticipant participant = tournament.findParticipant(event.getParticipantId());
        participant.setAnswered(true);
        participant.setNumberOfAnswered(event.getNumberOfAnswered());
        participant.setNumberOfCorrect(event.getNumberOfCorrect());
    }
}
