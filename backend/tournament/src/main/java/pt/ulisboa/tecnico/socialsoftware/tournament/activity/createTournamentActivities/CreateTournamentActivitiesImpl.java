package pt.ulisboa.tecnico.socialsoftware.tournament.activity.createTournamentActivities;

import java.util.Set;

import pt.ulisboa.tecnico.socialsoftware.tournament.domain.TournamentCourseExecution;
import pt.ulisboa.tecnico.socialsoftware.tournament.domain.TournamentTopic;
import pt.ulisboa.tecnico.socialsoftware.tournament.services.local.TournamentProvidedService;

public class CreateTournamentActivitiesImpl implements CreateTournamentActivities {

    private final TournamentProvidedService tournamentService;

    public CreateTournamentActivitiesImpl(TournamentProvidedService tournamentService) {
        this.tournamentService = tournamentService;
    }

    @Override
    public void undoCreate(Integer tournamentId) {
        tournamentService.rejectCreate(tournamentId);
    }

    @Override
    public void confirmCreate(Integer tournamentId, Integer quizId) {
        tournamentService.confirmCreate(tournamentId, quizId);
    }

    @Override
    public void storeCourseExecution(Integer tournamentId, TournamentCourseExecution tournamentCourseExecution) {
        tournamentService.storeCourseExecution(tournamentId, tournamentCourseExecution);
    }

    @Override
    public void storeTopics(Integer tournamentId, Set<TournamentTopic> tournamentTopics) {
        tournamentService.storeTopics(tournamentId, tournamentTopics);
    }

}
