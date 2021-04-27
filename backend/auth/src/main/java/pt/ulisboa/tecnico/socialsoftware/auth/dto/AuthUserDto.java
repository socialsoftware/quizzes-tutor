package pt.ulisboa.tecnico.socialsoftware.auth.dto;

import pt.ulisboa.tecnico.socialsoftware.common.dtos.execution.CourseExecutionDto;
import pt.ulisboa.tecnico.socialsoftware.common.dtos.execution.CourseExecutionStatus;
import pt.ulisboa.tecnico.socialsoftware.common.dtos.user.Role;
import pt.ulisboa.tecnico.socialsoftware.auth.domain.AuthUser;
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.CourseExecution;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.User;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AuthUserDto implements Serializable {
    private Integer id;
    private String name;
    private String username;
    private String email;
    private Role role;
    private boolean admin;
    private Map<String, List<CourseExecutionDto>> courses;

    public AuthUserDto(AuthUser authUser, List<CourseExecutionDto> courseExecutionList) {
        this.id = authUser.getUserSecurityInfo().getId();
        this.name = authUser.getUserSecurityInfo().getName();
        this.username = authUser.getUsername();
        this.email = authUser.getEmail();
        this.role = authUser.getUserSecurityInfo().getRole();
        this.admin = authUser.getUserSecurityInfo().isAdmin();
        this.courses = getActiveAndInactiveCourses(courseExecutionList, new ArrayList<>());
    }

    public AuthUserDto(AuthUser authUser, List<CourseExecutionDto> currentCourses, List<CourseExecutionDto> courseExecutionList) {
        this.id = authUser.getUserSecurityInfo().getId();
        this.name = authUser.getUserSecurityInfo().getName();
        this.username = authUser.getUsername();
        this.email = authUser.getEmail();
        this.role = authUser.getUserSecurityInfo().getRole();
        this.admin = authUser.getUserSecurityInfo().isAdmin();
        this.courses = getActiveAndInactiveCourses(courseExecutionList, currentCourses);
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

    private Map<String, List<CourseExecutionDto>> getActiveAndInactiveCourses(List<CourseExecutionDto> courseExecutions, List<CourseExecutionDto> courses) {
        courses.stream()
                .forEach(courseDto -> {
                    if (courseExecutions.stream().noneMatch(c -> c.getAcronym().equals(courseDto.getAcronym()) && c.getAcademicTerm().equals(courseDto.getAcademicTerm()))) {
                        if (courseDto.getStatus() == null) {
                            courseDto.setStatus(CourseExecutionStatus.INACTIVE);
                        }
                        courseExecutions.add(courseDto);
                    }
                });

        return courseExecutions.stream().sorted(Comparator.comparing(CourseExecutionDto::getName))
                .collect(Collectors.groupingBy(CourseExecutionDto::getAcademicTerm,
                        Collectors.mapping(courseDto -> courseDto, Collectors.toList())));
    }
}
