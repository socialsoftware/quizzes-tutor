package pt.ulisboa.tecnico.socialsoftware.common.events;

public class AddCourseExecutionEvent {
    private Integer userId;
    private Integer courseExecutionId;

    public AddCourseExecutionEvent(Integer userId, Integer courseExecutionId) {
        this.userId = userId;
        this.courseExecutionId = courseExecutionId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getCourseExecutionId() {
        return courseExecutionId;
    }

    public void setCourseExecutionId(Integer courseExecutionId) {
        this.courseExecutionId = courseExecutionId;
    }
}
