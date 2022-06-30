package pt.ulisboa.tecnico.socialsoftware.tutor.quiz;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.common.dtos.question.QuestionDto;
import pt.ulisboa.tecnico.socialsoftware.common.dtos.quiz.QuizDto;
import pt.ulisboa.tecnico.socialsoftware.common.dtos.quiz.QuizType;
import pt.ulisboa.tecnico.socialsoftware.common.dtos.tournament.TournamentDto;
import pt.ulisboa.tecnico.socialsoftware.common.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.common.utils.DateHandler;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.AnswerService;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswerItem;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.QuizAnswerDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.QuizAnswersDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.repository.QuestionAnswerItemRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.repository.QuizAnswerRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.CourseExecutionService;
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.CourseExecution;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.*;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.QuestionService;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.CourseRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsubmission.repository.QuestionSubmissionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.dto.QuizQuestionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository.CourseExecutionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository.QuizQuestionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository.QuizRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.User;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.repository.UserRepository;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static pt.ulisboa.tecnico.socialsoftware.common.exceptions.ErrorMessage.*;

@Service
public class QuizService {
    public static final String LATEX_MACROS_DIR = System.getProperty("user.dir") + "/src/main/resources/latex/";

    @Value("${figures.dir}")
    private String figuresDir;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private CourseExecutionRepository courseExecutionRepository;

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private QuestionAnswerItemRepository questionAnswerItemRepository;

    @Autowired
    private QuizAnswerRepository quizAnswerRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private QuestionSubmissionRepository questionSubmissionRepository;

    @Autowired
    private QuizQuestionRepository quizQuestionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AnswerService answerService;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private CourseExecutionService courseExecutionService;

