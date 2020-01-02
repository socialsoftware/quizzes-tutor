package pt.ulisboa.tecnico.socialsoftware.tutor.user.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

public class AuthUserDto implements Serializable {
    private String name;
    private String username;
    private User.Role role;
    private List<CourseDto> activeCourses;
    private List<CourseDto> otherCourses;

    public AuthUserDto(User user) {
        this.name = user.getName();
        this.username = user.getUsername();
        this.role = user.getRole();
        this.activeCourses = user.getCourseExecutions().stream().map(CourseDto::new).collect(Collectors.toList());
    }

    public AuthUserDto(User user, List<CourseDto> otherCourses) {
        this.name = user.getName();
        this.username = user.getUsername();
        this.role = user.getRole();
        this.activeCourses = user.getCourseExecutions().stream().map(CourseDto::new).collect(Collectors.toList());
        this.otherCourses = otherCourses;
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

    public User.Role getRole() {
        return role;
    }

    public void setRole(User.Role role) {
        this.role = role;
    }

    public List<CourseDto> getActiveCourses() {
        return activeCourses;
    }

    public void setActiveCourses(List<CourseDto> activeCourses) {
        this.activeCourses = activeCourses;
    }

    public List<CourseDto> getOtherCourses() {
        return otherCourses;
    }

    public void setOtherCourses(List<CourseDto> otherCourses) {
        this.otherCourses = otherCourses;
    }
}
