package pt.ulisboa.tecnico.socialsoftware.tutor.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pt.ulisboa.tecnico.socialsoftware.common.dtos.execution.CourseExecutionDto;
import pt.ulisboa.tecnico.socialsoftware.common.dtos.quiz.QuizDto;
import pt.ulisboa.tecnico.socialsoftware.common.dtos.tournament.ExternalStatementCreationDto;
import pt.ulisboa.tecnico.socialsoftware.common.dtos.tournament.FindTopicsDto;
import pt.ulisboa.tecnico.socialsoftware.common.dtos.tournament.TopicListDto;
import pt.ulisboa.tecnico.socialsoftware.common.dtos.tournament.TopicWithCourseDto;
import pt.ulisboa.tecnico.socialsoftware.common.dtos.user.UserDto;
import pt.ulisboa.tecnico.socialsoftware.common.remote.*;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.AnswerService;
import pt.ulisboa.tecnico.socialsoftware.common.dtos.answer.StatementQuizDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.CourseExecutionService;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.TopicService;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.QuizService;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserService;

import java.util.List;
import java.util.Set;

@Service
public class MonolithService implements UserContract, AnswerContract, QuizContract, QuestionContract, CourseExecutionContract {

    @Autowired
    private UserService userService;

    @Autowired
    private AnswerService answerService;

    @Autowired
    private CourseExecutionService courseExecutionService;

    @Autowired
    private QuizService quizService;

    @Autowired
    private TopicService topicService;

    @Override
    public UserDto findUser(Integer userId) {
        return userService.findUserById(userId);
    }

    @Override
    public CourseExecutionDto findCourseExecution(Integer courseExecutionId) {
        return courseExecutionService.getCourseExecutionById(courseExecutionId);
    }

    @Override
    public Integer getDemoCourseExecutionId() {
        return courseExecutionService.getDemoCourse().getCourseExecutionId();
    }

    @Override
    public FindTopicsDto findTopics(TopicListDto topicsList) {
        return topicService.findTopicById(topicsList.getTopicList());
    }

    public Integer generateQuizAndGetId(Integer creatorId, Integer courseExecutionId, ExternalStatementCreationDto quizDetails) {
        return answerService.generateTournamentQuiz(creatorId,
                courseExecutionId, quizDetails).getId();
    }

    public StatementQuizDto startQuiz(Integer userId, Integer quizId) {
        return answerService.startQuiz(userId, quizId);
    }

    @Override
    public QuizDto findQuizById(Integer quizId) {
        return quizService.findById(quizId);
    }

    @Override
    public void updateQuiz(QuizDto quizDto) {
        quizService.updateQuiz(quizDto.getId(), quizDto);
    }

    @Override
    public void deleteExternalQuiz(Integer quizId) {
        quizService.removeExternalQuiz(quizId);
    }
}
