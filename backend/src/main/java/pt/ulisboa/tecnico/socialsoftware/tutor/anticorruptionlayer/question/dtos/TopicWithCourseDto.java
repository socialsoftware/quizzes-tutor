package pt.ulisboa.tecnico.socialsoftware.tutor.anticorruptionlayer.question.dtos;

import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.domain.TournamentTopic;

import java.io.Serializable;

public class TopicWithCourseDto implements Serializable {
    private Integer id;
    private String name;
    private Integer courseId;

    public TopicWithCourseDto(Topic topic) {
        this.id = topic.getId();
        this.name = topic.getName();
        this.courseId = topic.getCourse().getId();
    }

    public TopicWithCourseDto(TournamentTopic topic) {
        this.id = topic.getId();
        this.name = topic.getName();
        this.courseId = topic.getCourseId();
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
    public String toString() {
        return "TopicWithCourseDto{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", courseId=" + courseId +
                '}';
    }
}
