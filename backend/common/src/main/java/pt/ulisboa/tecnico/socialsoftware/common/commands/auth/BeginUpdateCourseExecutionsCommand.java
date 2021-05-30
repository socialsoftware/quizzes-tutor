package pt.ulisboa.tecnico.socialsoftware.common.commands.auth;

import io.eventuate.tram.commands.common.Command;

public class BeginUpdateCourseExecutionsCommand implements Command {
    private Integer authUserId;

    public BeginUpdateCourseExecutionsCommand() {
    }

    public BeginUpdateCourseExecutionsCommand(Integer authUserId) {
        this.authUserId = authUserId;
    }

    public Integer getAuthUserId() {
        return authUserId;
    }

    public void setAuthUserId(Integer authUserId) {
        this.authUserId = authUserId;
    }
}
