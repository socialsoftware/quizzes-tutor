package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.services.remote;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pt.ulisboa.tecnico.socialsoftware.tutor.MonolithService;
import pt.ulisboa.tecnico.socialsoftware.tutor.anticorruptionlayer.answer.dtos.StatementQuizDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.anticorruptionlayer.tournament.dtos.*;
import pt.ulisboa.tecnico.socialsoftware.tutor.anticorruptionlayer.tournament.TournamentACL;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.anticorruptionlayer.execution.dtos.CourseExecutionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.anticorruptionlayer.question.dtos.TopicWithCourseDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.anticorruptionlayer.quiz.dtos.QuizDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.domain.TournamentCourseExecution;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.domain.TournamentCreator;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.domain.TournamentParticipant;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.domain.TournamentTopic;
import pt.ulisboa.tecnico.socialsoftware.tutor.anticorruptionlayer.user.dtos.UserDto;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*;

@Service
public class TournamentRequiredService implements UserInterface, CourseExecutionInterface, TopicInterface, AnswerInterface, QuizInterface{

    @Autowired
    private MonolithService monolithService;

    @Autowired
    private TournamentACL tournamentACL;

    @Override
    public TournamentCreator getTournamentCreator(Integer userId) {
        //User user = userRepository.findById(userId).orElseThrow(() -> new TutorException(USER_NOT_FOUND, userId));
        UserDto userDto = monolithService.findUserById(userId);
        if (userDto != null) {
            TournamentCreatorDto tournamentCreatorDto = tournamentACL.userToTournamentCreator(userDto);
            return new TournamentCreator(tournamentCreatorDto);
        }
        else {
            throw new TutorException(USER_NOT_FOUND, userId);
        }
    }

    @Override
    public TournamentParticipant getTournamentParticipant(Integer userId) {
        //User user = userRepository.findById(userId).orElseThrow(() -> new TutorException(USER_NOT_FOUND, userId));
        UserDto userDto = monolithService.findUserById(userId);
        if (userDto != null) {
            TournamentParticipantDto tournamentParticipantDto = tournamentACL.userToTournamentParticipant(userDto);
            return new TournamentParticipant(tournamentParticipantDto);
        }
        else {
            throw new TutorException(USER_NOT_FOUND, userId);
        }
    }

    @Override
    public TournamentCourseExecution getTournamentCourseExecution(Integer courseExecutionId) {
        //CourseExecution courseExecution = courseExecutionRepository.findById(courseExecutionId).orElseThrow(() -> new TutorException(COURSE_EXECUTION_NOT_FOUND, courseExecutionId));
        CourseExecutionDto courseExecutionDto = monolithService.getCourseExecutionById(courseExecutionId);
        TournamentCourseExecutionDto tournamentCourseExecutionDto = tournamentACL.courseExecutionToTournamentCourseExecution(courseExecutionDto);
        return new TournamentCourseExecution(tournamentCourseExecutionDto);
    }

    @Override
    public Integer getDemoCourseExecutionId() {
        return monolithService.getDemoCourseExecutionId();
    }

    @Override
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
            TournamentTopicWithCourseDto tournamentTopicWithCourseDto = tournamentACL.topicToTournamentTopic(topicWithCourseDto);
            topics.add(new TournamentTopic(tournamentTopicWithCourseDto.getId(), tournamentTopicWithCourseDto.getName(),
                    tournamentTopicWithCourseDto.getCourseId()));
        }

        return topics;
    }

    @Override
    public Integer getQuizId(Integer creatorId, Integer courseExecutionId, StatementTournamentCreationDto quizDetails) {
        return monolithService.generateQuizAndGetId(creatorId, courseExecutionId, quizDetails);
    }

    @Override
    public StatementQuizDto startTournamentQuiz(Integer userId, Integer quizId) {
        return monolithService.startQuiz(userId, quizId);
    }

    @Override
    public QuizDto getQuiz(Integer quizId) {
        /*Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new TutorException(QUIZ_NOT_FOUND, quizId));*/

        return monolithService.findQuizById(quizId);
    }

    @Override
    public void updateQuiz(QuizDto quizDto) {
        monolithService.updateQuiz(quizDto);
    }

    @Override
    public void deleteQuiz(Integer quizId) {
        monolithService.deleteExternalQuiz(quizId);
    }
}
