package pt.ulisboa.tecnico.socialsoftware.tutor.answer;

import com.google.common.eventbus.EventBus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.common.dtos.answer.*;
import pt.ulisboa.tecnico.socialsoftware.common.dtos.quiz.QuizDto;
import pt.ulisboa.tecnico.socialsoftware.common.dtos.quiz.QuizType;
import pt.ulisboa.tecnico.socialsoftware.common.dtos.tournament.ExternalStatementCreationDto;
import pt.ulisboa.tecnico.socialsoftware.common.events.ExternalQuizSolvedEvent;
import pt.ulisboa.tecnico.socialsoftware.common.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.common.utils.DateHandler;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.*;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.CorrectAnswerDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.QuizAnswerDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.SolvedQuizDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.StatementCreationDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.repository.AnswerDetailsRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.repository.QuestionAnswerItemRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.repository.QuizAnswerItemRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.repository.QuizAnswerRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.CourseExecutionService;
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.Assessment;
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.CourseExecution;
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.repository.AssessmentRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.AnswersXmlExportVisitor;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.AnswersXmlImport;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository.CourseExecutionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository.QuizRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.User;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.repository.UserRepository;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static pt.ulisboa.tecnico.socialsoftware.common.dtos.question.QuestionTypes.*;
import static pt.ulisboa.tecnico.socialsoftware.common.exceptions.ErrorMessage.*;

