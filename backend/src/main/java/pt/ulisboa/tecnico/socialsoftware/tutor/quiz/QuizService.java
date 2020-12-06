package pt.ulisboa.tecnico.socialsoftware.tutor.quiz;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.AnswerService;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.QuizAnswerDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.QuizAnswersDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.repository.QuizAnswerRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.config.DateHandler;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseService;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.domain.CourseExecution;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.repository.CourseExecutionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.repository.CourseRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.CSVQuizExportVisitor;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.LatexQuizExportVisitor;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.QuizzesXmlExport;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.QuizzesXmlImport;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.QuestionService;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsubmission.domain.QuestionSubmission;
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsubmission.repository.QuestionSubmissionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.dto.QuizDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.dto.QuizQuestionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository.QuizQuestionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository.QuizRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.statement.domain.QuestionAnswerItem;
import pt.ulisboa.tecnico.socialsoftware.tutor.statement.QuestionAnswerItemRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.User;
import java.io.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*;



@Service
public class QuizService {
    @SuppressWarnings("unused")
    private static final Logger logger = LoggerFactory.getLogger(QuizService.class);
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
    private AnswerService answerService;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private CourseService courseService;


    @Retryable(
            value = {SQLException.class},
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public QuizDto findById(Integer quizId) {
        return this.quizRepository.findById(quizId).map(quiz -> new QuizDto(quiz, true))
                .orElseThrow(() -> new TutorException(QUIZ_NOT_FOUND, quizId));
    }

    @Retryable(
            value = {SQLException.class},
            backoff = @Backoff(delay = 5000))

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public List<QuizDto> findNonGeneratedQuizzes(int executionId) {
        Comparator<Quiz> comparator = Comparator.comparing(Quiz::getAvailableDate, Comparator.nullsFirst(Comparator.reverseOrder()))
                .thenComparing(Quiz::getSeries, Comparator.nullsFirst(Comparator.reverseOrder()))
                .thenComparing(Quiz::getVersion, Comparator.nullsFirst(Comparator.reverseOrder()));

        return quizRepository.findQuizzesOfExecution(executionId).stream()
                .filter(quiz -> !quiz.getType().equals(Quiz.QuizType.GENERATED))
                .sorted(comparator)
                .map(quiz -> new QuizDto(quiz, false))
                .collect(Collectors.toList());
    }

    public Integer getMaxQuizKey() {
        Integer maxQuizKey = quizRepository.getMaxQuizKey();
        return maxQuizKey != null ? maxQuizKey : 0;
    }

    @Retryable(
            value = {SQLException.class},
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public QuizDto createQuiz(int executionId, QuizDto quizDto) {
        CourseExecution courseExecution = courseExecutionRepository.findById(executionId).orElseThrow(() -> new TutorException(COURSE_EXECUTION_NOT_FOUND, executionId));
        Quiz quiz = new Quiz(quizDto);

        if (quizDto.getKey() == null) {
            quizDto.setKey(getMaxQuizKey() + 1);
        }

        if (quizDto.getCreationDate() == null) {
            quiz.setCreationDate(DateHandler.now());
        }
        quiz.setCourseExecution(courseExecution);

        if (quizDto.getQuestions() != null) {
            for (QuestionDto questionDto : quizDto.getQuestions()) {
                Question question = questionRepository.findById(questionDto.getId())
                        .orElseThrow(() -> new TutorException(QUESTION_NOT_FOUND, questionDto.getId()));
                new QuizQuestion(quiz, question, quiz.getQuizQuestions().size());
            }
        }

        quizRepository.save(quiz);

        return new QuizDto(quiz, true);
    }


    @Retryable(
            value = {SQLException.class},
            backoff = @Backoff(delay = 5000))
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
        quiz.setOneWay(quizDto.isOneWay());

        if (quizDto.isTimed())
            quiz.setType(Quiz.QuizType.IN_CLASS.toString());
        else
            quiz.setType(Quiz.QuizType.PROPOSED.toString());

        Set<QuizQuestion> quizQuestions = new HashSet<>(quiz.getQuizQuestions());

        quizQuestions.forEach(QuizQuestion::remove);
        quizQuestions.forEach(quizQuestion -> quizQuestionRepository.delete(quizQuestion));

        if (quizDto.getQuestions() != null) {
            for (QuestionDto questionDto : quizDto.getQuestions()) {
                Question question = questionRepository.findById(questionDto.getId())
                        .orElseThrow(() -> new TutorException(QUESTION_NOT_FOUND, questionDto.getId()));
                QuizQuestion quizQuestion = new QuizQuestion(quiz, question, quiz.getQuizQuestions().size());
                quizQuestionRepository.save(quizQuestion);
            }
        }

        return new QuizDto(quiz, true);
    }


    @Retryable(
            value = {SQLException.class},
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public QuizQuestionDto addQuestionToQuiz(int questionId, int quizId) {
        Quiz quiz = quizRepository.findById(quizId).orElseThrow(() -> new TutorException(QUIZ_NOT_FOUND, quizId));
        Question question = questionRepository.findById(questionId).orElseThrow(() -> new TutorException(QUESTION_NOT_FOUND, questionId));

        QuizQuestion quizQuestion = new QuizQuestion(quiz, question, quiz.getQuizQuestions().size());

        quizQuestionRepository.save(quizQuestion);

        return new QuizQuestionDto(quizQuestion);
    }


    @Retryable(
            value = {SQLException.class},
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void removeQuiz(Integer quizId) {
        Quiz quiz = quizRepository.findById(quizId).orElseThrow(() -> new TutorException(QUIZ_NOT_FOUND, quizId));

        quiz.remove();

        Set<QuizQuestion> quizQuestions = new HashSet<>(quiz.getQuizQuestions());

        quizQuestions.forEach(QuizQuestion::remove);
        quizQuestions.forEach(quizQuestion -> quizQuestionRepository.delete(quizQuestion));

        quizRepository.delete(quiz);
    }

    @Retryable(
            value = {SQLException.class},
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public QuizAnswersDto getQuizAnswers(Integer quizId) {
        Quiz quiz = quizRepository.findById(quizId).orElseThrow(() -> new TutorException(QUIZ_NOT_FOUND, quizId));
        QuizAnswersDto quizAnswersDto = new QuizAnswersDto();

        quizAnswersDto.setCorrectSequence(
                quiz.getQuizQuestions().stream()
                        .sorted(Comparator.comparing(QuizQuestion::getSequence))
                        .map(quizQuestion -> quizQuestion.getQuestion().getCorrectAnswerText()
                ).collect(Collectors.toList()));

        quizAnswersDto.setQuizAnswers(quiz.getQuizAnswers().stream().map(QuizAnswerDto::new).collect(Collectors.toList()));
        if (quiz.getConclusionDate() != null && quiz.getConclusionDate().isAfter(DateHandler.now())) {
            quizAnswersDto.setTimeToSubmission(ChronoUnit.MILLIS.between(DateHandler.now(), quiz.getConclusionDate()));
        }

        return quizAnswersDto;
    }


    @Retryable(
            value = {SQLException.class},
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public String exportQuizzesToXml() {
        QuizzesXmlExport xmlExport = new QuizzesXmlExport();

        return xmlExport.export(quizRepository.findAll());
    }

    @Retryable(
            value = {SQLException.class},
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void importQuizzesFromXml(String quizzesXml) {
        QuizzesXmlImport xmlImport = new QuizzesXmlImport();

        xmlImport.importQuizzes(quizzesXml, this, questionRepository, quizQuestionRepository, courseRepository);
    }

    @Retryable(
            value = {SQLException.class},
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public String exportQuizzesToLatex(int quizId) {
        Quiz quiz = quizRepository.findById(quizId).orElseThrow(() -> new TutorException(QUIZ_NOT_FOUND, quizId));

        LatexQuizExportVisitor latexExport = new LatexQuizExportVisitor();

        return latexExport.export(quiz);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public ByteArrayOutputStream exportQuiz(int quizId) {
        Quiz quiz = quizRepository.findById(quizId).orElseThrow(() -> new TutorException(QUIZ_NOT_FOUND, quizId));

        List<QuestionAnswerItem> questionAnswerItems = questionAnswerItemRepository.findQuestionAnswerItemsByQuizId(quizId);

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ZipOutputStream zos = new ZipOutputStream(baos)) {

            List<Quiz> quizzes = new ArrayList<>();
            quizzes.add(quiz);

            QuizzesXmlExport xmlExport = new QuizzesXmlExport();
            InputStream in = IOUtils.toInputStream(xmlExport.export(quizzes), StandardCharsets.UTF_8);
            zos.putNextEntry(new ZipEntry(quizId + ".xml"));
            copyToZipStream(zos, in);
            zos.closeEntry();

            LatexQuizExportVisitor latexExport = new LatexQuizExportVisitor();
            zos.putNextEntry(new ZipEntry(quizId + ".tex"));
            in = IOUtils.toInputStream(latexExport.export(quiz), StandardCharsets.UTF_8);
            copyToZipStream(zos, in);
            zos.closeEntry();

            CSVQuizExportVisitor csvExport = new CSVQuizExportVisitor();
            zos.putNextEntry(new ZipEntry(quizId + ".csv"));
            in = IOUtils.toInputStream(csvExport.export(quiz, questionAnswerItems), StandardCharsets.UTF_8);
            copyToZipStream(zos, in);
            zos.closeEntry();

            baos.flush();

            return baos;
        } catch (IOException ex) {
            throw new TutorException(ErrorMessage.CANNOT_OPEN_FILE);
        }
    }

    private void copyToZipStream(ZipOutputStream zos, InputStream in) throws IOException {
        byte[] buffer = new byte[1024];
        int len;
        while ((len = in.read(buffer)) > 0) {
            zos.write(buffer, 0, len);
        }
        in.close();
    }

    @Retryable(
            value = {SQLException.class},
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void createQuizXmlDirectory(int quizId, String path) throws IOException {
        Quiz quiz = quizRepository.findById(quizId).orElseThrow(() -> new TutorException(QUIZ_NOT_FOUND, quizId));
        String directoryPath = path + "/" + quiz.getId();
        File file = new File(directoryPath);
        file.mkdir();

        QuizzesXmlExport xmlExport = new QuizzesXmlExport();
        List<Quiz> quizzes = new ArrayList<>();
        quizzes.add(quiz);
        File myObj = new File(directoryPath + "/" + quiz.getId() + ".xml");
        if (myObj.createNewFile()) {
            FileWriter myWriter = new FileWriter(myObj);
            myWriter.write(xmlExport.export(quizzes));
            myWriter.close();
        }

        LatexQuizExportVisitor latexExport = new LatexQuizExportVisitor();
        myObj = new File(directoryPath + "/" + quiz.getId() + ".tex");
        if (myObj.createNewFile()) {
            FileWriter myWriter = new FileWriter(myObj);
            myWriter.write(latexExport.export(quiz));
            myWriter.close();
        }

        List<QuestionAnswerItem> questionAnswerItems = questionAnswerItemRepository.findQuestionAnswerItemsByQuizId(quizId);
        CSVQuizExportVisitor csvExport = new CSVQuizExportVisitor();
        myObj = new File(directoryPath + "/" + quiz.getId() + ".csv");
        if (myObj.createNewFile()) {
            FileWriter myWriter = new FileWriter(myObj);
            myWriter.write(csvExport.export(quiz, questionAnswerItems));
            myWriter.close();
        }
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void resetDemoQuizzes() {
        quizRepository.findQuizzesOfExecution(courseService.getDemoCourse().getCourseExecutionId())
                .stream()
                .skip(2)
                .forEach(quiz -> {

                    for (QuizAnswer quizAnswer : new ArrayList<>(quiz.getQuizAnswers())) {
                        answerService.deleteQuizAnswer(quizAnswer);
                    }

                    for (QuizQuestion quizQuestion : quiz.getQuizQuestions()
                            .stream()
                            .filter(quizQuestion -> quizQuestion.getQuestionAnswers().isEmpty())
                            .collect(Collectors.toList())) {
                        questionService.deleteQuizQuestion(quizQuestion);
                    }

                    quiz.remove();
                    this.quizRepository.delete(quiz);
                });

        // remove questions that weren't in any quiz and are not submitted
        for (Question question : questionRepository.findQuestions(courseService.getDemoCourse().getCourseId())
                .stream()
                .filter(question -> question.getQuizQuestions().isEmpty() && questionSubmissionRepository.findQuestionSubmissionByQuestionId(question.getId()) == null)
                .collect(Collectors.toList())) {
            questionService.removeQuestion(question.getId());
        }
    }

    @Retryable(
            value = {SQLException.class},
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public QuizDto populateWithQuizAnswers(Integer quizId) {
        Quiz quiz = quizRepository.findById(quizId).orElseThrow(() -> new TutorException(QUIZ_NOT_FOUND, quizId));

        for (User student : quiz.getCourseExecution().getStudents()) {
            if (student.getQuizAnswer(quiz) == null) {
                answerService.createQuizAnswer(student.getId(), quizId);
            }
        }

        return new QuizDto(quiz, false);
    }

    @Retryable(
            value = {SQLException.class},
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public QuizDto removeNonFilledQuizAnswers(Integer quizId) {
        Quiz quiz = quizRepository.findById(quizId).orElseThrow(() -> new TutorException(QUIZ_NOT_FOUND, quizId));

        for (QuizAnswer quizAnswer : quizAnswerRepository.findNotAnsweredQuizAnswers(quizId)) {
            answerService.deleteQuizAnswer(quizAnswer);
        }

        return new QuizDto(quiz, false);
    }
}
