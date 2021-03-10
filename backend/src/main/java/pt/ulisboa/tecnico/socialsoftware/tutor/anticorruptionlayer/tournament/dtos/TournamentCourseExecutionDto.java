package pt.ulisboa.tecnico.socialsoftware.tutor.anticorruptionlayer.tournament.dtos;

import pt.ulisboa.tecnico.socialsoftware.tutor.anticorruptionlayer.CourseExecutionStatus;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.domain.TournamentCourseExecution;

public class TournamentCourseExecutionDto {
    private Integer id;
    private Integer courseId;
    private CourseExecutionStatus status;
    private String courseAcronym;

    public TournamentCourseExecutionDto(TournamentCourseExecution tournamentCourseExecution) {
        this.id = tournamentCourseExecution.getId();
        this.courseId = tournamentCourseExecution.getCourseId();
        this.status = tournamentCourseExecution.getStatus();
        this.courseAcronym = tournamentCourseExecution.getCourseAcronym();
    }

    public TournamentCourseExecutionDto(Integer id, Integer courseId, CourseExecutionStatus status, String courseAcronym) {
        this.id = id;
        this.courseId = courseId;
        this.status = status;
        this.courseAcronym = courseAcronym;
    }

    public Integer getId() {
        return id;
    }

    public Integer getCourseId() {
        return courseId;
    }

    public CourseExecutionStatus getStatus() {
        return status;
    }

    public String getCourseAcronym() {
        return courseAcronym;
    }

    @Override
    public String toString() {
        return "TournamentCourseExecutionDto{" +
                "id=" + id +
                ", courseId=" + courseId +
                ", status=" + status +
                ", courseAcronym='" + courseAcronym + '\'' +
                '}';
    }
}
