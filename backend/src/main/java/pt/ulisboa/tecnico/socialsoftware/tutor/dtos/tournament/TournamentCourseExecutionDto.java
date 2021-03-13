package pt.ulisboa.tecnico.socialsoftware.tutor.dtos.tournament;

import pt.ulisboa.tecnico.socialsoftware.tutor.dtos.execution.CourseExecutionStatus;

public class TournamentCourseExecutionDto {
    private Integer id;
    private Integer courseId;
    private CourseExecutionStatus status;
    private String courseAcronym;

    public TournamentCourseExecutionDto() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getCourseId() {
        return courseId;
    }

    public void setCourseId(Integer courseId) {
        this.courseId = courseId;
    }

    public CourseExecutionStatus getStatus() {
        return status;
    }

    public void setStatus(CourseExecutionStatus status) {
        this.status = status;
    }

    public String getCourseAcronym() {
        return courseAcronym;
    }

    public void setCourseAcronym(String courseAcronym) {
        this.courseAcronym = courseAcronym;
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
