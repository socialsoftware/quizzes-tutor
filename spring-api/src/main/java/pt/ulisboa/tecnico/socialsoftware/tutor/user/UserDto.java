package pt.ulisboa.tecnico.socialsoftware.tutor.user;

import java.io.Serializable;
import java.time.LocalDateTime;

public class UserDto implements Serializable {
    private int id;
    private String username;
    private String name;
    private int year;
    private User.Role role;
    private LocalDateTime creationDate;

    public UserDto(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.name = user.getName();
        this.year = user.getYear();
        this.role = user.getRole();
        this.creationDate = user.getCreationDate();

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public User.Role getRole() {
        return role;
    }

    public void setRole(User.Role role) {
        this.role = role;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    @Override
    public String toString() {
        return "UserDto{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", name='" + name + '\'' +
                ", year=" + year +
                ", role=" + role +
                ", creationDate=" + creationDate +
                '}';
    }
}
