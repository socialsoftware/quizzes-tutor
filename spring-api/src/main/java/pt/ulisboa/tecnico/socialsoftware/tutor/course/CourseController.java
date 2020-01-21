package pt.ulisboa.tecnico.socialsoftware.tutor.course;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.StudentDto;

import java.util.List;

@RestController
public class CourseController {

    @Autowired
    private CourseService courseService;

    @GetMapping("/courses")
    @PreAuthorize("hasRole('ROLE_ADMIN') or (hasRole('ROLE_TEACHER')and hasPermission(#name, 'ACCESS'))")
    public List<CourseDto> getCourses() {
        return courseService.getCourses();
    }

    @GetMapping("/courses/{name}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or (hasRole('ROLE_TEACHER') and hasPermission(#name, 'ACCESS'))")
    public List<CourseDto> getCourseExecutions(@PathVariable String name) {
        return courseService.getCourseExecutions(name);
    }

    @PostMapping("/courses")
    @PreAuthorize("hasRole('ROLE_ADMIN') or (hasRole('ROLE_TEACHER') and hasPermission(#courseDto, 'CREATE'))")
    public CourseDto activateCourseExecution(@RequestBody CourseDto courseDto) {
        return courseService.activateCourseExecution(courseDto);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') or (hasRole('ROLE_TEACHER') and hasPermission(#executionId, 'ACCESS'))")
    @GetMapping("/executions/{executionId}/students")
    public List<StudentDto> getCourseStudents(@PathVariable int executionId) {
        return courseService.courseStudents(executionId);
    }


}