@Service
public class AnswerService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private QuizAnswerRepository quizAnswerRepository;

    @Autowired
    private AnswerDetailsRepository answerDetailsRepository;

    @Autowired
    private QuizAnswerItemRepository quizAnswerItemRepository;

    @Autowired
    private CourseExecutionRepository courseExecutionRepository;

    @Autowired
    private QuestionAnswerItemRepository questionAnswerItemRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private AssessmentRepository assessmentRepository;

    @Autowired
    private AnswersXmlImport xmlImporter;

    @Autowired
    private CourseExecutionService courseExecutionService;

    @Autowired
    private EventBus eventBus;

    @Retryable(
            value = {SQLException.class},
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public QuizAnswerDto createQuizAnswer(Integer userId, Integer quizId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new TutorException(USER_NOT_FOUND, userId));

        Quiz quiz = quizRepository.findById(quizId).orElseThrow(() -> new TutorException(QUIZ_NOT_FOUND, quizId));

        QuizAnswer quizAnswer = new QuizAnswer(user, quiz);
        quizAnswerRepository.save(quizAnswer);

        return new QuizAnswerDto(quizAnswer);
    }

    @Retryable(
            value = {SQLException.class},
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public List<CorrectAnswerDto> concludeQuiz(StatementQuizDto statementQuizDto) {
        QuizAnswer quizAnswer = quizAnswerRepository.findById(statementQuizDto.getQuizAnswerId())
                .orElseThrow(() -> new TutorException(QUIZ_ANSWER_NOT_FOUND, statementQuizDto.getId()));

        if (quizAnswer.getQuiz().getAvailableDate() != null && quizAnswer.getQuiz().getAvailableDate().isAfter(DateHandler.now())) {
            throw new TutorException(QUIZ_NOT_YET_AVAILABLE);
        }

        if (quizAnswer.getQuiz().getConclusionDate() != null && quizAnswer.getQuiz().getConclusionDate().isBefore(DateHandler.now().minusMinutes(10))) {
            throw new TutorException(QUIZ_NO_LONGER_AVAILABLE);
        }

        if (!quizAnswer.isCompleted()) {
            quizAnswer.setCompleted(true);

            if (quizAnswer.getQuiz().getType().equals(QuizType.IN_CLASS)) {
                QuizAnswerItem quizAnswerItem = new QuizAnswerItem(statementQuizDto);
                quizAnswerItemRepository.save(quizAnswerItem);
            } else {
                quizAnswer.setAnswerDate(DateHandler.now());

                for (QuestionAnswer questionAnswer : quizAnswer.getQuestionAnswers()) {
                    writeQuestionAnswer(questionAnswer, statementQuizDto.getAnswers());
                }

                List<CorrectAnswerDto> correctAnswerDtoList = quizAnswer.getQuestionAnswers().stream()
                        .sorted(Comparator.comparing(QuestionAnswer::getSequence))
                        .map(CorrectAnswerDto::new)
                        .collect(Collectors.toList());

                if (quizAnswer.getQuiz().getType().equals(QuizType.EXTERNAL_QUIZ)) {
                    ExternalQuizSolvedEvent event = new ExternalQuizSolvedEvent(quizAnswer.getQuiz().getId(),
                            quizAnswer.getUser().getId(), quizAnswer.getNumberOfAnsweredQuestions(),
                            quizAnswer.getNumberOfCorrectAnswers());

                    eventBus.post(event);
                }

                return correctAnswerDtoList;
            }
        }
        return new ArrayList<>();
    }

    @Retryable(
            value = {SQLException.class},
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void writeQuizAnswers(Integer quizId) {
        Quiz quiz = quizRepository.findQuizWithAnswersAndQuestionsById(quizId).orElseThrow(() -> new TutorException(QUIZ_NOT_FOUND, quizId));
        Map<Integer, QuizAnswer> quizAnswersMap = quiz.getQuizAnswers().stream().collect(Collectors.toMap(QuizAnswer::getId, Function.identity()));

        List<QuizAnswerItem> quizAnswerItems = quizAnswerItemRepository.findQuizAnswerItemsByQuizId(quizId);

        quizAnswerItems.forEach(quizAnswerItem -> {
            QuizAnswer quizAnswer = quizAnswersMap.get(quizAnswerItem.getQuizAnswerId());

            if (quizAnswer.getAnswerDate() == null) {
                quizAnswer.setAnswerDate(quizAnswerItem.getAnswerDate());

                for (QuestionAnswer questionAnswer : quizAnswer.getQuestionAnswers()) {
                    writeQuestionAnswer(questionAnswer, quizAnswerItem.getAnswersList());
                }
            }
            quizAnswerItemRepository.deleteById(quizAnswerItem.getId());
        });
    }

    private void writeQuestionAnswer(QuestionAnswer questionAnswer, List<StatementAnswerDto> statementAnswerDtoList) {
        StatementAnswerDto statementAnswerDto = statementAnswerDtoList.stream()
                .filter(statementAnswerDto1 -> statementAnswerDto1.getQuestionAnswerId().equals(questionAnswer.getId()))
                .findAny()
                .orElseThrow(() -> new TutorException(QUESTION_ANSWER_NOT_FOUND, questionAnswer.getId()));

        questionAnswer.setTimeTaken(statementAnswerDto.getTimeTaken());
        AnswerDetails answer = questionAnswer.setAnswerDetails(statementAnswerDto);
        if (answer != null) {
            answerDetailsRepository.save(answer);
        }
    }

    @Retryable(
            value = {SQLException.class},
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public String exportAnswers() {
        AnswersXmlExportVisitor xmlExport = new AnswersXmlExportVisitor();

        return xmlExport.export(quizAnswerRepository.findAll());
    }

    @Retryable(
            value = {SQLException.class},
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void importAnswers(String answersXml) {
        xmlImporter.importAnswers(answersXml);
    }

    @Retryable(
            value = {SQLException.class},
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void deleteQuizAnswer(QuizAnswer quizAnswer) {
        quizAnswer.remove();
        quizAnswerRepository.delete(quizAnswer);
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 2000))
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public StatementQuizDto generateStudentQuiz(int userId, int executionId, StatementCreationDto quizDetails) {
        User user = userRepository.findById(userId).orElseThrow(() -> new TutorException(USER_NOT_FOUND, userId));

        Quiz quiz = new Quiz();
        quiz.setType(QuizType.GENERATED.toString());
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

        quiz.generateQuiz(availableQuestions);

        QuizAnswer quizAnswer = new QuizAnswer(user, quiz);

        quiz.setCourseExecution(courseExecution);

        quizRepository.save(quiz);

        return quizAnswer.getDto(false);
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 2000))
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public StatementQuizDto generateTournamentQuiz(int userId, int executionId, ExternalStatementCreationDto quizDetails) {
        User user = userRepository.findById(userId).orElseThrow(() -> new TutorException(USER_NOT_FOUND, userId));

        Quiz quiz = new Quiz();
        quiz.setType(QuizType.GENERATED.toString());
        quiz.setCreationDate(DateHandler.now());

        CourseExecution courseExecution = courseExecutionRepository.findById(executionId).orElseThrow(() -> new TutorException(COURSE_EXECUTION_NOT_FOUND, executionId));

        List<Question> availableQuestions = questionRepository.findAvailableQuestions(courseExecution.getCourse().getId());

        if (quizDetails.getTopics() != null) {
            availableQuestions = courseExecution.filterQuestionsByTopics(availableQuestions, quizDetails.getTopics());
        } else {
            availableQuestions = new ArrayList<>();
        }

        if (availableQuestions.size() < quizDetails.getNumberOfQuestions()) {
            throw new TutorException(NOT_ENOUGH_QUESTIONS_TOURNAMENT);
        }

        availableQuestions = user.filterQuestionsByStudentModel(quizDetails.getNumberOfQuestions(), availableQuestions);

        quiz.generateQuiz(availableQuestions);

        QuizAnswer quizAnswer = new QuizAnswer(user, quiz);

        quiz.setCourseExecution(courseExecution);

        if (DateHandler.now().isBefore(quizDetails.getStartTime())) {
            quiz.setAvailableDate(quizDetails.getStartTime());
        }
        quiz.setConclusionDate(quizDetails.getEndTime());
        quiz.setResultsDate(quizDetails.getEndTime());
        quiz.setTitle("Tournament " + quizDetails.getId() + " Quiz");
        quiz.setType(QuizType.EXTERNAL_QUIZ.toString());

        quizRepository.save(quiz);

        return quizAnswer.getDto(false);
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
            return quizAnswer.getDto(false);

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

        Set<Quiz> openQuizzes = quizAnswerRepository.findOpenNonQRCodeQuizAnswers(userId, executionId, now);

        return Stream.concat(quizRepository.findAvailableNonQRCodeNonGeneratedNonTournamentQuizzes(executionId, DateHandler.now()).stream(),
                openQuizzes.stream())
                .filter(quiz -> !answeredQuizIds.contains(quiz.getId()))
                .distinct()
                .map(quiz -> quiz.getDto(false))
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
                .filter(quizAnswer -> quizAnswer.getAnswerDate() != null)
                .map(SolvedQuizDto::new)
                .sorted(Comparator.comparing(SolvedQuizDto::getAnswerDate))
                .collect(Collectors.toList());
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public StatementQuestionDto getQuestionForQuizAnswer(Integer quizId, Integer questionId) {
            Question question = questionRepository.findById(questionId)
                    .orElseThrow(() -> new TutorException(QUESTION_NOT_FOUND, questionId));
            return question.getStatementQuestionDto();
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
            questionAnswerItemRepository.save(getQuestionAnswerType(username, quizId, answer));
        }
    }

    public QuestionAnswerItem getQuestionAnswerType(String username, Integer quizId, StatementAnswerDto answer) {
        switch (answer.getAnswerDetails().getType()) {
            case MULTIPLE_CHOICE_QUESTION:
                return new MultipleChoiceAnswerItem(username, quizId, answer, (MultipleChoiceStatementAnswerDetailsDto) answer.getAnswerDetails());
            case CODE_FILL_IN_QUESTION:
                return new CodeFillInAnswerItem(username, quizId, answer, (CodeFillInStatementAnswerDetailsDto) answer.getAnswerDetails());
            case CODE_ORDER_QUESTION:
                return new CodeOrderAnswerItem(username, quizId, answer, (CodeOrderStatementAnswerDetailsDto) answer.getAnswerDetails());
            default:
                throw new TutorException(INVALID_ANSWER_DETAILS, answer.toString());
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
                writeQuizAnswers(quizToWrite);
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

        Optional<QuizAnswer> optionalQuizAnswer = quizAnswerRepository.findQuizAnswer(quizId, userId);

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

        return quizAnswer.getDto(false);
    }

    public List<Question> filterByAssessment(List<Question> availableQuestions, StatementCreationDto quizDetails) {
        Assessment assessment = assessmentRepository.findById(quizDetails.getAssessment())
                .orElseThrow(() -> new TutorException(ASSESSMENT_NOT_FOUND, quizDetails.getAssessment()));

        return availableQuestions.stream().filter(question -> question.belongsToAssessment(assessment)).collect(Collectors.toList());
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void resetDemoAnswers() {
        Set<QuestionAnswerItem> questionAnswerItems = questionAnswerItemRepository.findDemoStudentQuestionAnswerItems();
        questionAnswerItems.forEach(questionAnswerItem -> questionAnswerItemRepository.delete(questionAnswerItem));

        Set<QuizAnswer> quizAnswers = quizAnswerRepository.findByExecutionCourseId(courseExecutionService.getDemoCourse().getCourseExecutionId());

        quizAnswers.forEach(quizAnswer -> {
                quizAnswer.remove();
                quizAnswerRepository.delete(quizAnswer);
        });
    }

}
