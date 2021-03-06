package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.services.remote;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.AnswerService;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.StatementQuizDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.anticorruptionlayer.tournament.dtos.StatementTournamentCreationDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.anticorruptionlayer.tournament.TournamentACL;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.CourseExecutionService;
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.dto.CourseExecutionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.TopicService;
import pt.ulisboa.tecnico.socialsoftware.tutor.anticorruptionlayer.question.dtos.TopicWithCourseDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.QuizService;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.dto.QuizDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.domain.TournamentCourseExecution;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.domain.TournamentCreator;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.domain.TournamentParticipant;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.domain.TournamentTopic;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserService;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.UserDto;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*;

@Service
public class TournamentRequiredService implements UserInterface, CourseExecutionInterface, TopicInterface, AnswerInterface, QuizInterface{

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

    @Autowired
    private TournamentACL tournamentACL;

    @Override
    public TournamentCreator getTournamentCreator(Integer userId) {
        //User user = userRepository.findById(userId).orElseThrow(() -> new TutorException(USER_NOT_FOUND, userId));
        UserDto userDto = userService.findUserById(userId);
        if(userDto != null) {
            return tournamentACL.userToTournamentCreator(userDto);
        }
        else{
            throw new TutorException(USER_NOT_FOUND, userId);
        }
    }

    @Override
    public TournamentParticipant getTournamentParticipant(Integer userId) {
        //User user = userRepository.findById(userId).orElseThrow(() -> new TutorException(USER_NOT_FOUND, userId));
        UserDto userDto = userService.findUserById(userId);
        if(userDto != null) {
            return tournamentACL.userToTournamentParticipant(userDto);
        }
        else{
            throw new TutorException(USER_NOT_FOUND, userId);
        }
    }

    @Override
    public TournamentCourseExecution getTournamentCourseExecution(Integer courseExecutionId) {
        //CourseExecution courseExecution = courseExecutionRepository.findById(courseExecutionId).orElseThrow(() -> new TutorException(COURSE_EXECUTION_NOT_FOUND, courseExecutionId));
        CourseExecutionDto courseExecutionDto = courseExecutionService.getCourseExecutionById(courseExecutionId);
        return tournamentACL.courseExecutionToTournamentCourseExecution(courseExecutionDto);
    }

    @Override
    public Integer getDemoCourseExecutionId() {
        return courseExecutionService.getDemoCourse().getCourseExecutionId();
    }

    @Override
    public Set<TournamentTopic> getTournamentTopics(Set<Integer> topicsList) {
        List<TopicWithCourseDto> topicWithCourseDtoList = topicService.findTopicById(topicsList);
        Set<TournamentTopic> topics = new HashSet<>();

        /*for (Integer topicId : topicsList) {
            TopicWithCourseDto topic = topicService.findTopicById(topicId);
            if(topic != null) {
                topics.add(tournamentACL.topicToTournamentTopic(topic));
            }
            else{
                throw new TutorException(TOPIC_NOT_FOUND, topicId);
            }
        }*/

        for (TopicWithCourseDto topicWithCourseDto : topicWithCourseDtoList) {
            topics.add(tournamentACL.topicToTournamentTopic(topicWithCourseDto));
        }

        return topics;
    }

    @Override
    public Integer getQuizId(Integer creatorId, Integer courseExecutionId, StatementTournamentCreationDto quizDetails) {
        StatementQuizDto statementQuizDto = answerService.generateTournamentQuiz(creatorId,
                courseExecutionId, quizDetails);

        return statementQuizDto.getId();
    }

    @Override
    public StatementQuizDto startTournamentQuiz(Integer userId, Integer quizId) {
        return answerService.startQuiz(userId, quizId);
    }

    @Override
    public QuizDto getQuiz(Integer quizId) {
        /*Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new TutorException(QUIZ_NOT_FOUND, quizId));*/

        return quizService.findById(quizId);
    }

    @Override
    public void updateQuiz(QuizDto quizDto) {
        quizService.updateQuiz(quizDto.getId(), quizDto);
    }
}
