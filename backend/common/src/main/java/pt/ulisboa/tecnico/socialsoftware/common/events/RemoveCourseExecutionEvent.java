package pt.ulisboa.tecnico.socialsoftware.common.events;

public class RemoveCourseExecutionEvent {

    private Integer courseExecutionId;

    public RemoveCourseExecutionEvent() {
    }

    public RemoveCourseExecutionEvent(int courseExecutionId) {
        this.courseExecutionId = courseExecutionId;
    }

    public Integer getCourseExecutionId() {
        return courseExecutionId;
    }
}
