package pt.ulisboa.tecnico.socialsoftware.common.dtos.tournament;

import java.io.Serializable;

/**
 * Used during tournament creation and update to avoid one invocation to Tutor module to gather course ids
 */
public class TopicWithCourseDto implements Serializable {
    private Integer id;
    private String name;
    private Integer courseId;

    public TopicWithCourseDto() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getCourseId() {
        return courseId;
    }

    public void setCourseId(Integer courseId) {
        this.courseId = courseId;
    }

    @Override
    public String toString() {
        return "TopicWithCourseDto{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", courseId=" + courseId +
                '}';
    }
}