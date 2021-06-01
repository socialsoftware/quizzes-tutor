package pt.ulisboa.tecnico.socialsoftware.auth.command;

import io.eventuate.tram.commands.common.Command;
import org.springframework.security.crypto.password.PasswordEncoder;

public class ConfirmRegistrationCommand implements Command {

    private Integer authUserId;
    private PasswordEncoder passwordEncoder;
    private String password;

    public ConfirmRegistrationCommand() {
    }

    public ConfirmRegistrationCommand(Integer authUserId, PasswordEncoder passwordEncoder, String password) {
        this.authUserId = authUserId;
        this.passwordEncoder = passwordEncoder;
        this.password = password;
    }

    public Integer getAuthUserId() {
        return authUserId;
    }

    public void setAuthUserId(Integer authUserId) {
        this.authUserId = authUserId;
    }

    public PasswordEncoder getPasswordEncoder() {
        return passwordEncoder;
    }

    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "ConfirmRegistrationCommand{" +
                "authUserId=" + authUserId +
                ", passwordEncoder=" + passwordEncoder +
                ", password='" + password + '\'' +
                '}';
    }
}
