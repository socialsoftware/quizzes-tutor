package pt.ulisboa.tecnico.socialsoftware.common.commands.auth;

import io.eventuate.tram.commands.common.Command;

public class UndoUpdateCourseExecutionsCommand implements Command {
    private Integer authUserId;

    public UndoUpdateCourseExecutionsCommand() {
    }

    public UndoUpdateCourseExecutionsCommand(Integer authUserId) {
        this.authUserId = authUserId;
    }

    public Integer getAuthUserId() {
        return authUserId;
    }

    public void setAuthUserId(Integer authUserId) {
        this.authUserId = authUserId;
    }
}
