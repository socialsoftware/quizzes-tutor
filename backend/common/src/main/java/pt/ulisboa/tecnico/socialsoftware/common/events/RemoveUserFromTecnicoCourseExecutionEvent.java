package pt.ulisboa.tecnico.socialsoftware.common.events;

import io.eventuate.tram.events.common.DomainEvent;

public class RemoveUserFromTecnicoCourseExecutionEvent implements DomainEvent {

    private Integer userId;
    private Integer courseExecutionId;

    public RemoveUserFromTecnicoCourseExecutionEvent() {
    }

    public RemoveUserFromTecnicoCourseExecutionEvent(Integer userId, Integer courseExecutionId) {
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
