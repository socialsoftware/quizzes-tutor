package pt.ulisboa.tecnico.socialsoftware.tournament.activity.createTournamentActivities;

import java.util.Set;

import pt.ulisboa.tecnico.socialsoftware.tournament.domain.TournamentCourseExecution;
import pt.ulisboa.tecnico.socialsoftware.tournament.domain.TournamentTopic;

public interface CreateTournamentActivities {

    void undoCreate(Integer tournamentId);

    void confirmCreate(Integer tournamentId, Integer quizId);

    void storeCourseExecution(Integer tournamentId, TournamentCourseExecution tournamentCourseExecution);

    void storeTopics(Integer tournamentId, Set<TournamentTopic> tournamentTopics);

}
