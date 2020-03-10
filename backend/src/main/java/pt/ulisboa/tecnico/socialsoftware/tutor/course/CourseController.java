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

    @GetMapping("/courses/{courseId}")
    @PreAuthorize("hasRole('ROLE_TEACHER') and hasPermission(#courseId, 'COURSE.ACCESS')")
    public List<CourseDto> getCourseExecutions(@PathVariable int courseId) {
        return courseService.getCourseExecutions(courseId);
    }

    @PostMapping("/courses")
    @PreAuthorize("hasRole('ROLE_TEACHER') and hasPermission(#courseDto, 'EXECUTION.CREATE')")
    public CourseDto createCourseExecution(@RequestBody CourseDto courseDto) {
        return courseService.createTecnicoCourseExecution(courseDto);
    }

    @PreAuthorize("hasRole('ROLE_TEACHER') and hasPermission(#executionId, 'EXECUTION.ACCESS')")
    @GetMapping("/executions/{executionId}/students")
    public List<StudentDto> getCourseStudents(@PathVariable int executionId) {
        return courseService.courseStudents(executionId);
    }


}