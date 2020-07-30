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
import pt.ulisboa.tecnico.socialsoftware.tutor.config.DateHandler;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecutionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Assessment;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.AssessmentRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.QuizService;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.dto.QuizDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository.QuizRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.statement.dto.*;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
    private QuizAnswerItemRepository quizAnswerItemRepository;

    @Autowired
    private QuestionAnswerItemRepository questionAnswerItemRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private AssessmentRepository assessmentRepository;

    @Autowired
    private QuizService quizService;

    @Autowired
    private AnswerService answerService;

    @Retryable(
      value = { SQLException.class },
      backoff = @Backoff(delay = 2000))
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public StatementQuizDto generateStudentQuiz(int userId, int executionId, StatementCreationDto quizDetails) {
        User user = userRepository.findById(userId).orElseThrow(() -> new TutorException(USER_NOT_FOUND, userId));

        Quiz quiz = new Quiz();
        quiz.setKey(quizService.getMaxQuizKey() + 1);
        quiz.setType(Quiz.QuizType.GENERATED.toString());
        quiz.setCreationDate(DateHandler.now());

        CourseExecution courseExecution = courseExecutionRepository.findById(executionId).orElseThrow(() -> new TutorException(COURSE_EXECUTION_NOT_FOUND, executionId));

        List<Question> availableQuestions = questionRepository.findAvailableQuestions(courseExecution.getCourse().getId());

        if (quizDetails.getAssessment() != null) {
            availableQuestions = filterByAssessment(availableQuestions, quizDetails);
        } else {
            availableQuestions = new ArrayList<>();
        }

        if (availableQuestions.size() < quizDetails.getNumberOfQuestions()) {
            throw new TutorException(NOT_ENOUGH_QUESTIONS);
        }

        availableQuestions = user.filterQuestionsByStudentModel(quizDetails.getNumberOfQuestions(), availableQuestions);

        quiz.generate(availableQuestions);

        QuizAnswer quizAnswer = new QuizAnswer(user, quiz);

        quiz.setCourseExecution(courseExecution);

        quizRepository.save(quiz);
        quizAnswerRepository.save(quizAnswer);

        return new StatementQuizDto(quizAnswer);
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 2000))
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public StatementQuizDto getQuizByQRCode(int userId, int quizId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new TutorException(USER_NOT_FOUND, userId));
        Quiz quiz = quizRepository.findById(quizId).orElseThrow(() -> new TutorException(QUIZ_NOT_FOUND, quizId));

        if (!quiz.isQrCodeOnly()) {
            throw new TutorException(NOT_QRCODE_QUIZ);
        }

        if (!user.getCourseExecutions().contains(quiz.getCourseExecution())) {
            throw new TutorException(USER_NOT_ENROLLED, user.getUsername());
        }

        if (quiz.getConclusionDate() != null && DateHandler.now().isAfter(quiz.getConclusionDate())) {
            throw new TutorException(QUIZ_NO_LONGER_AVAILABLE);
        }

        QuizAnswer quizAnswer = quizAnswerRepository.findQuizAnswer(quiz.getId(), user.getId()).orElseGet(() -> {
            QuizAnswer qa = new QuizAnswer(user, quiz);
            quizAnswerRepository.save(qa);
            return qa;
        });

        if (!quizAnswer.openToAnswer()) {
            throw new TutorException(QUIZ_ALREADY_COMPLETED);
        }

        if (quiz.getAvailableDate() == null || DateHandler.now().isAfter(quiz.getAvailableDate())) {
            if (quizAnswer.getCreationDate() == null) {
                quizAnswer.setCreationDate(DateHandler.now());
            }
            return new StatementQuizDto(quizAnswer);

        // Send timer
        } else {
            StatementQuizDto quizDto = new StatementQuizDto();
            quizDto.setTimeToAvailability(ChronoUnit.MILLIS.between(DateHandler.now(), quiz.getAvailableDate()));
            return quizDto;
        }
    }

    @Retryable(
      value = { SQLException.class },
      backoff = @Backoff(delay = 2000))
    @Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true)
    public List<QuizDto> getAvailableQuizzes(int userId, int executionId) {
        LocalDateTime now = DateHandler.now();
        Set<Integer> answeredQuizIds = quizAnswerRepository.findClosedQuizAnswersQuizIds(userId, executionId, now);

        Stream<Quiz> availableQuizzes = quizRepository.findAvailableNonQRCodeQuizzes(executionId, DateHandler.now()).stream()
                .filter(quiz -> !answeredQuizIds.contains(quiz.getId()));

        return Stream.concat(availableQuizzes, quizAnswerRepository.findOpenQRCodeQuizzes(userId, executionId))
                .map(quiz -> new QuizDto(quiz, false))
                .sorted(Comparator.comparing(QuizDto::getAvailableDate, Comparator.nullsLast(Comparator.naturalOrder())))
                .collect(Collectors.toList());
    }

    @Retryable(
      value = { SQLException.class },
      backoff = @Backoff(delay = 2000))
    @Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true)
    public List<SolvedQuizDto> getSolvedQuizzes(int userId, int executionId) {
        User user = userRepository.findUserWithQuizAnswersAndQuestionAnswersById(userId).orElseThrow(() -> new TutorException(USER_NOT_FOUND, userId));

        return user.getQuizAnswers().stream()
                .filter(quizAnswer -> quizAnswer.canResultsBePublic(executionId))
                .map(SolvedQuizDto::new)
                .sorted(Comparator.comparing(SolvedQuizDto::getAnswerDate))
                .collect(Collectors.toList());
    }

    @Retryable(
      value = { SQLException.class },
      backoff = @Backoff(delay = 2000))
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public List<CorrectAnswerDto> concludeQuiz(StatementQuizDto statementQuizDto) {
        return answerService.concludeQuiz(statementQuizDto);
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 2000))
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    public void submitAnswer(String username, int quizId, StatementAnswerDto answer) {
        if (answer.getTimeToSubmission() == null) {
            answer.setTimeToSubmission(0);
        }

        if (answer.emptyAnswer()) {
            questionAnswerItemRepository.insertQuestionAnswerItemOptionIdNull(username, quizId, answer.getQuizQuestionId(), DateHandler.now(),
                    answer.getTimeTaken(), answer.getTimeToSubmission());
        } else {
            questionAnswerItemRepository.save(answer.getQuestionAnswerItem(username, quizId));
        }
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 2000))
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void writeQuizAnswersAndCalculateStatistics() {
        Set<Integer> quizzesToWrite = quizAnswerItemRepository.findQuizzesToWrite();
        quizzesToWrite.forEach(quizToWrite -> {
            if (quizRepository.findById(quizToWrite).isPresent()) {
                answerService.writeQuizAnswers(quizToWrite);
            }
        });

        Set<QuizAnswer> quizAnswersToClose = quizAnswerRepository.findQuizAnswersToCalculateStatistics(DateHandler.now());

        quizAnswersToClose.forEach(QuizAnswer::calculateStatistics);
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 2000))
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public StatementQuizDto startQuiz(int userId, int quizId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new TutorException(USER_NOT_FOUND, userId));
        Quiz quiz = quizRepository.findById(quizId).orElseThrow(() -> new TutorException(QUIZ_NOT_FOUND, quizId));

        if (!user.getCourseExecutions().contains(quiz.getCourseExecution())) {
            throw new TutorException(USER_NOT_ENROLLED, user.getUsername());
        }

        if (quiz.getAvailableDate() != null && quiz.getAvailableDate().isAfter(DateHandler.now())) {
            throw new TutorException(QUIZ_NOT_YET_AVAILABLE);
        }

        if (quiz.getConclusionDate() != null && DateHandler.now().isAfter(quiz.getConclusionDate())) {
            throw new TutorException(QUIZ_NO_LONGER_AVAILABLE);
        }

        Optional<QuizAnswer> optionalQuizAnswer = quizAnswerRepository.findQuizAnswer(quizId, user.getId());

        if (!optionalQuizAnswer.isPresent() && quiz.isQrCodeOnly()) {
            throw new TutorException(CANNOT_START_QRCODE_QUIZ);
        }

        QuizAnswer quizAnswer = optionalQuizAnswer.orElseGet(() -> {
            QuizAnswer qa = new QuizAnswer(user, quiz);
            quizAnswerRepository.save(qa);
            return qa;
        });

        if (!quizAnswer.openToAnswer()) {
            throw new TutorException(QUIZ_ALREADY_COMPLETED);
        }

        if (quizAnswer.getCreationDate() == null) {
            quizAnswer.setCreationDate(DateHandler.now());
        }

        return new StatementQuizDto(quizAnswer);
    }

    public List<Question> filterByAssessment(List<Question> availableQuestions, StatementCreationDto quizDetails) {
        Assessment assessment = assessmentRepository.findById(quizDetails.getAssessment())
                .orElseThrow(() -> new TutorException(ASSESSMENT_NOT_FOUND, quizDetails.getAssessment()));

        return availableQuestions.stream().filter(question -> question.belongsToAssessment(assessment)).collect(Collectors.toList());
    }
}
