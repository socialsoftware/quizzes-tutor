package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.domain;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.util.Objects;

@Embeddable
public class TournamentCourseExecution {
    public enum Status {ACTIVE, INACTIVE, HISTORIC}

    @Column(name = "course_execution_id")
    private Integer id;

    @Column(name = "course_id")
    private Integer courseId;

    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(name = "course_acronym")
    private String courseAcronym;

    public TournamentCourseExecution() {
    }

    public TournamentCourseExecution(Integer id, Integer courseId, Status status, String courseAcronym) {
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

    public Status getStatus() {
        return status;
    }

    public String getCourseAcronym() {
        return courseAcronym;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TournamentCourseExecution that = (TournamentCourseExecution) o;
        return courseId.equals(that.courseId) && status == that.status && courseAcronym.equals(that.courseAcronym);
    }

    @Override
    public int hashCode() {
        return Objects.hash(courseId, status, courseAcronym);
    }
}
