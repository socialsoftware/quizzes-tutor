package pt.ulisboa.tecnico.socialsoftware.tutor.tournament;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.AnswerService;
import pt.ulisboa.tecnico.socialsoftware.tutor.utils.DateHandler;
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.CourseExecutionService;
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.CourseExecution;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository.CourseExecutionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.TopicRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.QuizService;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.dto.QuizDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository.QuizRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.StatementQuizDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.StatementTournamentCreationDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.domain.Tournament;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.dto.TournamentDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.repository.TournamentRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.User;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.repository.UserRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.StudentDto;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*;

@Service
public class TournamentService {
    @Autowired
    private CourseExecutionRepository courseExecutionRepository;

    @Autowired
    private QuizService quizService;

    @Autowired
    private CourseExecutionService courseExecutionService;

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private AnswerService answerService;

    @Autowired
    private TopicRepository topicRepository;

    @Autowired
    private TournamentRepository tournamentRepository;

    @Autowired
    private UserRepository userRepository;

    @Retryable(value = { SQLException.class }, backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public TournamentDto createTournament(Integer userId, Integer executionId, Set<Integer> topicsId, TournamentDto tournamentDto) {
        checkInput(userId, topicsId, tournamentDto);
        User user = userRepository.findById(userId).orElseThrow(() -> new TutorException(USER_NOT_FOUND, userId));
        CourseExecution courseExecution = courseExecutionRepository.findById(executionId).orElseThrow(() -> new TutorException(COURSE_EXECUTION_NOT_FOUND, executionId));

        Set<Topic> topics = new HashSet<>();
        for (Integer topicId : topicsId) {
            Topic topic = topicRepository.findById(topicId)
                    .orElseThrow(() -> new TutorException(TOPIC_NOT_FOUND, topicId));

            topics.add(topic);
        }

        if (topics.isEmpty()) {
            throw new TutorException(TOURNAMENT_MISSING_TOPICS);
        }

        Tournament tournament = new Tournament(user, courseExecution, topics, tournamentDto);
        tournamentRepository.save(tournament);

        return new TournamentDto(tournament);
    }

    @Retryable(value = { SQLException.class }, backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public List<TournamentDto> getTournamentsForCourseExecution(Integer executionId) {
        return tournamentRepository.getTournamentsForCourseExecution(executionId).stream().map(TournamentDto::new)
                .collect(Collectors.toList());
    }

    @Retryable(value = { SQLException.class }, backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public List<TournamentDto> getOpenedTournamentsForCourseExecution(Integer executionId) {
        LocalDateTime now = DateHandler.now();
        return tournamentRepository.getOpenedTournamentsForCourseExecution(executionId, now).stream().map(TournamentDto::new)
                .collect(Collectors.toList());
    }

    @Retryable(value = { SQLException.class }, backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public List<TournamentDto> getClosedTournamentsForCourseExecution(Integer executionId) {
        LocalDateTime now = DateHandler.now();
        return tournamentRepository.getClosedTournamentsForCourseExecution(executionId, now).stream().map(TournamentDto::new)
                .collect(Collectors.toList());
    }


    @Retryable(value = { SQLException.class }, backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public TournamentDto getTournament(Integer tournamentId) {
        return tournamentRepository.findById(tournamentId)
                .map(TournamentDto::new)
                .orElseThrow(() -> new TutorException(TOURNAMENT_NOT_FOUND, tournamentId));
    }

    @Retryable(value = { SQLException.class }, backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void joinTournament(Integer userId, Integer tournamentId, String password) {
        User user = userRepository.findById(userId).orElseThrow(() -> new TutorException(USER_NOT_FOUND, userId));
        Tournament tournament = checkTournament(tournamentId);

        tournament.addParticipant(user, password);
    }

    @Retryable(value = { SQLException.class }, backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public StatementQuizDto solveQuiz(Integer userId, Integer tournamentId) {
        Tournament tournament = checkTournament(tournamentId);

        if (!tournament.hasQuiz()) {
            createQuiz(tournament);
        }

        return answerService.startQuiz(userId, tournament.getQuiz().getId());
    }

    @Retryable(value = { SQLException.class }, backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void leaveTournament(Integer userId, Integer tournamentId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new TutorException(USER_NOT_FOUND, userId));
        Tournament tournament = checkTournament(tournamentId);

        tournament.removeParticipant(user);
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public TournamentDto updateTournament(Set<Integer> topicsId, TournamentDto tournamentDto) {
        Tournament tournament = checkTournament(tournamentDto.getId());

        Set<Topic> topics = new HashSet<>();
        for (Integer topicId : topicsId) {
            Topic topic = topicRepository.findById(topicId)
                    .orElseThrow(() -> new TutorException(TOPIC_NOT_FOUND, topicId));
            topics.add(topic);
        }

        tournament.updateTournament(tournamentDto, topics);

        if (tournament.hasQuiz()) { // update current Quiz
            Quiz quiz = quizRepository.findById(tournamentDto.getQuizId())
                    .orElseThrow(() -> new TutorException(QUIZ_NOT_FOUND, tournamentDto.getQuizId()));

            QuizDto quizDto = quizService.findById(tournamentDto.getQuizId());
            quizDto.setNumberOfQuestions(tournamentDto.getNumberOfQuestions());

            quizService.updateQuiz(quiz.getId(), quizDto);
        }

        return new TournamentDto(tournament);
    }

    @Retryable(value = { SQLException.class }, backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public TournamentDto cancelTournament(Integer tournamentId) {
        Tournament tournament = checkTournament(tournamentId);

        tournament.cancel();

        return new TournamentDto(tournament);
    }

    @Retryable(value = { SQLException.class }, backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void removeTournament(Integer tournamentId) {
        Tournament tournament = checkTournament(tournamentId);

        tournament.remove();

        tournamentRepository.delete(tournament);
    }

    @Retryable(value = { SQLException.class }, backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public List<StudentDto> getTournamentParticipants(TournamentDto tournamentDto) {
        Tournament tournament = checkTournament(tournamentDto.getId());

        return tournament.getParticipants().stream().map(StudentDto::new).collect(Collectors.toList());
    }

    private void checkInput(Integer userId, Set<Integer> topicsId, TournamentDto tournamentDto) {
        if (userId == null) {
            throw new TutorException(TOURNAMENT_MISSING_USER);
        }
        if (topicsId == null) {
            throw new TutorException(TOURNAMENT_MISSING_TOPICS);
        }
        if (tournamentDto.getStartTime() == null) {
            throw new TutorException(TOURNAMENT_MISSING_START_TIME);
        }
        if (tournamentDto.getEndTime() == null) {
            throw new TutorException(TOURNAMENT_MISSING_END_TIME);
        }
        if (tournamentDto.getNumberOfQuestions() == null) {
            throw new TutorException(TOURNAMENT_MISSING_NUMBER_OF_QUESTIONS);
        }
    }

    private Tournament checkTournament(Integer tournamentId) {
        return tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new TutorException(TOURNAMENT_NOT_FOUND, tournamentId));
    }

    private void createQuiz(Tournament tournament) {
        StatementTournamentCreationDto quizForm = new StatementTournamentCreationDto(tournament);

        StatementQuizDto statementQuizDto = answerService.generateTournamentQuiz(tournament.getCreator().getId(),
                tournament.getCourseExecution().getId(), quizForm, tournament);

        Quiz quiz = quizRepository.findById(statementQuizDto.getId()).orElse(null);

        tournament.setQuiz(quiz);
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void resetDemoTournaments() {
        tournamentRepository.getTournamentsForCourseExecution(courseExecutionService.getDemoCourse().getCourseExecutionId())
            .forEach(tournament -> {
                tournament.getParticipants().forEach(user -> user.removeTournament(tournament));
                if (tournament.getQuiz() != null) {
                    tournament.getQuiz().setTournament(null);
                }

                tournamentRepository.delete(tournament);
            });
    }

}
