package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.domain;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Embeddable
public class TournamentCourseExecution {
    public enum Status {ACTIVE, INACTIVE, HISTORIC}

    @Column(name = "tournament_course_execution_id")
    private Integer tournamentCourseExecutionId;

    @Column(name = "tournament_course_id")
    private Integer tournamentCourseId;

    @Enumerated(EnumType.STRING)
    private Status tournamentCourseExecutionStatus;

    public TournamentCourseExecution() {
    }

    public TournamentCourseExecution(Integer tournamentCourseExecutionId, Integer tournamentCourseId, Status tournamentCourseExecutionStatus) {
        this.tournamentCourseExecutionId = tournamentCourseExecutionId;
        this.tournamentCourseId = tournamentCourseId;
        this.tournamentCourseExecutionStatus = tournamentCourseExecutionStatus;
    }

    public Integer getTournamentCourseExecutionId() {
        return tournamentCourseExecutionId;
    }

    public Integer getTournamentCourseId() {
        return tournamentCourseId;
    }

    public Status getTournamentCourseExecutionStatus() {
        return tournamentCourseExecutionStatus;
    }
}
