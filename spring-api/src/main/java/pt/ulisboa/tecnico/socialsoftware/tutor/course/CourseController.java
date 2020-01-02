package pt.ulisboa.tecnico.socialsoftware.tutor.course;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.StudentDto;

import java.util.List;

@RestController
@Secured({ "ROLE_ADMIN", "ROLE_TEACHER" })
@RequestMapping("/courses")
public class CourseController {

    @Autowired
    private CourseService courseService;

    @GetMapping
    public List<CourseDto> getCourses() {
        return courseService.getCourses();
    }

    @GetMapping("/{name}/executions")
    public List<CourseDto> getCourseExecutions(@PathVariable String name) {
        return courseService.getCourseExecutions(name);
    }

    @PostMapping("/{name}/executions")
    public CourseDto createCourseExecution(@PathVariable String name, @ModelAttribute CourseDto courseDto) {
        return courseService.createCourseExecution(courseDto);
    }

    @GetMapping("/executions/{year}/students")
    public List<StudentDto> getCourseStudents(@PathVariable Integer year) {
        return courseService.courseStudents(year);
    }


}