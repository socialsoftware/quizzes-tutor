package pt.ulisboa.tecnico.socialsoftware.common.events;

import io.eventuate.tram.events.common.DomainEvent;

public class DeleteAuthUserEvent implements DomainEvent {
    private Integer userId;

    public DeleteAuthUserEvent() {
    }

    public DeleteAuthUserEvent(Integer userId) {
        this.userId = userId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }
}
