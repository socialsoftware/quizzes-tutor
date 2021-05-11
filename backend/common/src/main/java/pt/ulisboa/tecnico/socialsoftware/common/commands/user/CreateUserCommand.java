package pt.ulisboa.tecnico.socialsoftware.common.commands.user;

import io.eventuate.tram.commands.common.Command;
import pt.ulisboa.tecnico.socialsoftware.common.dtos.user.Role;

public class CreateUserCommand implements Command {

    private String name;
    private Role role;
    private String username;
    private boolean isActive;
    private boolean isAdmin;

    public CreateUserCommand() {
    }

    public CreateUserCommand(String name, Role role, String username, boolean isActive, boolean isAdmin) {
        this.name = name;
        this.role = role;
        this.username = username;
        this.isActive = isActive;
        this.isAdmin = isAdmin;
    }

    public String getName() {
        return name;
    }

    public Role getRole() {
        return role;
    }

    public String getUsername() {
        return username;
    }

    public boolean isActive() {
        return isActive;
    }

    public boolean isAdmin() {
        return isAdmin;
    }
}
