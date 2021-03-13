package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.services.remote;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pt.ulisboa.tecnico.socialsoftware.tutor.api.MonolithService;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.StatementQuizDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.dtos.tournament.*;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.TournamentACL;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.dtos.execution.CourseExecutionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.dtos.quiz.QuizDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.domain.TournamentCourseExecution;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.domain.TournamentCreator;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.domain.TournamentParticipant;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.domain.TournamentTopic;
import pt.ulisboa.tecnico.socialsoftware.tutor.dtos.user.UserDto;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*;

@Service
public class TournamentRequiredService {

    @Autowired
    private MonolithService monolithService;

    @Autowired
    private TournamentACL tournamentACL;

    public TournamentCreator getTournamentCreator(Integer userId) {
        //User user = userRepository.findById(userId).orElseThrow(() -> new TutorException(USER_NOT_FOUND, userId));
        UserDto userDto = monolithService.findUserById(userId);
        if (userDto != null) {
            return tournamentACL.userToTournamentCreator(userDto);
        }
        else {
            throw new TutorException(USER_NOT_FOUND, userId);
        }
    }

    public TournamentParticipant getTournamentParticipant(Integer userId) {
        //User user = userRepository.findById(userId).orElseThrow(() -> new TutorException(USER_NOT_FOUND, userId));
        UserDto userDto = monolithService.findUserById(userId);
        if (userDto != null) {
            return tournamentACL.userToTournamentParticipant(userDto);
        }
        else {
            throw new TutorException(USER_NOT_FOUND, userId);
        }
    }

    public TournamentCourseExecution getTournamentCourseExecution(Integer courseExecutionId) {
        //CourseExecution courseExecution = courseExecutionRepository.findById(courseExecutionId).orElseThrow(() -> new TutorException(COURSE_EXECUTION_NOT_FOUND, courseExecutionId));
        CourseExecutionDto courseExecutionDto = monolithService.getCourseExecutionById(courseExecutionId);
        return tournamentACL.courseExecutionToTournamentCourseExecution(courseExecutionDto);
    }

    public Integer getDemoCourseExecutionId() {
        return monolithService.getDemoCourseExecutionId();
    }

    public Set<TournamentTopic> getTournamentTopics(Set<Integer> topicsList) {
        List<TopicWithCourseDto> topicWithCourseDtoList = monolithService.findTopics(topicsList);
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

    public Integer getQuizId(Integer creatorId, Integer courseExecutionId, ExternalStatementCreationDto quizDetails) {
        return monolithService.generateQuizAndGetId(creatorId, courseExecutionId, quizDetails);
    }

    public StatementQuizDto startTournamentQuiz(Integer userId, Integer quizId) {
        return monolithService.startQuiz(userId, quizId);
    }

    public QuizDto getQuiz(Integer quizId) {
        /*Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new TutorException(QUIZ_NOT_FOUND, quizId));*/

        return monolithService.findQuizById(quizId);
    }

    public void updateQuiz(QuizDto quizDto) {
        monolithService.updateQuiz(quizDto);
    }

    public void deleteQuiz(Integer quizId) {
        monolithService.deleteExternalQuiz(quizId);
    }
}
