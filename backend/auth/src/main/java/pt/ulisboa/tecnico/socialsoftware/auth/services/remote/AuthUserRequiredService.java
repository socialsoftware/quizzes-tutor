package pt.ulisboa.tecnico.socialsoftware.auth.services.remote;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pt.ulisboa.tecnico.socialsoftware.common.dtos.execution.CourseExecutionDto;
import pt.ulisboa.tecnico.socialsoftware.common.dtos.user.Role;
import pt.ulisboa.tecnico.socialsoftware.common.dtos.user.UserDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.CourseExecutionService;
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.CourseExecution;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserService;

import java.util.List;

@Service
public class AuthUserRequiredService {

    @Autowired
    private UserService userService;

    @Autowired
    private CourseExecutionService courseExecutionService;

    public List<CourseExecutionDto> getUserCourseExecutions(Integer userId) {
        return userService.getUserCourseExecutions(userId);
    }

    public CourseExecutionDto getCourseExecutionById(Integer courseExecutionId) {
        return courseExecutionService.getCourseExecutionById(courseExecutionId);
    }

    public CourseExecution getDemoCourseExecution() {
        return courseExecutionService.getDemoCourseExecution();
    }

    public CourseExecutionDto getCourseExecutionByFields(String acronym, String academicTerm, String type) {
        return courseExecutionService.getCourseExecutionByFields(acronym, academicTerm, type);
    }

    public void addCourseExecution(Integer id, int courseExecutionId) {
        userService.addCourseExecution(id, courseExecutionId);
    }

    public UserDto createUser(String name, Role role, String username, boolean isActive, boolean isAdmin) {
        return userService.createUser(name, role, username, isActive, isAdmin);
    }

    public void addUserToTecnicoCourseExecution(Integer id, Integer courseExecutionId) {
        courseExecutionService.addUserToTecnicoCourseExecution(id, courseExecutionId);
    }

    public void activateUser(Integer id) {
        userService.activateUser(id);
    }
}
