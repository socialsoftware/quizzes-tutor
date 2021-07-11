package pt.ulisboa.tecnico.socialsoftware.common.security.token;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import pt.ulisboa.tecnico.socialsoftware.common.dtos.user.Role;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class UserInfo {

    private final Integer id;
    private final Role role;
    private final Boolean isAdmin;
    private final String username;
    private final List<Integer> courseExecutions;
    private final String enrolledCourseAcronyms;
    private final String name;

    public UserInfo(Integer id, Role role, Boolean isAdmin, String username, List<Integer> executions,
                    String enrolledCourseAcronyms, String name) {
        this.id = id;
        this.role = role;
        this.isAdmin = isAdmin;
        this.username = username;
        this.courseExecutions = executions;
        this.enrolledCourseAcronyms = enrolledCourseAcronyms;
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public Role getRole() {
        return role;
    }

    public Boolean getAdmin() {
        return isAdmin;
    }

    public String getUsername() {
        return username;
    }

    public List<Integer> getCourseExecutions() {
        return courseExecutions;
    }

    public String getEnrolledCourseAcronyms() {
        return enrolledCourseAcronyms;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "UserInfo{" +
                "id=" + id +
                ", role=" + role +
                ", isAdmin=" + isAdmin +
                ", username='" + username + '\'' +
                ", courseExecutions=" + courseExecutions +
                ", enrolledCourseAcronyms='" + enrolledCourseAcronyms + '\'' +
                '}';
    }

    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> list = new ArrayList<>();

        list.add(new SimpleGrantedAuthority("ROLE_" + getRole()));

        if (getAdmin())
            list.add(new SimpleGrantedAuthority("ROLE_ADMIN"));

        return list;
    }

    public boolean isTeacher() { return this.role == Role.TEACHER; }
}
