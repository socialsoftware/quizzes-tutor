package pt.ulisboa.tecnico.socialsoftware.tutor.answer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.*;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.*;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.repository.AnswerDetailsRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.repository.QuestionAnswerItemRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.repository.QuizAnswerItemRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.repository.QuizAnswerRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.CourseExecutionService;
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.Assessment;
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.CourseExecution;
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.repository.AssessmentRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.repository.CourseExecutionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.AnswersXmlExportVisitor;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.AnswersXmlImport;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.dto.QuizDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository.QuizRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.domain.Tournament;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.Student;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.Teacher;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.repository.StudentRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.utils.DateHandler;
import pt.ulisboa.tecnico.socialsoftware.tutor.utils.Mailer;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*;

@Service
public class AnswerService {
    @Autowired
    private Mailer mailer;

    @Value("${spring.mail.username}")
    private String mailUsername;

    @Autowired
    private StudentRepository studentRepository;

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

    @Retryable(
            value = {SQLException.class},
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public QuizAnswerDto createQuizAnswer(Integer userId, Integer quizId) {
        Student student = studentRepository.findById(userId).orElseThrow(() -> new TutorException(USER_NOT_FOUND, userId));

        Quiz quiz = quizRepository.findById(quizId).orElseThrow(() -> new TutorException(QUIZ_NOT_FOUND, quizId));

        QuizAnswer quizAnswer = new QuizAnswer(student, quiz);
        quizAnswerRepository.save(quizAnswer);

        return new QuizAnswerDto(quizAnswer);
    }

    @Retryable(
            value = {SQLException.class},
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public List<CorrectAnswerDto> concludeQuiz(StatementQuizDto statementQuizDto) {
        QuizAnswer quizAnswer = quizAnswerRepository.findById(statementQuizDto.getQuizAnswerId())
                .orElseThrow(() -> new TutorException(QUIZ_ANSWER_NOT_FOUND, statementQuizDto.getQuizAnswerId()));

        if (quizAnswer.getQuiz().getAvailableDate() != null && quizAnswer.getQuiz().getAvailableDate().isAfter(DateHandler.now())) {
            throw new TutorException(QUIZ_NOT_YET_AVAILABLE);
        }

        if (quizAnswer.getQuiz().getConclusionDate() != null && quizAnswer.getQuiz().getConclusionDate().isBefore(DateHandler.now().minusMinutes(10))) {
            throw new TutorException(QUIZ_NO_LONGER_AVAILABLE);
        }

        if (!quizAnswer.isCompleted()) {
            quizAnswer.setCompleted(true);

            if (quizAnswer.getQuiz().getType().equals(Quiz.QuizType.IN_CLASS)) {
                QuizAnswerItem quizAnswerItem = new QuizAnswerItem(statementQuizDto);
                quizAnswerItemRepository.save(quizAnswerItem);
            } else {
                quizAnswer.setAnswerDate(DateHandler.now());

                for (QuestionAnswer questionAnswer : quizAnswer.getQuestionAnswers()) {
                    writeQuestionAnswer(questionAnswer, statementQuizDto.getAnswers());
                }
                return quizAnswer.getQuestionAnswers().stream()
                        .sorted(Comparator.comparing(QuestionAnswer::getSequence))
                        .map(CorrectAnswerDto::new)
                        .collect(Collectors.toList());
            }
        }
        return new ArrayList<>();
    }

    @Retryable(
            value = {SQLException.class},
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void writeQuizAnswers(Integer quizId) {
        Quiz quiz = quizRepository.findById(quizId).orElseThrow(() -> new TutorException(QUIZ_NOT_FOUND, quizId));

        List<QuizAnswerItem> quizAnswerItems = quizAnswerItemRepository.findQuizAnswerItemsByQuizId(quizId);

        Map<String,List<QuestionAnswerItem>> questionAnswerMap =  questionAnswerItemRepository.findQuestionAnswerItemsByQuizId(quiz.getId()).stream()
                .collect(Collectors.groupingBy(
                        QuestionAnswerItem::getUsername,
                        Collectors.mapping(qai -> qai, Collectors.toList())
                ));

        quizAnswerItems.forEach(quizAnswerItem -> {
            QuizAnswer quizAnswer = quizAnswerRepository.getById(quizAnswerItem.getQuizAnswerId());

            if (quiz.isOneWay()) {
                List<QuestionAnswerItem> questionAnswerItems = questionAnswerMap.get(quizAnswer.getStudent().getUsername());
                if (questionAnswerItems != null && !questionAnswerItems.isEmpty()) {
                    fraudDetection(quizAnswer, questionAnswerItems.stream()
                            .sorted(Comparator.comparing(QuestionAnswerItem::getAnswerDate))
                            .collect(Collectors.toList()));
                }
           }

            if (quizAnswer.getAnswerDate() == null) {
                quizAnswer.setAnswerDate(quizAnswerItem.getAnswerDate());

                for (QuestionAnswer questionAnswer : quizAnswer.getQuestionAnswers()) {
                    writeQuestionAnswer(questionAnswer, quizAnswerItem.getAnswersList());
                }
            }
            quizAnswerItemRepository.deleteById(quizAnswerItem.getId());
        });
    }

    private void fraudDetection(QuizAnswer quizAnswer, List<QuestionAnswerItem> questionAnswerItems) {
        List<QuestionAnswerItem> finalItems = new ArrayList<>();
        for (int i = 0; i < questionAnswerItems.size() - 1; i++) {
            if (!questionAnswerItems.get(i).getQuizQuestionId().equals(questionAnswerItems.get(i+1).getQuizQuestionId())) {
                finalItems.add(questionAnswerItems.get(i));
            }
        }
        finalItems.add(questionAnswerItems.get(questionAnswerItems.size() - 1));

        if (finalItems.size() > quizAnswer.getQuestionAnswers().size()) {
            quizAnswer.setFraud(true);
        } else {
            List<QuizQuestion> quizQuestions = quizAnswer.getQuestionAnswers().stream()
                    .sorted(Comparator.comparing(QuestionAnswer::getSequence))
                    .map(QuestionAnswer::getQuizQuestion)
                    .collect(Collectors.toList());
            for (int i = 0; i < finalItems.size(); i++) {
                if (!finalItems.get(i).getQuizQuestionId().equals(quizQuestions.get(i).getId())) {
                    quizAnswer.setFraud(true);
                }
            }
        }

        if (quizAnswer.isFraud()) {
            Student student = quizAnswer.getStudent();
            for (Teacher teacher : quizAnswer.getQuiz().getCourseExecution().getTeachers()) {
                mailer.sendSimpleMail(mailUsername, teacher.getEmail(),
                        Mailer.QUIZZES_TUTOR_SUBJECT + " Fraud Suspicion",
                        "Student " + student.getName() + "(" + student.getUsername() + ") may have answered OneWay quiz " +
                                quizAnswer.getQuiz().getTitle() + " on " + quizAnswer.getCreationDate() + " by not following the predefined order. Check the log in the exported .csv file");
            }
        }
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
        Student student = studentRepository.findById(userId).orElseThrow(() -> new TutorException(USER_NOT_FOUND, userId));

        Quiz quiz = new Quiz();
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

        availableQuestions = student.filterQuestionsByStudentModel(quizDetails.getNumberOfQuestions(), availableQuestions);

        quiz.generateQuiz(availableQuestions);

        QuizAnswer quizAnswer = new QuizAnswer(student, quiz);

        quiz.setCourseExecution(courseExecution);

        quizRepository.save(quiz);

        return new StatementQuizDto(quizAnswer, false);
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 2000))
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public StatementQuizDto generateTournamentQuiz(int userId, int executionId, StatementTournamentCreationDto quizDetails, Tournament tournament) {
        Student student = studentRepository.findById(userId).orElseThrow(() -> new TutorException(USER_NOT_FOUND, userId));

        Quiz quiz = new Quiz();
        quiz.setType(Quiz.QuizType.GENERATED.toString());
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

        availableQuestions = student.filterQuestionsByStudentModel(quizDetails.getNumberOfQuestions(), availableQuestions);

        quiz.generateQuiz(availableQuestions);

        QuizAnswer quizAnswer = new QuizAnswer(student, quiz);

        quiz.setCourseExecution(courseExecution);

        if (DateHandler.now().isBefore(tournament.getStartTime())) {
            quiz.setAvailableDate(tournament.getStartTime());
        }
        quiz.setConclusionDate(tournament.getEndTime());
        quiz.setResultsDate(tournament.getEndTime());
        quiz.setTitle("Tournament " + tournament.getId() + " Quiz");
        quiz.setType(Quiz.QuizType.TOURNAMENT.toString());

        quizRepository.save(quiz);

        return new StatementQuizDto(quizAnswer, false);
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 2000))
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public StatementQuizDto getQuizByQRCode(int userId, int quizId) {
        Student student = studentRepository.findById(userId).orElseThrow(() -> new TutorException(USER_NOT_FOUND, userId));
        Quiz quiz = quizRepository.findById(quizId).orElseThrow(() -> new TutorException(QUIZ_NOT_FOUND, quizId));

        commonChecks(student, quiz);

        if (!quiz.isQrCodeOnly()) {
            throw new TutorException(NOT_QRCODE_QUIZ);
        }

        QuizAnswer quizAnswer = getQuizAnswer(student, quiz);

        if (quiz.getAvailableDate() == null || DateHandler.now().isAfter(quiz.getAvailableDate())) {
            return getPreparedStatementQuizDto(student, quiz, quizAnswer);
        } else {  // Send timer
            StatementQuizDto quizDto = new StatementQuizDto();
            quizDto.setTimeToAvailability(ChronoUnit.MILLIS.between(DateHandler.now(), quiz.getAvailableDate()));
            return quizDto;
        }
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 2000))
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public StatementQuizDto startQuiz(int userId, int quizId) {
        Student student = studentRepository.findById(userId).orElseThrow(() -> new TutorException(USER_NOT_FOUND, userId));
        Quiz quiz = quizRepository.findById(quizId).orElseThrow(() -> new TutorException(QUIZ_NOT_FOUND, quizId));

        commonChecks(student, quiz);

        if (quiz.isQrCodeOnly()) {
            throw new TutorException(CANNOT_START_QRCODE_QUIZ);
        }

        if (quiz.getAvailableDate() != null && quiz.getAvailableDate().isAfter(DateHandler.now())) {
            throw new TutorException(QUIZ_NOT_YET_AVAILABLE);
        }

        QuizAnswer quizAnswer = getQuizAnswer(student, quiz);

        return getPreparedStatementQuizDto(student, quiz, quizAnswer);
    }

