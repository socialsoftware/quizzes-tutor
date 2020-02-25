package pt.ulisboa.tecnico.socialsoftware.tutor.administration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseService;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.StudentDto;

import java.util.List;

@RestController
public class AdministrationController {

    @Autowired
    private AdministrationService administrationService;

    @GetMapping("/admin/courses/executions")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_DEMO_ADMIN')")
    public List<CourseDto> getCourseExecutions() {
        return administrationService.getCourseExecutions();
    }

    @PostMapping("/admin/courses/executions")
    @PreAuthorize("hasRole('ROLE_ADMIN') or (hasRole('ROLE_DEMO_ADMIN') and hasPermission(#courseDto, 'DEMO.ACCESS'))")
    public CourseDto createCourseExecution(@RequestBody CourseDto courseDto) {
        return administrationService.createExternalCourseExecution(courseDto);
    }

}