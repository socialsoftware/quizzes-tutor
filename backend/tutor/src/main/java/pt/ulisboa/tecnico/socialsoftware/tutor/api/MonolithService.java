package pt.ulisboa.tecnico.socialsoftware.tutor.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pt.ulisboa.tecnico.socialsoftware.common.dtos.answer.StatementQuizDto;
import pt.ulisboa.tecnico.socialsoftware.common.dtos.execution.CourseExecutionDto;
import pt.ulisboa.tecnico.socialsoftware.common.dtos.tournament.FindTopicsDto;
import pt.ulisboa.tecnico.socialsoftware.common.dtos.tournament.TopicListDto;
import pt.ulisboa.tecnico.socialsoftware.common.dtos.user.UserCourseExecutionsDto;
import pt.ulisboa.tecnico.socialsoftware.common.dtos.user.UserDto;
import pt.ulisboa.tecnico.socialsoftware.common.remote.AnswerContract;
import pt.ulisboa.tecnico.socialsoftware.common.remote.CourseExecutionContract;
import pt.ulisboa.tecnico.socialsoftware.common.remote.QuestionContract;
import pt.ulisboa.tecnico.socialsoftware.common.remote.UserContract;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.AnswerService;
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.CourseExecutionService;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.TopicService;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserService;

@Service
public class MonolithService implements UserContract, AnswerContract, QuestionContract, CourseExecutionContract {

    @Autowired
    private UserService userService;

    @Autowired
    private AnswerService answerService;

    @Autowired
    private CourseExecutionService courseExecutionService;

    @Autowired
    private TopicService topicService;

    @Override
    public UserDto findUser(Integer userId) {
        return userService.findUserById(userId);
    }

    @Override
    public UserCourseExecutionsDto getUserCourseExecutions(Integer userId) {
        return userService.getUserCourseExecutions(userId);
    }

    @Override
    public CourseExecutionDto findCourseExecution(Integer courseExecutionId) {
        return courseExecutionService.getCourseExecutionById(courseExecutionId);
    }

    @Override
    public CourseExecutionDto findDemoCourseExecution() {
        return courseExecutionService.getDemoCourse();
    }

    @Override
    public CourseExecutionDto findCourseExecutionByFields(String acronym, String academicTerm, String type) {
        return courseExecutionService.getCourseExecutionByFields(acronym, academicTerm, type);
    }

    @Override
    public FindTopicsDto findTopics(TopicListDto topicsList) {
        return topicService.findTopicById(topicsList.getTopicList());
    }

    public StatementQuizDto startQuiz(Integer userId, Integer quizId) {
        return answerService.startQuiz(userId, quizId);
    }

    @Override
    public StatementQuizDto getStatementQuiz(Integer userId, Integer quizId) {
        return answerService.getStatementQuiz(userId, quizId);
    }
}
