package pt.ulisboa.tecnico.socialsoftware.common.events.execution;

import io.eventuate.tram.events.common.DomainEvent;

public class AddCourseExecutionEvent implements DomainEvent {
    private Integer userId;
    private Integer courseExecutionId;

    public AddCourseExecutionEvent() {
    }

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
