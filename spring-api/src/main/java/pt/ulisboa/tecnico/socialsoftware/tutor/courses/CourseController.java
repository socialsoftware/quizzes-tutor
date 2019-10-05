package pt.ulisboa.tecnico.socialsoftware.tutor.courses;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import pt.ulisboa.tecnico.socialsoftware.tutor.courses.dto.CourseExecutionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.courses.dto.StudentDto;

import java.util.List;

@RestController
@Secured({ "ROLE_ADMIN", "ROLE_TEACHER" })
public class CourseController {

    @Autowired
    private CourseService courseService;

    @GetMapping("/courses/executions")
    public List<CourseExecutionDto> getCourseExecutions() {
        return courseService.findCourseExecutions();
    }

    @GetMapping("/courses/executions/{year}/students")
    public List<StudentDto> getCourseStudents(@PathVariable Integer year) {
        return courseService.courseStudents(year);
    }


}