    private void commonChecks(Student student, Quiz quiz) {
        if (!student.getCourseExecutions().contains(quiz.getCourseExecution())) {
            throw new TutorException(USER_NOT_ENROLLED, student.getUsername());
        }

        if (quiz.getConclusionDate() != null && DateHandler.now().isAfter(quiz.getConclusionDate())) {
            throw new TutorException(QUIZ_NO_LONGER_AVAILABLE);
        }
    }

    private QuizAnswer getQuizAnswer(Student student, Quiz quiz) {
        QuizAnswer quizAnswer = quizAnswerRepository.findQuizAnswer(quiz.getId(), student.getId()).orElseGet(() -> {
            QuizAnswer qa = new QuizAnswer(student, quiz);
            quizAnswerRepository.save(qa);
            return qa;
        });

        if (!quizAnswer.openToAnswer()) {
            throw new TutorException(QUIZ_ALREADY_COMPLETED);
        }
        return quizAnswer;
    }

    private StatementQuizDto getPreparedStatementQuizDto(Student student, Quiz quiz, QuizAnswer quizAnswer) {
        if (quiz.getType().equals(Quiz.QuizType.IN_CLASS) && quizAnswer.getCreationDate() != null) {
            List<QuestionAnswerItem> items = questionAnswerItemRepository
                    .findQuestionAnswerItemsByUsernameAndQuizId(student.getUsername(), quiz.getId()).stream()
                    .sorted(Comparator.comparing(QuestionAnswerItem::getQuizQuestionId).thenComparing(QuestionAnswerItem::getAnswerDate))
                    .collect(Collectors.toList());

            if (items.isEmpty()) return new StatementQuizDto(quizAnswer, false);
            else return new StatementQuizDto(quizAnswer, items);
        } else {
            if (quizAnswer.getCreationDate() == null) {
                quizAnswer.setCreationDate(DateHandler.now());
            }
            return new StatementQuizDto(quizAnswer, false);
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
                .map(quiz -> new QuizDto(quiz, false))
                .sorted(Comparator.comparing(QuizDto::getAvailableDate, Comparator.nullsLast(Comparator.naturalOrder())))
                .collect(Collectors.toList());
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 2000))
    @Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true)
    public List<SolvedQuizDto> getSolvedQuizzes(int userId, int executionId) {
        Student student = studentRepository.findStudentWithQuizAnswersAndQuestionAnswersById(userId).orElseThrow(() -> new TutorException(USER_NOT_FOUND, userId));

        return student.getQuizAnswers().stream()
                .filter(quizAnswer -> quizAnswer.canResultsBePublic(executionId))
                .filter(quizAnswer -> quizAnswer.getAnswerDate() != null)
                .map(SolvedQuizDto::new)
                .sorted(Comparator.comparing(SolvedQuizDto::getAnswerDate))
                .collect(Collectors.toList());
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public StatementQuestionDto getQuestionForQuizAnswer(String username, Integer quizId, Integer questionId, StatementAnswerDto answer) {
        submitAnswer(username,quizId, answer);

        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new TutorException(QUESTION_NOT_FOUND, questionId));

        return new StatementQuestionDto(question);
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 2000))
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    public void submitAnswer(String username, int quizId, StatementAnswerDto answer) {
        if (answer.getTimeToSubmission() == null) {
            answer.setTimeToSubmission(0);
        }

        questionAnswerItemRepository.save(answer.getQuestionAnswerItem(username, quizId));
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
