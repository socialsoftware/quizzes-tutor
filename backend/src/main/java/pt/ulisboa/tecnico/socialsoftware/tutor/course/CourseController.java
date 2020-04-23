package pt.ulisboa.tecnico.socialsoftware.tutor.course;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.StudentDto;

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
        return courseService.getCourseExecutions(user.getRole());
    }

    @PostMapping("/courses/activate")
    @PreAuthorize("hasRole('ROLE_TEACHER') and hasPermission(#courseDto, 'EXECUTION.CREATE')")
    public CourseDto activateCourseExecution(@RequestBody CourseDto courseDto) {
        return courseService.createTecnicoCourseExecution(courseDto);
    }

    @GetMapping("/executions/{executionId}/deactivate")
    @PreAuthorize("hasRole('ROLE_TEACHER') and hasPermission(#executionId, 'EXECUTION.ACCESS')")
    public ResponseEntity deactivateCourseExecution(@PathVariable int executionId) {
        courseService.deactivateCourseExecution(executionId);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/courses/external")
    @PreAuthorize("hasRole('ROLE_ADMIN') or (hasRole('ROLE_DEMO_ADMIN') and hasPermission(#courseDto, 'DEMO.ACCESS'))")
    public CourseDto createExternalCourseExecution(@RequestBody CourseDto courseDto) {
        return courseService.createExternalCourseExecution(courseDto);
    }

    @GetMapping("/executions/{executionId}/students")
    @PreAuthorize("hasRole('ROLE_TEACHER') and hasPermission(#executionId, 'EXECUTION.ACCESS')")
    public List<StudentDto> getCourseStudents(@PathVariable int executionId) {
        return courseService.courseStudents(executionId);
    }

    @DeleteMapping("/executions/{courseExecutionId}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or (hasRole('ROLE_DEMO_ADMIN') and hasPermission(#courseExecutionId, 'DEMO.ACCESS'))")
    public ResponseEntity removeCourseExecution(@PathVariable Integer courseExecutionId) {
        courseService.removeCourseExecution(courseExecutionId);

        return ResponseEntity.ok().build();
    }
}