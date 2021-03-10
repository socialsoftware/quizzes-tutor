package pt.ulisboa.tecnico.socialsoftware.tutor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.AnswerService;
import pt.ulisboa.tecnico.socialsoftware.tutor.anticorruptionlayer.answer.dtos.StatementQuizDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.anticorruptionlayer.question.dtos.TopicWithCourseDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.anticorruptionlayer.quiz.dtos.QuizDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.anticorruptionlayer.tournament.dtos.StatementTournamentCreationDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.CourseExecutionService;
import pt.ulisboa.tecnico.socialsoftware.tutor.anticorruptionlayer.execution.dtos.CourseExecutionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.TopicService;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.QuizService;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserService;
import pt.ulisboa.tecnico.socialsoftware.tutor.anticorruptionlayer.user.dtos.UserDto;

import java.util.List;
import java.util.Set;

@Service
public class MonolithService {

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

    public UserDto findUserById(Integer userId) {
        return userService.findUserById(userId);
    }

    public CourseExecutionDto getCourseExecutionById(Integer courseExecutionId) {
        return courseExecutionService.getCourseExecutionById(courseExecutionId);
    }

    public Integer getDemoCourseExecutionId() {
        return courseExecutionService.getDemoCourse().getCourseExecutionId();
    }

    public List<TopicWithCourseDto> findTopics(Set<Integer> topicsList) {
        return topicService.findTopicById(topicsList);
    }

    public Integer generateQuizAndGetId(Integer creatorId, Integer courseExecutionId, StatementTournamentCreationDto quizDetails) {
        return answerService.generateTournamentQuiz(creatorId,
                courseExecutionId, quizDetails).getId();
    }

    public StatementQuizDto startQuiz(Integer userId, Integer quizId) {
        return answerService.startQuiz(userId, quizId);
    }

    public QuizDto findQuizById(Integer quizId) {
        return quizService.findById(quizId);
    }

    public void updateQuiz(QuizDto quizDto) {
        quizService.updateQuiz(quizDto.getId(), quizDto);
    }

    public void deleteExternalQuiz(Integer quizId) {
        quizService.removeExternalQuiz(quizId);
    }


}
