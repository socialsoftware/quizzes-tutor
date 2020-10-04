package pt.ulisboa.tecnico.socialsoftware.tutor.auth.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.course.dto.CourseDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.domain.CourseExecution;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.User;
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.domain.AuthUser;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AuthUserDto implements Serializable {
    private Integer key;
    private Integer id;
    private String name;
    private String username;
    private String email;
    private User.Role role;
    private boolean admin;
    private Map<String, List<CourseDto>> courses;

    public AuthUserDto(AuthUser authUser) {
        this.id = authUser.getUser().getId();
        this.key = authUser.getUser().getKey();
        this.name = authUser.getUser().getName();
        this.username = authUser.getUsername();
        this.email = authUser.getEmail();
        this.role = authUser.getUser().getRole();
        this.admin = authUser.getUser().isAdmin();
        this.courses = getActiveAndInactiveCourses(authUser.getUser(), new ArrayList<>());
    }

    public AuthUserDto(AuthUser authUser, List<CourseDto> currentCourses) {
        this.id = authUser.getUser().getId();
        this.key = authUser.getUser().getKey();
        this.name = authUser.getUser().getName();
        this.username = authUser.getUsername();
        this.email = authUser.getEmail();
        this.role = authUser.getUser().getRole();
        this.admin = authUser.getUser().isAdmin();
        this.courses = getActiveAndInactiveCourses(authUser.getUser(), currentCourses);
    }

    public Integer getKey() {
        return key;
    }

    public void setKey(Integer key) {
        this.key = key;
    }
    
    public Integer getId() {
        return id;
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
