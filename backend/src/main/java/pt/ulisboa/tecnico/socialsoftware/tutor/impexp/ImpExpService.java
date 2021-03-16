package pt.ulisboa.tecnico.socialsoftware.tutor.impexp;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.AnswerService;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.repository.QuizAnswerRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository.CourseExecutionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.CourseRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.*;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.QuestionService;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.TopicService;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.TopicRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.QuizService;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository.QuizQuestionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository.QuizRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.repository.UserRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserService;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public class ImpExpService {
    public static final String PATH_DELIMITER = "/";

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private TopicRepository topicRepository;

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private QuizQuestionRepository quizQuestionRepository;

    @Autowired
    private QuizAnswerRepository quizAnswerRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private CourseExecutionRepository courseExecutionRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private TopicService topicService;

    @Autowired
    private QuizService quizService;

    @Autowired
    private AnswerService answerService;

    @Autowired
    private AnswersXmlImport answersXmlImport;

    @Value("${load.dir}")
    private String loadDir;

    @Value("${export.dir}")
    private String exportDir;


    @Retryable(
      value = { SQLException.class },
      backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public String exportAll() {
        String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
        File directory = new File(exportDir);

        String filename = "tutor-" + timeStamp + ".zip";
        try (FileOutputStream fos = new FileOutputStream(directory.getPath() + PATH_DELIMITER + filename);
             ZipOutputStream zos = new ZipOutputStream(fos)) {


            zos.putNextEntry(new ZipEntry("users.xml"));
            InputStream in = generateUsersInputStream();
            copyToZipStream(zos, in);
            zos.closeEntry();

            zos.putNextEntry(new ZipEntry("questions.xml"));
            in = generateQuestionsInputStream();
            copyToZipStream(zos, in);
            zos.closeEntry();

            zos.putNextEntry(new ZipEntry("topics.xml"));
            in = generateTopicsInputStream();
            copyToZipStream(zos, in);
            zos.closeEntry();

            zos.putNextEntry(new ZipEntry("quizzes.xml"));
            in = generateQuizzesInputStream();
            copyToZipStream(zos, in);
            zos.closeEntry();

            zos.putNextEntry(new ZipEntry("answers.xml"));
            in = generateAnswersInputStream();
            copyToZipStream(zos, in);
            zos.closeEntry();
        } catch (IOException ex) {
           throw new TutorException(ErrorMessage.CANNOT_OPEN_FILE);
        }

        return filename;
    }

    private void copyToZipStream(ZipOutputStream zos, InputStream in) throws IOException {
        byte[] buffer = new byte[1024];
        int len;
        while ((len = in.read(buffer)) > 0) {
            zos.write(buffer, 0, len);
        }
        in.close();
    }

    private InputStream generateUsersInputStream() {
        UsersXmlExport usersExporter = new UsersXmlExport();
        return IOUtils.toInputStream(usersExporter.export(userRepository.findAll()), StandardCharsets.UTF_8);
    }

    private InputStream generateQuestionsInputStream() {
        XMLQuestionExportVisitor generator = new XMLQuestionExportVisitor();
        return IOUtils.toInputStream(generator.export(questionRepository.findAll()), StandardCharsets.UTF_8);
    }

    private InputStream generateTopicsInputStream() {
        TopicsXmlExport generator = new TopicsXmlExport();
        return IOUtils.toInputStream(generator.export(topicRepository.findAll()), StandardCharsets.UTF_8);
    }

    private InputStream generateQuizzesInputStream() {
        QuizzesXmlExport generator = new QuizzesXmlExport();
        return IOUtils.toInputStream(generator.export(quizRepository.findAll()), StandardCharsets.UTF_8);
    }

    private InputStream generateAnswersInputStream() {
        AnswersXmlExportVisitor generator = new AnswersXmlExportVisitor();
        return IOUtils.toInputStream(generator.export(quizAnswerRepository.findAll()), StandardCharsets.UTF_8);
    }


    @Retryable(
      value = { SQLException.class },
      backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void importAll() {
        if (userRepository.findAll().isEmpty()) {
            try {
                File directory = new File(loadDir);

                File usersFile = new File(directory.getPath() + PATH_DELIMITER + "users.xml");
                UsersXmlImport usersXmlImport = new UsersXmlImport();
                usersXmlImport.importUsers(new FileInputStream(usersFile), userService);

                File questionsFile = new File(directory.getPath() + PATH_DELIMITER + "questions.xml");
                QuestionsXmlImport questionsXmlImport = new QuestionsXmlImport();
                questionsXmlImport.importQuestions(new FileInputStream(questionsFile), questionService, courseRepository, null);

                File topicsFile = new File(directory.getPath() + PATH_DELIMITER + "topics.xml");
                TopicsXmlImport topicsXmlImport = new TopicsXmlImport();
                topicsXmlImport.importTopics(new FileInputStream(topicsFile), topicService, questionService, courseRepository);

                File quizzesFile = new File(directory.getPath() + PATH_DELIMITER + "quizzes.xml");
                QuizzesXmlImport quizzesXmlImport = new QuizzesXmlImport();
                quizzesXmlImport.importQuizzes(new FileInputStream(quizzesFile), quizService, questionRepository, quizQuestionRepository, courseRepository);

                File answersFile = new File(directory.getPath() + PATH_DELIMITER + "answers.xml");

                answersXmlImport.importAnswers(new FileInputStream(answersFile));
            } catch (FileNotFoundException e) {
                throw new TutorException(ErrorMessage.CANNOT_OPEN_FILE);
            }
        }
    }
}
