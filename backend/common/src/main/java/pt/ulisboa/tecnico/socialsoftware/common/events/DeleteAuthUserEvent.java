package pt.ulisboa.tecnico.socialsoftware.common.events;

public class DeleteAuthUserEvent {
    private Integer userId;

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
