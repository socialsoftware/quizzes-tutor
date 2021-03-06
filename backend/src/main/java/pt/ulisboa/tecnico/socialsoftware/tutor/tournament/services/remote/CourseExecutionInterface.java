package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.services.remote;

import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.domain.TournamentCourseExecution;

public interface CourseExecutionInterface {
    TournamentCourseExecution getTournamentCourseExecution(Integer courseExecutionId);

    Integer getDemoCourseExecutionId();
}
