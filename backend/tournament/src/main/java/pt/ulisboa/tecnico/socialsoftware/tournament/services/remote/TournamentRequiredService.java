package pt.ulisboa.tecnico.socialsoftware.tournament.services.remote;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pt.ulisboa.tecnico.socialsoftware.common.dtos.answer.StatementQuizDto;
import pt.ulisboa.tecnico.socialsoftware.common.dtos.execution.CourseExecutionDto;
import pt.ulisboa.tecnico.socialsoftware.common.dtos.execution.CourseExecutionStatus;
import pt.ulisboa.tecnico.socialsoftware.common.dtos.quiz.QuizDto;
import pt.ulisboa.tecnico.socialsoftware.common.dtos.tournament.ExternalStatementCreationDto;
import pt.ulisboa.tecnico.socialsoftware.common.dtos.tournament.FindTopicsDto;
import pt.ulisboa.tecnico.socialsoftware.common.dtos.tournament.TopicListDto;
import pt.ulisboa.tecnico.socialsoftware.common.dtos.tournament.TopicWithCourseDto;
import pt.ulisboa.tecnico.socialsoftware.common.dtos.user.UserDto;
import pt.ulisboa.tecnico.socialsoftware.common.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.common.remote.*;
import pt.ulisboa.tecnico.socialsoftware.tournament.domain.TournamentCourseExecution;
import pt.ulisboa.tecnico.socialsoftware.tournament.domain.TournamentCreator;
import pt.ulisboa.tecnico.socialsoftware.tournament.domain.TournamentParticipant;
import pt.ulisboa.tecnico.socialsoftware.tournament.domain.TournamentTopic;

import java.util.HashSet;
import java.util.Set;

import static pt.ulisboa.tecnico.socialsoftware.common.exceptions.ErrorMessage.QUIZ_ANSWER_NOT_FOUND;
import static pt.ulisboa.tecnico.socialsoftware.common.exceptions.ErrorMessage.USER_NOT_FOUND;

@Service
public class TournamentRequiredService {

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


    public TournamentCreator getTournamentCreator(Integer userId) {
        UserDto userDto = userInterface.findUser(userId);

        if (userDto != null) {
            return new TournamentCreator(userDto.getId(), userDto.getUsername(), userDto.getName());
        }
        else {
            throw new TutorException(USER_NOT_FOUND, userId);
        }
    }

    public TournamentParticipant getTournamentParticipant(Integer userId) {
        UserDto userDto = userInterface.findUser(userId);
        if (userDto != null) {
            return new TournamentParticipant(userDto.getId(), userDto.getUsername(), userDto.getName());
        }
        else {
            throw new TutorException(USER_NOT_FOUND, userId);
        }
    }

    public TournamentCourseExecution getTournamentCourseExecution(Integer courseExecutionId) {
        CourseExecutionDto courseExecutionDto = courseExecutionInterface.findCourseExecution(courseExecutionId);
        return new TournamentCourseExecution(courseExecutionDto.getCourseExecutionId(),
                courseExecutionDto.getCourseId(), CourseExecutionStatus.valueOf(courseExecutionDto.getStatus().toString()), courseExecutionDto.getAcronym());
    }

    public Integer getDemoCourseExecutionId() {
        return courseExecutionInterface.findDemoCourseExecution();
    }

    public Set<TournamentTopic> getTournamentTopics(TopicListDto topicsList) {
        FindTopicsDto topicWithCourseDtoList = questionInterface.findTopics(topicsList);
        Set<TournamentTopic> topics = new HashSet<>();

        for (TopicWithCourseDto topicWithCourseDto : topicWithCourseDtoList.getTopicWithCourseDtoList()) {
            topics.add(new TournamentTopic(topicWithCourseDto.getId(), topicWithCourseDto.getName(),
                    topicWithCourseDto.getCourseId()));
        }

        return topics;
    }

    public Integer createQuiz(Integer creatorId, Integer courseExecutionId, ExternalStatementCreationDto quizDetails) {
        return quizInterface.generateQuizAndGetId(creatorId, courseExecutionId, quizDetails);
    }

    public StatementQuizDto startTournamentQuiz(Integer userId, Integer quizId) {
        return answerInterface.startQuiz(userId, quizId);
    }

    public QuizDto getQuiz(Integer quizId) {
        return quizInterface.findQuizById(quizId);
    }

    public void updateQuiz(QuizDto quizDto) {
        quizInterface.updateQuiz(quizDto);
    }

    public void deleteQuiz(Integer quizId) {
        quizInterface.deleteExternalQuiz(quizId);
    }

    public StatementQuizDto getStatementQuiz(Integer userId, Integer quizId) {
        StatementQuizDto quizDto = answerInterface.getStatementQuiz(userId, quizId);
        if (quizDto != null) {
            return quizDto;
        }
        else {
            throw new TutorException(QUIZ_ANSWER_NOT_FOUND, quizId);
        }
    }
}
