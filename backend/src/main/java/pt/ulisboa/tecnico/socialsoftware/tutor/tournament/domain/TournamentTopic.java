package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.domain;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.util.Objects;

@Embeddable
public class TournamentTopic {

    @Column(name = "topics_id")
    private Integer id;

    @Column(name = "topics_name")
    private String name;

    @Column(name = "topics_course_id")
    private Integer courseId;

    public TournamentTopic() {
    }

    public TournamentTopic(Integer topicId, String topicName, Integer topicCourseId) {
        this.id = topicId;
        this.name = topicName;
        this.courseId = topicCourseId;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Integer getCourseId() {
        return courseId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TournamentTopic that = (TournamentTopic) o;
        return id.equals(that.id) && name.equals(that.name) && courseId.equals(that.courseId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, courseId);
    }

    @Override
    public String toString() {
        return "TournamentTopic{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", courseId=" + courseId +
                '}';
    }
}
