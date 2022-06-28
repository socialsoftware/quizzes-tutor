package pt.ulisboa.tecnico.socialsoftware.tournament.activity.createTournamentActivities;

import java.util.Set;

import com.uber.cadence.activity.ActivityMethod;

import pt.ulisboa.tecnico.socialsoftware.common.utils.Constants;
import pt.ulisboa.tecnico.socialsoftware.tournament.domain.TournamentCourseExecution;
import pt.ulisboa.tecnico.socialsoftware.tournament.domain.TournamentTopic;

public interface CreateTournamentActivities {

    @ActivityMethod(scheduleToCloseTimeoutSeconds = 60, taskList = Constants.TOURNAMENT_TASK_LIST)
    void undoCreate(Integer tournamentId);

    @ActivityMethod(scheduleToCloseTimeoutSeconds = 60, taskList = Constants.TOURNAMENT_TASK_LIST)
    void confirmCreate(Integer tournamentId, Integer quizId);

    @ActivityMethod(scheduleToCloseTimeoutSeconds = 60, taskList = Constants.TOURNAMENT_TASK_LIST)
    void storeCourseExecution(Integer tournamentId, TournamentCourseExecution tournamentCourseExecution);

    @ActivityMethod(scheduleToCloseTimeoutSeconds = 60, taskList = Constants.TOURNAMENT_TASK_LIST)
    void storeTopics(Integer tournamentId, Set<TournamentTopic> tournamentTopics);

}