    @Retryable(value = { SQLException.class }, backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public QuizDto findById(Integer quizId) {
        return this.quizRepository.findById(quizId).map(quiz -> quiz.getDto(true))
                .orElseThrow(() -> new TutorException(QUIZ_NOT_FOUND, quizId));
    }

    @Retryable(value = { SQLException.class }, backoff = @Backoff(delay = 5000))

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public List<QuizDto> findNonGeneratedQuizzes(int executionId) {
        Comparator<Quiz> comparator = Comparator
                .comparing(Quiz::getAvailableDate, Comparator.nullsFirst(Comparator.reverseOrder()))
                .thenComparing(Quiz::getSeries, Comparator.nullsFirst(Comparator.reverseOrder()))
                .thenComparing(Quiz::getVersion, Comparator.nullsFirst(Comparator.reverseOrder()));

        return quizRepository.findQuizzesOfExecution(executionId).stream()
                .filter(quiz -> !quiz.getType().equals(QuizType.GENERATED)
                        || quiz.getType().equals(QuizType.EXTERNAL_QUIZ))
                .sorted(comparator)
                .map(quiz -> quiz.getDto(false))
                .collect(Collectors.toList());
    }

    @Retryable(value = { SQLException.class }, backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public QuizDto createQuiz(int executionId, QuizDto quizDto) {
        CourseExecution courseExecution = courseExecutionRepository.findById(executionId)
                .orElseThrow(() -> new TutorException(COURSE_EXECUTION_NOT_FOUND, executionId));
        if (quizDto.isQrCodeOnly() && quizDto.getCode() == null) {
            quizDto.setCode(quizRepository.getMaxCode(executionId).orElse(0) + 1);
        }
        Quiz quiz = new Quiz(quizDto);

        if (quizDto.getCreationDate() == null) {
            quiz.setCreationDate(DateHandler.now());
        }
        quiz.setCourseExecution(courseExecution);

        if (quizDto.getQuestions() != null) {
            quizDto.getQuestions().stream().sorted(Comparator.comparing(QuestionDto::getSequence))
                    .forEach(questionDto -> {
                        Question question = questionRepository.findById(questionDto.getId())
                                .orElseThrow(() -> new TutorException(QUESTION_NOT_FOUND, questionDto.getId()));
                        new QuizQuestion(quiz, question, quiz.getQuizQuestionsNumber());
                    });
        }

        quizRepository.save(quiz);

        return quiz.getDto(true);
    }

    @Retryable(value = { SQLException.class }, backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public QuizDto updateQuiz(Integer quizId, QuizDto quizDto) {
        Quiz quiz = quizRepository.findById(quizId).orElseThrow(() -> new TutorException(QUIZ_NOT_FOUND, quizId));

        quiz.checkCanChange();

        quiz.setTitle(quizDto.getTitle());
        if (DateHandler.isValidDateFormat(quizDto.getAvailableDate()))
            quiz.setAvailableDate(DateHandler.toLocalDateTime(quizDto.getAvailableDate()));
        if (DateHandler.isValidDateFormat(quizDto.getConclusionDate()))
            quiz.setConclusionDate(DateHandler.toLocalDateTime(quizDto.getConclusionDate()));
        if (DateHandler.isValidDateFormat(quizDto.getResultsDate()))
            quiz.setResultsDate(DateHandler.toLocalDateTime(quizDto.getResultsDate()));
        quiz.setScramble(quizDto.isScramble());
        quiz.setQrCodeOnly(quizDto.isQrCodeOnly());
        if (quizDto.isQrCodeOnly() && quiz.getCode() == null) {
            quiz.setCode(quizRepository.getMaxCode(quiz.getCourseExecution().getId()).orElse(0) + 1);
        }
        quiz.setOneWay(quizDto.isOneWay());

        if (quizDto.isTimed())
            quiz.setType(QuizType.IN_CLASS.toString());
        else
            quiz.setType(QuizType.PROPOSED.toString());

        Set<QuizQuestion> quizQuestions = new HashSet<>(quiz.getQuizQuestions());

        quizQuestions.forEach(QuizQuestion::remove);
        quizQuestions.forEach(quizQuestion -> quizQuestionRepository.delete(quizQuestion));

        if (quizDto.getQuestions() != null) {
            quizDto.getQuestions().stream().sorted(Comparator.comparing(QuestionDto::getSequence))
                    .forEach(questionDto -> {
                        Question question = questionRepository.findById(questionDto.getId()).get();
                        QuizQuestion quizQuestion = new QuizQuestion(quiz, question, quiz.getQuizQuestionsNumber());
                        quizQuestionRepository.save(quizQuestion);
                    });
        }

        return quiz.getDto(true);
    }

    @Retryable(value = { SQLException.class }, backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public QuizQuestionDto addQuestionToQuiz(int questionId, int quizId) {
        Quiz quiz = quizRepository.findById(quizId).orElseThrow(() -> new TutorException(QUIZ_NOT_FOUND, quizId));
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new TutorException(QUESTION_NOT_FOUND, questionId));

        QuizQuestion quizQuestion = new QuizQuestion(quiz, question, quiz.getQuizQuestionsNumber());

        quizQuestionRepository.save(quizQuestion);

        return new QuizQuestionDto(quizQuestion);
    }

    @Retryable(value = { SQLException.class }, backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void removeQuiz(Integer quizId) {
        Quiz quiz = quizRepository.findById(quizId).orElseThrow(() -> new TutorException(QUIZ_NOT_FOUND, quizId));

        if (quiz.getType().equals(QuizType.EXTERNAL_QUIZ)) {
            throw new TutorException(EXTERNAL_CANNOT_BE_REMOVED);
        }

        quiz.remove();

        Set<QuizQuestion> quizQuestions = new HashSet<>(quiz.getQuizQuestions());

        quizQuestions.forEach(QuizQuestion::remove);
        quizQuestions.forEach(quizQuestion -> quizQuestionRepository.delete(quizQuestion));

        quizRepository.delete(quiz);
    }

    @Retryable(value = { SQLException.class }, backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public QuizAnswersDto getQuizAnswers(Integer quizId) {
        Quiz quiz = quizRepository.findById(quizId).orElseThrow(() -> new TutorException(QUIZ_NOT_FOUND, quizId));
        QuizAnswersDto quizAnswersDto = new QuizAnswersDto();

        quizAnswersDto.setCorrectSequence(
                quiz.getQuizQuestions().stream()
                        .map(quizQuestion -> quizQuestion.getQuestion().getCorrectAnswerRepresentation())
                        .collect(Collectors.toList()));

        quizAnswersDto
                .setQuizAnswers(quiz.getQuizAnswers().stream().map(QuizAnswerDto::new).collect(Collectors.toList()));
        if (quiz.getConclusionDate() != null && quiz.getConclusionDate().isAfter(DateHandler.now())) {
            quizAnswersDto.setTimeToSubmission(ChronoUnit.MILLIS.between(DateHandler.now(), quiz.getConclusionDate()));
        }

        return quizAnswersDto;
    }

    @Retryable(value = { SQLException.class }, backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public String exportQuizzesToXml() {
        QuizzesXmlExport xmlExport = new QuizzesXmlExport();

        return xmlExport.export(quizRepository.findAll());
    }

    @Retryable(value = { SQLException.class }, backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void importQuizzesFromXml(String quizzesXml) {
        QuizzesXmlImport xmlImport = new QuizzesXmlImport();

        xmlImport.importQuizzes(quizzesXml, this, questionRepository, quizQuestionRepository, courseRepository);
    }

    @Retryable(value = { SQLException.class }, backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void createQuizXmlDirectory(int quizId, String path) throws IOException {
        Quiz quiz = quizRepository.findById(quizId).orElseThrow(() -> new TutorException(QUIZ_NOT_FOUND, quizId));
        String directoryPath = path + "/" + quiz.getId();
        File file = new File(directoryPath);
        file.mkdir();

        List<QuestionAnswerItem> questionAnswerItems = questionAnswerItemRepository
                .findQuestionAnswerItemsByQuizId(quizId);
        CSVQuizExportVisitor csvExport = new CSVQuizExportVisitor();
        File myObj = new File(directoryPath + "/" + quiz.getId() + ".csv");
        if (myObj.createNewFile()) {
            FileWriter myWriter = new FileWriter(myObj);
            myWriter.write(csvExport.export(quiz, questionAnswerItems));
            myWriter.close();
        }

        String latexDirectoryPath = directoryPath + "/latex/";
        File latexDirectory = new File(latexDirectoryPath);
        latexDirectory.mkdir();
        LatexQuizExportVisitor latexExport = new LatexQuizExportVisitor();
        myObj = new File(latexDirectoryPath + "quiz.tex");
        if (myObj.createNewFile()) {
            FileWriter myWriter = new FileWriter(myObj);
            myWriter.write(latexExport.exportQuiz(quiz));
            myWriter.close();
        }
        myObj = new File(latexDirectoryPath + "questions.tex");
        if (myObj.createNewFile()) {
            FileWriter myWriter = new FileWriter(myObj);
            myWriter.write(latexExport.exportQuestions(
                    quiz.getQuizQuestions().stream().map(QuizQuestion::getQuestion).collect(Collectors.toList())));
            myWriter.close();
        }
        // export images
        for (Question question : quiz.getQuizQuestions().stream().map(QuizQuestion::getQuestion)
                .collect(Collectors.toList())) {
            if (question.getImage() != null) {
                try {
                    File figureFile = new File(figuresDir + question.getImage().getUrl());
                    Files.copy(figureFile.toPath(),
                            (new File(latexDirectoryPath + figureFile.getName())).toPath(),
                            StandardCopyOption.REPLACE_EXISTING);
                } catch (NoSuchFileException noSuchFileException) {
                    // if image does not exist do nothing
                }
            }
        }
        // export latex macros
        String latexStyDirectoryPath = latexDirectoryPath + "/sty/";
        File latexStyDirectory = new File(latexStyDirectoryPath);
        latexStyDirectory.mkdir();
        String[] latexFileNames = { "docist.cls", "macros.tex", "exameIST.sty", "exame.tex", "LogoIST-novo.pdf" };
        for (String latexFileName : latexFileNames) {
            File latexFile = new File(LATEX_MACROS_DIR + latexFileName);
            Files.copy(latexFile.toPath(),
                    (new File(latexStyDirectoryPath + latexFile.getName())).toPath(),
                    StandardCopyOption.REPLACE_EXISTING);
        }

        QuizzesXmlExport xmlExport = new QuizzesXmlExport();
        List<Quiz> quizzes = new ArrayList<>();
        quizzes.add(quiz);
        myObj = new File(directoryPath + "/" + "quizzes-" + quiz.getId() + ".xml");
        if (myObj.createNewFile()) {
            FileWriter myWriter = new FileWriter(myObj);
            myWriter.write(xmlExport.export(quizzes));
            myWriter.close();
        }

        XMLQuestionExportVisitor questionsXmlExport = new XMLQuestionExportVisitor();
        myObj = new File(directoryPath + "/" + "questions-" + quiz.getId() + ".xml");
        if (myObj.createNewFile()) {
            FileWriter myWriter = new FileWriter(myObj);
            myWriter.write(questionsXmlExport.export(quiz.getQuizQuestions().stream()
                    .map(QuizQuestion::getQuestion)
                    .collect(Collectors.toList())));
            myWriter.close();
        }

        AnswersXmlExportVisitor answersXmlExport = new AnswersXmlExportVisitor();
        myObj = new File(directoryPath + "/" + "answers-" + quiz.getId() + ".xml");
        if (myObj.createNewFile()) {
            FileWriter myWriter = new FileWriter(myObj);
            myWriter.write(answersXmlExport.export(quiz.getQuizAnswers().stream()
                    .sorted(Comparator.comparing(quizAnswer -> quizAnswer.getUser().getId()))
                    .collect(Collectors.toList())));
            myWriter.close();
        }
    }

    @Retryable(value = { SQLException.class }, backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void resetDemoQuizzes() {
        quizRepository.findQuizzesOfExecution(courseExecutionService.getDemoCourse().getCourseExecutionId())
                .stream()
                .sorted(Comparator.comparing(Quiz::getId))
                .filter(q -> !q.isExternalQuiz())
                .forEach(quiz -> {
                    quiz.remove();
                    this.quizRepository.delete(quiz);
                });

        // remove questions that weren't in any quiz and are not submitted
        for (Question question : questionRepository.findQuestions(courseExecutionService.getDemoCourse().getCourseId())
                .stream()
                .filter(question -> question.getQuizQuestions().isEmpty()
                        && questionSubmissionRepository.findQuestionSubmissionByQuestionId(question.getId()) == null)
                .collect(Collectors.toList())) {
            questionService.removeQuestion(question.getId());
        }
    }

    @Retryable(value = { SQLException.class }, backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public QuizDto populateWithQuizAnswers(Integer quizId) {
        Quiz quiz = quizRepository.findById(quizId).orElseThrow(() -> new TutorException(QUIZ_NOT_FOUND, quizId));

        for (User student : quiz.getCourseExecution().getStudents()) {
            if (student.getQuizAnswer(quiz) == null) {
                answerService.createQuizAnswer(student.getId(), quizId);
            }
        }

        return quiz.getDto(false);
    }

    @Retryable(value = { SQLException.class }, backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public QuizDto removeNonFilledQuizAnswers(Integer quizId) {
        Quiz quiz = quizRepository.findById(quizId).orElseThrow(() -> new TutorException(QUIZ_NOT_FOUND, quizId));

        for (QuizAnswer quizAnswer : quizAnswerRepository.findNotAnsweredQuizAnswers(quizId)) {
            answerService.deleteQuizAnswer(quizAnswer);
        }

        return quiz.getDto(false);
    }

    @Retryable(value = { SQLException.class }, backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void populateExternalQuizzesWithQuizAnswers() {
        List<Quiz> quizzes = quizRepository.findAll();
        quizzes.stream().filter(Quiz::isExternalQuiz).forEach(
                quiz -> {
                    for (User student : quiz.getCourseExecution().getStudents()) {
                        if (student.getQuizAnswer(quiz) == null) {
                            answerService.createQuizAnswer(student.getId(), quiz.getId());
                        }
                    }
                });
    }

    @Transactional
    public void updateExternalQuiz(Integer userId, Integer executionId, Integer quizId,
            TournamentDto tournamentDto) {
        Quiz quiz = quizRepository.findById(quizId).orElseThrow(() -> new TutorException(QUIZ_NOT_FOUND, quizId));
        quiz.checkCanChange();

        User user = userRepository.findById(userId).orElseThrow(() -> new TutorException(USER_NOT_FOUND, userId));

        CourseExecution courseExecution = courseExecutionRepository.findById(executionId)
                .orElseThrow(() -> new TutorException(COURSE_EXECUTION_NOT_FOUND, executionId));
        List<Question> availableQuestions = questionRepository
                .findAvailableQuestions(courseExecution.getCourse().getId());

        if (tournamentDto.getTopicsDto() != null) {
            availableQuestions = courseExecution.filterQuestionsByTopics(availableQuestions,
                    tournamentDto.getTopicsDto());
        } else {
            availableQuestions = new ArrayList<>();
        }

        if (availableQuestions.size() < tournamentDto.getNumberOfQuestions()) {
            throw new TutorException(NOT_ENOUGH_QUESTIONS_TOURNAMENT);
        }

        availableQuestions = user.filterQuestionsByStudentModel(tournamentDto.getNumberOfQuestions(),
                availableQuestions);

        updateExternalQuizDetailsTransactional(tournamentDto, quiz, availableQuestions);
    }

    @Retryable(value = { SQLException.class }, backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void updateExternalQuizDetailsTransactional(TournamentDto tournamentDto, Quiz quiz,
            List<Question> availableQuestions) {
        if (DateHandler.isValidDateFormat(tournamentDto.getStartTime()))
            quiz.setAvailableDate(DateHandler.toLocalDateTime(tournamentDto.getStartTime()));
        if (DateHandler.isValidDateFormat(tournamentDto.getEndTime())) {
            quiz.setConclusionDate(DateHandler.toLocalDateTime(tournamentDto.getEndTime()));
            quiz.setResultsDate(DateHandler.toLocalDateTime(tournamentDto.getEndTime()));
        }

        Set<QuizQuestion> quizQuestions = new HashSet<>(quiz.getQuizQuestions());

        quizQuestions.forEach(QuizQuestion::remove);
        quizQuestions.forEach(quizQuestion -> quizQuestionRepository.delete(quizQuestion));

        IntStream.range(0, availableQuestions.size())
                .forEach(index -> new QuizQuestion(quiz, availableQuestions.get(index), index));
    }

    @Transactional
    public void removeExternalQuiz(Integer quizId) {
        Quiz quiz = quizRepository.findById(quizId).orElseThrow(() -> new TutorException(QUIZ_NOT_FOUND, quizId));

        deleteExternalQuizTransactional(quiz);
    }

    @Retryable(value = { SQLException.class }, backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void deleteExternalQuizTransactional(Quiz quiz) {
        quiz.remove();

        Set<QuizQuestion> quizQuestions = new HashSet<>(quiz.getQuizQuestions());

        quizQuestions.forEach(QuizQuestion::remove);
        quizQuestions.forEach(quizQuestion -> quizQuestionRepository.delete(quizQuestion));

        quizRepository.delete(quiz);
    }
}
