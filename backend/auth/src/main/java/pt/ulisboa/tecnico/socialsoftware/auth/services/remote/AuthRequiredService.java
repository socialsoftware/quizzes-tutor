package pt.ulisboa.tecnico.socialsoftware.auth.services.remote;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pt.ulisboa.tecnico.socialsoftware.common.dtos.execution.CourseExecutionDto;
import pt.ulisboa.tecnico.socialsoftware.common.remote.*;

import java.util.List;

@Service
public class AuthRequiredService {

    @Autowired
    private UserInterface userInterface;

    @Autowired
    private CourseExecutionInterface courseExecutionInterface;

    @Autowired
    private QuestionInterface questionInterface;

    @Autowired
    private QuizInterface quizInterface;

    @Autowired
    private AnswerInterface answerInterface;

    public List<CourseExecutionDto> getUserCourseExecutions(Integer userId) {
        List<CourseExecutionDto> courseExecutionDtoList = userInterface.getCourseExecutions(userId);
        return courseExecutionDtoList;
    }

    public CourseExecutionDto getCourseExecutionById(Integer courseExecutionId) {
        CourseExecutionDto courseExecutionDto= courseExecutionInterface.findCourseExecution(courseExecutionId);
        return courseExecutionDto;
    }
}
