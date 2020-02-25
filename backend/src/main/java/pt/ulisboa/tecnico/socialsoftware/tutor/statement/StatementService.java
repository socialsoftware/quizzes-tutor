package pt.ulisboa.tecnico.socialsoftware.tutor.statement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.AnswerService;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.CorrectAnswerDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.repository.QuizAnswerRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecutionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Assessment;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.AssessmentRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.QuizService;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository.QuizRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.statement.dto.SolvedQuizDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.statement.dto.StatementAnswerDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.statement.dto.StatementCreationDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.statement.dto.StatementQuizDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*;

@Service
public class StatementService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CourseExecutionRepository courseExecutionRepository;

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private QuizAnswerRepository quizAnswerRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private AssessmentRepository assessmentRepository;

    @Autowired
    private QuizService quizService;

    @Autowired
    private AnswerService answerService;

    @PersistenceContext
    EntityManager entityManager;

    @Retryable(
      value = { SQLException.class },
      backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public StatementQuizDto generateStudentQuiz(String username, int executionId, StatementCreationDto quizDetails) {
        User user = userRepository.findByUsername(username);

        Quiz quiz = new Quiz();
        quiz.setKey(quizService.getMaxQuizKey() + 1);
        quiz.setType(Quiz.QuizType.GENERATED);
        quiz.setCreationDate(LocalDateTime.now());

        CourseExecution courseExecution = courseExecutionRepository.findById(executionId).orElseThrow(() -> new TutorException(COURSE_EXECUTION_NOT_FOUND, executionId));

        List<Question> availableQuestions = questionRepository.findAvailableQuestions(courseExecution.getCourse().getId());

        availableQuestions = filterByAssessment(availableQuestions, quizDetails, user);

        if (availableQuestions.size() < quizDetails.getNumberOfQuestions()) {
            throw new TutorException(NOT_ENOUGH_QUESTIONS);
        }

        availableQuestions = user.filterQuestionsByStudentModel(quizDetails.getNumberOfQuestions(), availableQuestions);

        quiz.generate(availableQuestions);

        QuizAnswer quizAnswer = new QuizAnswer(user, quiz);

        quiz.setCourseExecution(courseExecution);
        courseExecution.addQuiz(quiz);

        entityManager.persist(quiz);
        entityManager.persist(quizAnswer);

        return new StatementQuizDto(quizAnswer);
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public StatementQuizDto getEvaluationQuiz(String username, int quizId) {
        User user = userRepository.findByUsername(username);
        Quiz quiz = quizRepository.findById(quizId).orElseThrow(() -> new TutorException(QUIZ_NOT_FOUND, quizId));

        if (!user.getCourseExecutions().contains(quiz.getCourseExecution())) {
            throw new TutorException(USER_NOT_ENROLLED, username);
        }

        if (quiz.getConclusionDate() != null && LocalDateTime.now().isAfter(quiz.getConclusionDate())) {
            throw new TutorException(QUIZ_NO_LONGER_AVAILABLE);
        }

        QuizAnswer quizAnswer = quizAnswerRepository.findQuizAnswer(quiz.getId(), user.getId()).orElseGet(() -> {
            QuizAnswer qa = new QuizAnswer(user, quiz);
            entityManager.persist(qa);
            return qa;
        });

        if (quizAnswer.getCompleted()) {
            throw new TutorException(QUIZ_ALREADY_COMPLETED);

        } else if (quiz.getAvailableDate() == null || LocalDateTime.now().isAfter(quiz.getAvailableDate())) {
            return new StatementQuizDto(quizAnswer);

        // Send timer
        } else {
            StatementQuizDto quizDto = new StatementQuizDto();
            quizDto.setSecondsToAvailability(ChronoUnit.SECONDS.between(LocalDateTime.now(), quiz.getAvailableDate()));
            return quizDto;
        }
    }

    @Retryable(
      value = { SQLException.class },
      backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public List<StatementQuizDto> getAvailableQuizzes(String username, int executionId) {
        User user = userRepository.findByUsername(username);

        LocalDateTime now = LocalDateTime.now();

        Set<Integer> studentQuizIds =  user.getQuizAnswers().stream()
                .filter(quizAnswer -> quizAnswer.getQuiz().getCourseExecution().getId() == executionId)
                .map(QuizAnswer::getQuiz)
                .map(Quiz::getId)
                .collect(Collectors.toSet());

        // create QuizAnswer for quizzes
        quizRepository.findQuizzes(executionId).stream()
                .filter(quiz -> quiz.getAvailableDate() == null || quiz.getAvailableDate().isBefore(now))
                .filter(quiz -> !studentQuizIds.contains(quiz.getId()))
                .filter(quiz -> !quiz.getType().equals(Quiz.QuizType.IN_CLASS))
                .filter(quiz -> !quiz.getType().equals(Quiz.QuizType.GENERATED))
                .forEach(quiz ->  {
                    if (quiz.getConclusionDate() == null || quiz.getConclusionDate().isAfter(now)) {
                        QuizAnswer quizAnswer = new QuizAnswer(user, quiz);
                        entityManager.persist(quizAnswer);
                    }
                });

        return user.getQuizAnswers().stream()
                .filter(quizAnswer -> quizAnswer.getQuiz().getConclusionDate() == null || LocalDateTime.now().isBefore(quizAnswer.getQuiz().getConclusionDate()))
                .filter(quizAnswer -> quizAnswer.getQuiz().getCourseExecution().getId() == executionId)
                .filter(quizAnswer -> quizAnswer.getQuiz().getAvailableDate() == null || !(quizAnswer.getQuiz().getType().equals(Quiz.QuizType.IN_CLASS) && quizAnswer.getQuiz().getAvailableDate().isAfter(now)))
                .filter(quizAnswer -> !quizAnswer.getCompleted())
                .map(StatementQuizDto::new)
                .sorted(Comparator.comparing(StatementQuizDto::getAvailableDate, Comparator.nullsLast(Comparator.naturalOrder())))
                .collect(Collectors.toList());
    }

    @Retryable(
      value = { SQLException.class },
      backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public List<SolvedQuizDto> getSolvedQuizzes(String username, int executionId) {
        User user = userRepository.findByUsername(username);
        CourseExecution courseExecution = courseExecutionRepository.findById(executionId).orElseThrow(() -> new TutorException(COURSE_EXECUTION_NOT_FOUND, executionId));

        return user.getQuizAnswers().stream()
                .filter(quizAnswer -> quizAnswer.canResultsBePublic(courseExecution))
                .map(SolvedQuizDto::new)
                .sorted(Comparator.comparing(SolvedQuizDto::getAnswerDate))
                .collect(Collectors.toList());
    }

    @Retryable(
      value = { SQLException.class },
      backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public List<CorrectAnswerDto> concludeQuiz(String username, Integer quizId) {
        User user = userRepository.findByUsername(username);

        return answerService.concludeQuiz(user, quizId);
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void submitAnswer(String username, Integer quizId, StatementAnswerDto answer) {
        User user = userRepository.findByUsername(username);

        answerService.submitAnswer(user, quizId, answer);
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void completeOpenQuizAnswers() {
        Set<QuizAnswer> quizAnswersToClose = quizAnswerRepository.findQuizAnswersToClose(LocalDateTime.now());

        quizAnswersToClose.forEach(quizAnswer -> {
            if (!quizAnswer.isCompleted()) {
                quizAnswer.setAnswerDate(quizAnswer.getQuiz().getConclusionDate());
                quizAnswer.setCompleted(true);
            }

            quizAnswer.calculateStatistics();
        });
    }

    public List<Question> filterByAssessment(List<Question> availableQuestions, StatementCreationDto quizDetails, User user) {
        // remove all by default
        // TODO: support a ALL assessment
        //if (!quizDetails.getAssessment().equals("all")) {
            Assessment assessment = assessmentRepository.findById(Integer.valueOf(quizDetails.getAssessment()))
                    .orElseThrow(() -> new TutorException(ASSESSMENT_NOT_FOUND, Integer.valueOf(quizDetails.getAssessment())));

            return availableQuestions.stream().filter(question -> question.belongsToAssessment(assessment)).collect(Collectors.toList());
       // }
       // return availableQuestions;
    }

}
