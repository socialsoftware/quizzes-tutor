package pt.ulisboa.tecnico.socialsoftware.tournament.subscriptions;

import com.google.common.eventbus.Subscribe;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pt.ulisboa.tecnico.socialsoftware.common.events.ExternalQuizSolvedEvent;
import pt.ulisboa.tecnico.socialsoftware.common.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tournament.domain.Tournament;
import pt.ulisboa.tecnico.socialsoftware.tournament.domain.TournamentParticipant;
import pt.ulisboa.tecnico.socialsoftware.tournament.repository.TournamentRepository;

import static pt.ulisboa.tecnico.socialsoftware.common.exceptions.ErrorMessage.NO_TOURNAMENT_WITH_QUIZ_ID;

@Component
public class TournamentSubscriptions {

    @Autowired
    private TournamentRepository tournamentRepository;

    @Subscribe
    public void collectQuizAnswerData(ExternalQuizSolvedEvent event) {
        Tournament tournament = tournamentRepository.findTournamentByQuizId(event.getQuizId()).orElseThrow(() ->
                new TutorException(NO_TOURNAMENT_WITH_QUIZ_ID, event.getQuizId()));

        TournamentParticipant participant = tournament.findParticipant(event.getParticipantId());
        participant.setAnswered(true);
        participant.setNumberOfAnswered(event.getNumberOfAnswered());
        participant.setNumberOfCorrect(event.getNumberOfCorrect());
    }
}
