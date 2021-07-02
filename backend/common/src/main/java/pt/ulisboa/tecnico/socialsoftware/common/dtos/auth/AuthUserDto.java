package pt.ulisboa.tecnico.socialsoftware.common.dtos.auth;

import pt.ulisboa.tecnico.socialsoftware.common.dtos.execution.CourseExecutionDto;
import pt.ulisboa.tecnico.socialsoftware.common.dtos.user.Role;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class AuthUserDto implements Serializable {
    private Integer id;
    private String name;
    private String username;
    private String email;
    private Role role;
    private boolean admin;
    private Map<String, List<CourseExecutionDto>> courses;

    public AuthUserDto() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    public Map<String, List<CourseExecutionDto>> getCourses() {
        return courses;
    }

    public void setCourses(Map<String, List<CourseExecutionDto>> courses) {
        this.courses = courses;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

}
