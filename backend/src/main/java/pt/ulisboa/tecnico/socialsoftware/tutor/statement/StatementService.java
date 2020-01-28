package pt.ulisboa.tecnico.socialsoftware.tutor.statement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.AnswerService;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.CorrectAnswersDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.ResultAnswersDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecutionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Assessment;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.AssessmentRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.QuizService;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository.QuizRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.statement.dto.SolvedQuizDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.statement.dto.StatementCreationDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.statement.dto.StatementQuizDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.Valid;
import java.sql.SQLException;
import java.time.LocalDateTime;
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
        quiz.setNumber(quizService.getMaxQuizNumber() + 1);

        Course course = courseExecutionRepository.findById(executionId).orElseThrow(() -> new TutorException(COURSE_EXECUTION_NOT_FOUND, executionId)).getCourse();

        List<Question> availableQuestions = questionRepository.findAvailableQuestions(course.getName());

        availableQuestions = filterByAssessment(availableQuestions, quizDetails, user);
//        availableQuestions = filterCorrectlyAnsweredQuestions(availableQuestions, quizDetails, user);
//        availableQuestions = filterAnsweredQuestions(availableQuestions, quizDetails, user);

        if (availableQuestions.size() < quizDetails.getNumberOfQuestions()) {
            throw new TutorException(NOT_ENOUGH_QUESTIONS);
        }

        availableQuestions = user.filterQuestionsByStudentModel(quizDetails.getNumberOfQuestions(), availableQuestions);

        quiz.generate(availableQuestions);

        QuizAnswer quizAnswer = new QuizAnswer(user, quiz);

        entityManager.persist(quiz);
        entityManager.persist(quizAnswer);

        return new StatementQuizDto(quizAnswer);
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

        quizRepository.findAvailableQuizzes(executionId).stream()
                .filter(quiz -> quiz.getCourseExecution().getId() == executionId)
                .filter(quiz -> quiz.getAvailableDate().isBefore(now))
                .filter(quiz -> !studentQuizIds.contains(quiz.getId()))
                .forEach(quiz ->  {
                    QuizAnswer quizAnswer = new QuizAnswer(user, quiz);
                    if (quiz.getConclusionDate() != null && quiz.getConclusionDate().isBefore(now)) {
                        quizAnswer.setCompleted(true);
                    }
                    entityManager.persist(quizAnswer);
                });

        return user.getQuizAnswers().stream()
                .filter(quizAnswer -> quizAnswer.getQuiz().getCourseExecution().getId() == executionId)
                .filter(quizAnswer -> !quizAnswer.getCompleted())
                .filter(quizAnswer -> quizAnswer.getQuiz().getType().equals(Quiz.QuizType.TEACHER))
                .map(StatementQuizDto::new)
                .sorted(Comparator.comparing(StatementQuizDto::getAvailableDate))
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
                .filter(quizAnswer -> quizAnswer.getCompleted() && quizAnswer.getQuiz().getCourseExecution() == courseExecution)
                .filter(QuizAnswer::getCompleted)
                .map(SolvedQuizDto::new)
                .sorted(Comparator.comparing(SolvedQuizDto::getAnswerDate))
                .collect(Collectors.toList());
    }

    @Retryable(
      value = { SQLException.class },
      backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public CorrectAnswersDto solveQuiz(String username, @Valid @RequestBody ResultAnswersDto answers) {
        User user = userRepository.findByUsername(username);

        return answerService.submitQuestionsAnswers(user, answers);
    }

    public List<Question> filterByAssessment(List<Question> availableQuestions, StatementCreationDto quizDetails, User user) {
        if (!quizDetails.getAssessment().equals("all")) {
            Assessment assessment = assessmentRepository.findById(Integer.valueOf(quizDetails.getAssessment()))
                    .orElseThrow(() -> new TutorException(ASSESSMENT_NOT_FOUND, Integer.valueOf(quizDetails.getAssessment())));

            return availableQuestions.stream().filter(question -> question.belongsToAssessment(assessment)).collect(Collectors.toList());
        }
        return availableQuestions;
    }

    public List<Question> filterCorrectlyAnsweredQuestions(List<Question> availableQuestions, StatementCreationDto quizDetails, User user) {
        if (quizDetails.getQuestionType().equals("failed")) {
            List<Question> failedQuestions = user.getQuizAnswers().stream()
                    .map(QuizAnswer::getQuiz)
                    .flatMap(q -> q.getQuizQuestions().stream())
                    .map(QuizQuestion::getQuestion)
                    .collect(Collectors.toList());

            return availableQuestions.stream().filter(failedQuestions::contains).collect(Collectors.toList());
        }
        return availableQuestions;
    }

    public List<Question> filterAnsweredQuestions(List<Question> availableQuestions, StatementCreationDto quizDetails, User user) {
        if (quizDetails.getQuestionType().equals("new")) {
            List<Question> failedQuestions = user.getQuizAnswers().stream()
                    .map(QuizAnswer::getQuiz)
                    .flatMap(q -> q.getQuizQuestions().stream())
                    .map(QuizQuestion::getQuestion)
                    .collect(Collectors.toList());

            return availableQuestions.stream().filter(failedQuestions::contains).collect(Collectors.toList());
        }
        return availableQuestions;
    }
}
