package pt.ulisboa.tecnico.socialsoftware.tutor.course.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.dto.CourseDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseService;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.ExternalUserDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.StudentDto;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.AUTHENTICATION_ERROR;

@RestController
public class CourseController {

    @Autowired
    private CourseService courseService;

    @GetMapping("/courses/executions")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_DEMO_ADMIN')")
    public List<CourseDto> getCourseExecutions(Principal principal) {
        User user = (User) ((Authentication) principal).getPrincipal();

        if (user == null) {
            throw new TutorException(AUTHENTICATION_ERROR);
        }

        User.Role role;
        if (user.isAdmin()) {
            role = User.Role.ADMIN;
        } else {
            role = user.getRole();
        }

        return courseService.getCourseExecutions(role);
    }

    @GetMapping("/executions/{courseExecutionId}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_DEMO_ADMIN')")
    public CourseDto getCourseExecutionById(@PathVariable Integer courseExecutionId) {
        return courseService.getCourseExecutionById(courseExecutionId);
    }

    @PostMapping("/courses/activate")
    @PreAuthorize("hasRole('ROLE_TEACHER') and hasPermission(#courseDto, 'EXECUTION.CREATE')")
    public CourseDto activateCourseExecution(@RequestBody CourseDto courseDto) {
        return courseService.createTecnicoCourseExecution(courseDto);
    }

    @GetMapping("/executions/{executionId}/deactivate")
    @PreAuthorize("hasRole('ROLE_TEACHER') and hasPermission(#executionId, 'EXECUTION.ACCESS')")
    public void deactivateCourseExecution(@PathVariable int executionId) {
        courseService.deactivateCourseExecution(executionId);
    }

    @GetMapping("/executions/{executionId}/anonymize")
    @PreAuthorize("hasRole('ROLE_ADMIN') or (hasRole('ROLE_DEMO_ADMIN') and hasPermission(#executionId, 'DEMO.ACCESS'))")
    public void anonymizeCourseExecutionUsers(@PathVariable int executionId) {
        courseService.anonymizeCourseExecutionUsers(executionId);
    }

    @PostMapping("/courses/external")
    @PreAuthorize("hasRole('ROLE_ADMIN') or (hasRole('ROLE_DEMO_ADMIN') and hasPermission(#courseDto, 'DEMO.ACCESS'))")
    public CourseDto createExternalCourseExecution(@RequestBody CourseDto courseDto) {
        return courseService.createExternalCourseExecution(courseDto);
    }

    @GetMapping("/executions/{executionId}/students")
    @PreAuthorize("hasRole('ROLE_TEACHER') and hasPermission(#executionId, 'EXECUTION.ACCESS')")
    public List<StudentDto> getCourseStudents(@PathVariable int executionId) {
        return courseService.getCourseStudents(executionId);
    }

    @DeleteMapping("/executions/{courseExecutionId}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or (hasRole('ROLE_DEMO_ADMIN') and hasPermission(#courseExecutionId, 'DEMO.ACCESS'))")
    public void removeCourseExecution(@PathVariable Integer courseExecutionId) {
        courseService.removeCourseExecution(courseExecutionId);
    }

    @GetMapping("/executions/{executionId}/users/external")
    @PreAuthorize("hasRole('ROLE_ADMIN') or (hasRole('ROLE_DEMO_ADMIN') and hasPermission(#executionId, 'DEMO.ACCESS'))")
    public List<ExternalUserDto> getExternalUsers(@PathVariable Integer executionId) {
        return courseService.getExternalUsers(executionId);
    }

    @PostMapping("/executions/{executionId}/users/delete/")
    @PreAuthorize("hasRole('ROLE_ADMIN') or (hasRole('ROLE_DEMO_ADMIN') and hasPermission(#executionId, 'DEMO.ACCESS'))")
    public CourseDto deleteExternalInactiveUsers(@PathVariable Integer executionId, @Valid @RequestBody List<Integer> usersIds) {
        return courseService.deleteExternalInactiveUsers(executionId, usersIds);
    }
}