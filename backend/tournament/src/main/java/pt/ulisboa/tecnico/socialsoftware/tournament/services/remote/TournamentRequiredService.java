package pt.ulisboa.tecnico.socialsoftware.tournament.services.remote;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pt.ulisboa.tecnico.socialsoftware.dtos.execution.CourseExecutionDto;
import pt.ulisboa.tecnico.socialsoftware.dtos.quiz.QuizDto;
import pt.ulisboa.tecnico.socialsoftware.dtos.tournament.ExternalStatementCreationDto;
import pt.ulisboa.tecnico.socialsoftware.dtos.tournament.TopicWithCourseDto;
import pt.ulisboa.tecnico.socialsoftware.dtos.user.UserDto;
import pt.ulisboa.tecnico.socialsoftware.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tournament.TournamentACL;
import pt.ulisboa.tecnico.socialsoftware.tournament.domain.TournamentCourseExecution;
import pt.ulisboa.tecnico.socialsoftware.tournament.domain.TournamentCreator;
import pt.ulisboa.tecnico.socialsoftware.tournament.domain.TournamentParticipant;
import pt.ulisboa.tecnico.socialsoftware.tournament.domain.TournamentTopic;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.StatementQuizDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.api.MonolithService;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static pt.ulisboa.tecnico.socialsoftware.exceptions.ErrorMessage.USER_NOT_FOUND;

@Service
public class TournamentRequiredService {

    @Autowired
    private MonolithService monolithService;

    @Autowired
    private TournamentACL tournamentACL;

    public TournamentCreator getTournamentCreator(Integer userId) {
        UserDto userDto = monolithService.findUserById(userId);
        if (userDto != null) {
            return tournamentACL.userToTournamentCreator(userDto);
        }
        else {
            throw new TutorException(USER_NOT_FOUND, userId);
        }
    }

    public TournamentParticipant getTournamentParticipant(Integer userId) {
        UserDto userDto = monolithService.findUserById(userId);
        if (userDto != null) {
            return tournamentACL.userToTournamentParticipant(userDto);
        }
        else {
            throw new TutorException(USER_NOT_FOUND, userId);
        }
    }

    public TournamentCourseExecution getTournamentCourseExecution(Integer courseExecutionId) {
        CourseExecutionDto courseExecutionDto = monolithService.getCourseExecutionById(courseExecutionId);
        return tournamentACL.courseExecutionToTournamentCourseExecution(courseExecutionDto);
    }

    public Integer getDemoCourseExecutionId() {
        return monolithService.getDemoCourseExecutionId();
    }

    public Set<TournamentTopic> getTournamentTopics(Set<Integer> topicsList) {
        List<TopicWithCourseDto> topicWithCourseDtoList = monolithService.findTopics(topicsList);
        Set<TournamentTopic> topics = new HashSet<>();

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
        return monolithService.findQuizById(quizId);
    }

    public void updateQuiz(QuizDto quizDto) {
        monolithService.updateQuiz(quizDto);
    }

    public void deleteQuiz(Integer quizId) {
        monolithService.deleteExternalQuiz(quizId);
    }
}
