package pt.ulisboa.tecnico.socialsoftware.tutor.tournament;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.config.DateHandler;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.AssessmentService;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Assessment;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.AssessmentDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.TopicConjunctionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.TopicDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.AssessmentRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.TopicRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository.QuizRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.statement.StatementService;
import pt.ulisboa.tecnico.socialsoftware.tutor.statement.dto.StatementCreationDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.statement.dto.StatementQuizDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.domain.Tournament;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.dto.TournamentDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.repository.TournamentRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.StudentDto;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*;

@Service
public class TournamentService {

    @Autowired
    private AssessmentService assessmentService;

    @Autowired
    private AssessmentRepository assessmentRepository;

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private StatementService statementService;

    @Autowired
    private TopicRepository topicRepository;

    @Autowired
    private TournamentRepository tournamentRepository;

    @Autowired
    private UserRepository userRepository;

    @PersistenceContext
    EntityManager entityManager;

    @Retryable(value = { SQLException.class }, backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public TournamentDto createTournament(Integer userId, Set<Integer> topicsId, TournamentDto tournamentDto) {
        checkInput(userId, topicsId, tournamentDto);
        User user = checkUser(userId);

        List<TopicDto> topicDtos = new ArrayList<>();
        for (Integer topicId : topicsId) {
            Topic topic = topicRepository.findById(topicId)
                    .orElseThrow(() -> new TutorException(TOPIC_NOT_FOUND, topicId));

            TopicDto topicDto = new TopicDto(topic);
            topicDtos.add(topicDto);
        }

        if (topicDtos.isEmpty()) {
            throw new TutorException(TOURNAMENT_MISSING_TOPICS);
        }

        List<TopicConjunctionDto> topicConjunctions = new ArrayList<>();
        TopicConjunctionDto topicConjunctionDto = new TopicConjunctionDto();
        topicConjunctionDto.setTopics(topicDtos);
        topicConjunctions.add(topicConjunctionDto);

        AssessmentDto assessmentDto = new AssessmentDto();
        assessmentDto.setTitle("Tournament");
        assessmentDto.setStatus(Assessment.Status.TOURNAMENT.name());
        assessmentDto.setTopicConjunctions(topicConjunctions);
        assessmentService.createAssessment(user.getCourseExecutions().iterator().next().getId(), assessmentDto);

        Assessment assessment = assessmentRepository.findTournamentAssessment();

        Tournament tournament = new Tournament(user, assessment, tournamentDto);
        this.entityManager.persist(tournament);

        tournament.getAssessment().setTitle("Tournament " + tournament.getId() + " Assessment");

        return new TournamentDto(tournament);
    }

    @Retryable(value = { SQLException.class }, backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public List<TournamentDto> getAllTournaments(User user) {
        Set<CourseExecution> set = user.getCourseExecutions();
        return tournamentRepository.getAllTournaments(set).stream().map(TournamentDto::new)
                .collect(Collectors.toList());
    }

    @Retryable(value = { SQLException.class }, backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public List<TournamentDto> getOpenedTournaments(User user) {
        Set<CourseExecution> set = user.getCourseExecutions();
        return tournamentRepository.getOpenedTournaments(set).stream().map(TournamentDto::new)
                .collect(Collectors.toList());
    }

    @Retryable(value = { SQLException.class }, backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public List<TournamentDto> getClosedTournaments(User user) {
        Set<CourseExecution> set = user.getCourseExecutions();
        return tournamentRepository.getClosedTournaments(set).stream().map(TournamentDto::new)
                .collect(Collectors.toList());
    }

    @Retryable(value = { SQLException.class }, backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public List<TournamentDto> getUserTournaments(User user) {
        return tournamentRepository.getUserTournaments(user.getId()).stream().map(TournamentDto::new)
                .collect(Collectors.toList());
    }

    @Retryable(value = { SQLException.class }, backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void joinTournament(Integer userId, TournamentDto tournamentDto, String password) {
        User user = checkUser(userId);
        Tournament tournament = checkTournament(tournamentDto);

        if (DateHandler.now().isAfter(tournament.getEndTime())) {
            throw new TutorException(TOURNAMENT_NOT_OPEN, tournament.getId());
        }

        if (tournament.getState() == Tournament.Status.CANCELED) {
            throw new TutorException(TOURNAMENT_CANCELED, tournament.getId());
        }

        if (tournament.getParticipants().contains(user)) {
            throw new TutorException(DUPLICATE_TOURNAMENT_PARTICIPANT, user.getUsername());
        }

        if (!user.getCourseExecutions().contains(tournament.getCourseExecution())) {
            throw new TutorException(STUDENT_NO_COURSE_EXECUTION, user.getId());
        }

        if (tournament.isPrivateTournament() && !password.equals(tournament.getPassword())) {
            throw new TutorException(WRONG_TOURNAMENT_PASSWORD, tournament.getId());
        }

        tournament.addParticipant(user);

        if (!tournament.hasQuiz()) {
            StatementCreationDto quizForm = new StatementCreationDto();
            quizForm.setNumberOfQuestions(tournament.getNumberOfQuestions());
            quizForm.setAssessment(tournament.getAssessment().getId());

            StatementQuizDto statementQuizDto = statementService.generateStudentQuiz(tournament.getCreator().getId(),
                    tournament.getCourseExecution().getId(), quizForm);

            Quiz quiz = quizRepository.findById(statementQuizDto.getId())
                    .orElseThrow(() -> new TutorException(QUIZ_NOT_FOUND, statementQuizDto.getId()));

            if (DateHandler.now().isBefore(tournament.getStartTime())) {
                quiz.setAvailableDate(tournament.getStartTime());
            }
            quiz.setConclusionDate(tournament.getEndTime());
            quiz.setTitle("Tournament " + tournament.getId() + " Quiz");
            quiz.setType(Quiz.QuizType.TOURNAMENT.toString());

            tournament.setQuizId(statementQuizDto.getId());
        }
    }

    @Retryable(value = { SQLException.class }, backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public StatementQuizDto solveQuiz(Integer userId, TournamentDto tournamentDto) {
        User user = checkUser(userId);
        Tournament tournament = checkTournament(tournamentDto);

        if (!tournament.getParticipants().contains(user)) {
            throw new TutorException(USER_NOT_JOINED, userId);
        }

        if (!tournament.hasQuiz()) {
            throw new TutorException(TOURNAMENT_NO_QUIZ, tournamentDto.getId());
        }

        return statementService.startQuiz(userId, tournament.getQuizId());
    }

    @Retryable(value = { SQLException.class }, backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void leaveTournament(Integer userId, TournamentDto tournamentDto) {
        User user = userRepository.findById(userId).orElseThrow(() -> new TutorException(USER_NOT_FOUND, userId));

        Tournament tournament = tournamentRepository.findById(tournamentDto.getId())
                .orElseThrow(() -> new TutorException(TOURNAMENT_NOT_FOUND, tournamentDto.getId()));

        if (!tournament.getParticipants().contains(user)) {
            throw new TutorException(USER_NOT_JOINED, user.getUsername());
        }

        tournament.removeParticipant(user);
    }

    @Retryable(value = { SQLException.class }, backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public List<StudentDto> getTournamentParticipants(TournamentDto tournamentDto) {
        Tournament tournament = checkTournament(tournamentDto);

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

    private User checkUser(Integer userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new TutorException(USER_NOT_FOUND, userId));

        if (user.getRole() != User.Role.STUDENT) {
            throw new TutorException(USER_NOT_STUDENT, user.getId());
        }

        return user;
    }

    private Tournament checkTournament(TournamentDto tournamentDto) {
        return tournamentRepository.findById(tournamentDto.getId())
                .orElseThrow(() -> new TutorException(TOURNAMENT_NOT_FOUND, tournamentDto.getId()));
    }
}
