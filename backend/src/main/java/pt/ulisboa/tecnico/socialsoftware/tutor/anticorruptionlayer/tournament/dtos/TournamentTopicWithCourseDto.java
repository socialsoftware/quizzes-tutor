package pt.ulisboa.tecnico.socialsoftware.tutor.anticorruptionlayer.tournament.dtos;

/**
 * Only for internal use when retrieving topics in creation and update tournament operations
 */
public class TournamentTopicWithCourseDto {
    private Integer id;
    private String name;
    private Integer courseId;

    public TournamentTopicWithCourseDto(Integer id, String name, Integer courseId) {
        this.id = id;
        this.name = name;
        this.courseId = courseId;
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
}
