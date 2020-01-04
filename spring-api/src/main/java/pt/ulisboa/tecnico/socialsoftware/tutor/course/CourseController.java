package pt.ulisboa.tecnico.socialsoftware.tutor.course;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.StudentDto;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/courses")
public class CourseController {

    @Autowired
    private CourseService courseService;

    @GetMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public List<CourseDto> getCourses() {
        return courseService.getCourses();
    }

    @GetMapping("/{name}/executions")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public List<CourseDto> getCourseExecutions(@PathVariable String name) {
        return courseService.getCourseExecutions(name);
    }

    @PostMapping("/{name}/executions")
    @PreAuthorize("hasRole('ROLE_ADMIN') or (hasRole('ROLE_TEACHER') and hasPermission(#courseDto, 'CREATE'))")
    public CourseDto createCourseExecution(@PathVariable String name, @ModelAttribute CourseDto courseDto) {
        return courseService.createCourseExecution(courseDto);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') or (hasRole('ROLE_TEACHER') and hasPermission(#acronym, 'ACCESS'))")
    @GetMapping("/{name}/executions/{acronym}/{academicterm}/students")
    public List<StudentDto> getCourseStudents(@PathVariable String name,@PathVariable String acronym,@PathVariable String academicterm) {
        return courseService.courseStudents(name, acronym, academicterm.replace("_", "/"));
    }


}