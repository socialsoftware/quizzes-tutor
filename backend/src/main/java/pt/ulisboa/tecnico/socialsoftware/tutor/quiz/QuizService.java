package pt.ulisboa.tecnico.socialsoftware.tutor.quiz;

import org.apache.commons.io.IOUtils;
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
import pt.ulisboa.tecnico.socialsoftware.tutor.config.DateHandler;
import pt.ulisboa.tecnico.socialsoftware.tutor.config.Demo;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecutionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.CSVQuizExportVisitor;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.LatexQuizExportVisitor;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.QuizzesXmlExport;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.QuizzesXmlImport;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.QuestionService;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Option;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.dto.QuizDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.dto.QuizQuestionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository.QuizQuestionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository.QuizRepository;

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
    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private CourseExecutionRepository courseExecutionRepository;

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private QuizQuestionRepository quizQuestionRepository;

    @Autowired
    private AnswerService answerService;

    @Autowired
    private QuestionService questionService;

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public CourseDto findQuizCourseExecution(int quizId) {
        return this.quizRepository.findById(quizId)
                .map(Quiz::getCourseExecution)
                .map(CourseDto::new)
                .orElseThrow(() -> new TutorException(QUIZ_NOT_FOUND, quizId));
    }

    @Retryable(
      value = { SQLException.class },
      backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public QuizDto findById(Integer quizId) {
        return this.quizRepository.findById(quizId).map(quiz -> new QuizDto(quiz, true))
                .orElseThrow(() -> new TutorException(QUIZ_NOT_FOUND, quizId));
    }

    @Retryable(
      value = { SQLException.class },
      backoff = @Backoff(delay = 5000))

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public List<QuizDto> findNonGeneratedQuizzes(int executionId) {
        Comparator<Quiz> comparator = Comparator.comparing(Quiz::getAvailableDate, Comparator.nullsFirst(Comparator.reverseOrder()))
                .thenComparing(Quiz::getSeries, Comparator.nullsFirst(Comparator.reverseOrder()))
                .thenComparing(Quiz::getVersion, Comparator.nullsFirst(Comparator.reverseOrder()));

        return quizRepository.findQuizzes(executionId).stream()
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
      value = { SQLException.class },
      backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
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
      value = { SQLException.class },
      backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public QuizDto updateQuiz(Integer quizId, QuizDto quizDto) {
        Quiz quiz = quizRepository.findById(quizId).orElseThrow(() ->new TutorException(QUIZ_NOT_FOUND, quizId));

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

        if (quizDto.getType() != null)
            quiz.setType(quizDto.getType());
        else if (quizDto.isTimed())
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
      value = { SQLException.class },
      backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public QuizQuestionDto addQuestionToQuiz(int questionId, int quizId) {
        Quiz quiz = quizRepository.findById(quizId).orElseThrow(() -> new TutorException(QUIZ_NOT_FOUND, quizId));
        Question question = questionRepository.findById(questionId).orElseThrow(() ->new TutorException(QUESTION_NOT_FOUND, questionId));

        QuizQuestion quizQuestion = new QuizQuestion(quiz, question, quiz.getQuizQuestions().size());

        quizQuestionRepository.save(quizQuestion);

        return new QuizQuestionDto(quizQuestion);
    }


    @Retryable(
      value = { SQLException.class },
      backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void removeQuiz(Integer quizId) {
        Quiz quiz = quizRepository.findById(quizId).orElseThrow(() -> new TutorException(QUIZ_NOT_FOUND, quizId));

        quiz.remove();

        Set<QuizQuestion> quizQuestions = new HashSet<>(quiz.getQuizQuestions());

        quizQuestions.forEach(QuizQuestion::remove);
        quizQuestions.forEach(quizQuestion -> quizQuestionRepository.delete(quizQuestion));

        quizRepository.delete(quiz);
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public QuizAnswersDto getQuizAnswers(Integer quizId) {
        Quiz quiz = quizRepository.findById(quizId).orElseThrow(() -> new TutorException(QUIZ_NOT_FOUND, quizId));
        QuizAnswersDto quizAnswersDto = new QuizAnswersDto();

        quizAnswersDto.setCorrectSequence(
                quiz.getQuizQuestions().stream().sorted(Comparator.comparing(QuizQuestion::getSequence)).map(quizQuestion ->
                quizQuestion.getQuestion()
                        .getOptions()
                        .stream()
                        .filter(Option::getCorrect)
                        .findFirst().orElseThrow(() -> new TutorException(NO_CORRECT_OPTION))
                        .getSequence()
        ).collect(Collectors.toList()));

        quizAnswersDto.setQuizAnswers(quiz.getQuizAnswers().stream().map(QuizAnswerDto::new).collect(Collectors.toList()));
        if (quiz.getConclusionDate() != null && quiz.getConclusionDate().isAfter(DateHandler.now())) {
            quizAnswersDto.setTimeToSubmission(ChronoUnit.MILLIS.between(DateHandler.now(), quiz.getConclusionDate()));
        }

        return quizAnswersDto;
    }


    @Retryable(
      value = { SQLException.class },
      backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public String exportQuizzesToXml() {
        QuizzesXmlExport xmlExport = new QuizzesXmlExport();

        return xmlExport.export(quizRepository.findAll());
    }

    @Retryable(
      value = { SQLException.class },
      backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void importQuizzesFromXml(String quizzesXml) {
        QuizzesXmlImport xmlImport = new QuizzesXmlImport();

        xmlImport.importQuizzes(quizzesXml, this, questionRepository, quizQuestionRepository, courseExecutionRepository, courseRepository);
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public String exportQuizzesToLatex(int quizId) {
        Quiz quiz = quizRepository.findById(quizId).orElseThrow(() -> new TutorException(QUIZ_NOT_FOUND, quizId));

        LatexQuizExportVisitor latexExport = new LatexQuizExportVisitor();

        return latexExport.export(quiz);
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public ByteArrayOutputStream exportQuiz(int quizId) {
        Quiz quiz = quizRepository.findById(quizId).orElseThrow(() -> new TutorException(QUIZ_NOT_FOUND, quizId));

        String name = quiz.getTitle();
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ZipOutputStream zos = new ZipOutputStream(baos)) {

            List<Quiz> quizzes = new ArrayList<>();
            quizzes.add(quiz);

            QuizzesXmlExport xmlExport = new QuizzesXmlExport();
            InputStream in = IOUtils.toInputStream(xmlExport.export(quizzes), StandardCharsets.UTF_8);
            zos.putNextEntry(new ZipEntry(name + ".xml"));
            copyToZipStream(zos, in);
            zos.closeEntry();

            LatexQuizExportVisitor latexExport = new LatexQuizExportVisitor();
            zos.putNextEntry(new ZipEntry(name + ".tex"));
            in = IOUtils.toInputStream(latexExport.export(quiz), StandardCharsets.UTF_8);
            copyToZipStream(zos, in);
            zos.closeEntry();

            CSVQuizExportVisitor csvExport = new CSVQuizExportVisitor();
            zos.putNextEntry(new ZipEntry(name + ".csv"));
            in = IOUtils.toInputStream( csvExport.export(quiz), StandardCharsets.UTF_8);
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
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void resetDemoQuizzes() {
        quizRepository.findQuizzes(Demo.COURSE_EXECUTION_ID).stream().filter(quiz -> quiz.getId() > 5360).forEach(quiz -> {
            for (QuizAnswer quizAnswer : new ArrayList<>(quiz.getQuizAnswers())) {
                answerService.deleteQuizAnswer(quizAnswer);
            }

            for (QuizQuestion quizQuestion : quiz.getQuizQuestions().stream().filter(quizQuestion -> quizQuestion.getQuestionAnswers().isEmpty()).collect(Collectors.toList())) {
                questionService.deleteQuizQuestion(quizQuestion);
            }

            quiz.remove();
            this.quizRepository.delete(quiz);
        });

        // remove questions that weren't in any quiz
        for (Question question: questionRepository.findQuestions(Demo.COURSE_ID).stream().filter(question -> question.getQuizQuestions().isEmpty()).collect(Collectors.toList())) {
            questionService.deleteQuestion(question);
        }
    }
}
