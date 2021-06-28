package pt.ulisboa.tecnico.socialsoftware.auth.services.remote;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pt.ulisboa.tecnico.socialsoftware.common.dtos.execution.CourseExecutionDto;
import pt.ulisboa.tecnico.socialsoftware.common.dtos.user.UserCourseExecutionsDto;
import pt.ulisboa.tecnico.socialsoftware.common.remote.CourseExecutionInterface;
import pt.ulisboa.tecnico.socialsoftware.common.remote.UserInterface;

@Service
public class AuthRequiredService {

    @Autowired
    private UserInterface userInterface;

    @Autowired
    private CourseExecutionInterface courseExecutionInterface;

    public UserCourseExecutionsDto getUserCourseExecutions(Integer userId) {
        return userInterface.getUserCourseExecutions(userId);
    }

    public CourseExecutionDto getCourseExecutionById(Integer courseExecutionId) {
        return courseExecutionInterface.findCourseExecution(courseExecutionId);
    }

    public CourseExecutionDto getDemoCourseExecution() {
        return courseExecutionInterface.findDemoCourseExecution();
    }

    public CourseExecutionDto getCourseExecutionByFields(String acronym, String academicTerm, String type) {
        return courseExecutionInterface.findCourseExecutionByFields(acronym, academicTerm, type);
    }

}
