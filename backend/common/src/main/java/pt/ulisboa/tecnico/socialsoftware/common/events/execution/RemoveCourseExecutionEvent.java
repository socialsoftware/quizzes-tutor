package pt.ulisboa.tecnico.socialsoftware.common.events.execution;

import io.eventuate.tram.events.common.DomainEvent;

public class RemoveCourseExecutionEvent implements DomainEvent {

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
