package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.services.remote;

import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.domain.TournamentCreator;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.domain.TournamentParticipant;

public interface UserInterface {
    TournamentCreator getTournamentCreator(Integer userId);

    TournamentParticipant getTournamentParticipant(Integer userId);
}
