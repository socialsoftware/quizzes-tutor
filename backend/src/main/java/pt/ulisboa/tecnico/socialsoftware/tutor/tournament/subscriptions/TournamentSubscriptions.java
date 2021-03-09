package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.subscriptions;

import com.google.common.eventbus.Subscribe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pt.ulisboa.tecnico.socialsoftware.tutor.anticorruptionlayer.tournament.dtos.TournamentDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.events.TournamentQuizSolvedEvent;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.domain.Tournament;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.domain.TournamentParticipant;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.repository.TournamentRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.services.local.TournamentProvidedService;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.NO_TOURNAMENT_WITH_QUIZ_ID;

@Component
public class TournamentSubscriptions {

    private static final Logger logger = LoggerFactory.getLogger(TournamentSubscriptions.class);

    @Autowired
    private TournamentRepository tournamentRepository;

    @Subscribe
    public void collectQuizAnswerData(TournamentQuizSolvedEvent event) {
        logger.info("TournamentQuizSolvedEvent: QuizId: " + event.getQuizId() + ", ParticipantId: " + event.getParticipantId() +
                ", NumberOfAnswered: " + event.getNumberOfAnswered() + ",NumberOfCorrect: " + event.getNumberOfCorrect() + "\n");

        Tournament tournament = tournamentRepository.findTournamentByQuizId(event.getQuizId()).orElseThrow(() ->
                new TutorException(NO_TOURNAMENT_WITH_QUIZ_ID, event.getQuizId()));
        //TournamentDto tournament = tournamentProvidedService.getTournament(2);

        logger.info("Tournament:" + tournament);

        TournamentParticipant participant = tournament.getParticipant(event.getParticipantId());
        participant.setAnswered(true);
        participant.setNumberOfAnswered(event.getNumberOfAnswered());
        participant.setNumberOfCorrect(event.getNumberOfCorrect());
    }
}
