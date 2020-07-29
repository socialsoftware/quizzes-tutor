package pt.ulisboa.tecnico.socialsoftware.tutor.user.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AuthUserDto implements Serializable {
    private String name;
    private String username;
    private String email;
    private User.Role role;
    private boolean admin;
    private Map<String, List<CourseDto>> courses;

    public AuthUserDto(User user) {
        this.name = user.getName();
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.role = user.getRole();
        this.admin = user.isAdmin();
        this.courses = getActiveAndInactiveCourses(user, new ArrayList<>());
    }

    public AuthUserDto(User user, List<CourseDto> currentCourses) {
        this.name = user.getName();
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.role = user.getRole();
        this.admin = user.isAdmin();
        this.courses = getActiveAndInactiveCourses(user, currentCourses);
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

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    public Map<String, List<CourseDto>> getCourses() {
        return courses;
    }

    public void setCourses(Map<String, List<CourseDto>> courses) {
        this.courses = courses;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    private Map<String, List<CourseDto>> getActiveAndInactiveCourses(User user, List<CourseDto> courses) {
        List<CourseDto> courseExecutions = user.getCourseExecutions().stream().map(CourseDto::new).collect(Collectors.toList());
        courses.stream()
                .forEach(courseDto -> {
                    if (courseExecutions.stream().noneMatch(c -> c.getAcronym().equals(courseDto.getAcronym()) && c.getAcademicTerm().equals(courseDto.getAcademicTerm()))) {
                        if (courseDto.getStatus() == null) {
                            courseDto.setStatus(CourseExecution.Status.INACTIVE);
                        }
                        courseExecutions.add(courseDto);
                    }
                });

        return courseExecutions.stream().sorted(Comparator.comparing(CourseDto::getName).thenComparing(CourseDto::getAcademicTerm).reversed())
                .collect(Collectors.groupingBy(CourseDto::getName,
                        Collectors.mapping(courseDto -> courseDto, Collectors.toList())));
    }
}
