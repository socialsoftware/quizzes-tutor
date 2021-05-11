package pt.ulisboa.tecnico.socialsoftware.common.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import pt.ulisboa.tecnico.socialsoftware.common.dtos.user.Role;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public class UserInfo {

    private final Integer id;
    private final Role role;
    private final Boolean isAdmin;
    private final String username;
    private final Set<Integer> courseExecutions;
    private final String enrolledCourseAcronyms;

    public UserInfo(Integer id, Role role, Boolean isAdmin, String username, Set<Integer> executions, String enrolledCourseAcronyms) {
        this.id = id;
        this.role = role;
        this.isAdmin = isAdmin;
        this.username = username;
        this.courseExecutions = executions;
        this.enrolledCourseAcronyms = enrolledCourseAcronyms;
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

    public Set<Integer> getCourseExecutions() {
        return courseExecutions;
    }

    public String getEnrolledCourseAcronyms() {
        return enrolledCourseAcronyms;
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
