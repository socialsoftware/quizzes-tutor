package pt.ulisboa.tecnico.socialsoftware.tutor.impexp;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.repository.QuizAnswerRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.AnswerService;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.*;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.TopicRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.QuestionService;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.QuizService;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository.QuizRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserService;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public class ImpExpService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private TopicRepository topicRepository;

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private QuizAnswerRepository quizAnswerRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private QuizService quizService;

    @Autowired
    private AnswerService answerService;

    @Value("${load.dir}")
    private String loadDir;

    @Value("${export.dir}")
    private String exportDir;

    @Transactional
    public String exportAll() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
        File directory = new File(exportDir);

        String filename = "tutor-" + timeStamp + ".zip";
        FileOutputStream fos = new FileOutputStream(directory.getPath() + "/" + filename);
        ZipOutputStream zos = new ZipOutputStream(fos);

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

        zos.close();

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

    private InputStream generateUsersInputStream() throws IOException {
        UsersXmlExport usersExporter = new UsersXmlExport();
        return IOUtils.toInputStream(usersExporter.export(userRepository.findAll()), "UTF-8");
    }

    private InputStream generateQuestionsInputStream() throws IOException {
        QuestionsXmlExport generator = new QuestionsXmlExport();
        return IOUtils.toInputStream(generator.export(questionRepository.findAll()), "UTF-8");
    }

    private InputStream generateTopicsInputStream() throws IOException {
        TopicsXmlExport generator = new TopicsXmlExport();
        return IOUtils.toInputStream(generator.export(topicRepository.findAll()), "UTF-8");
    }

    private InputStream generateQuizzesInputStream() throws IOException {
        QuizzesXmlExport generator = new QuizzesXmlExport();
        return IOUtils.toInputStream(generator.export(quizRepository.findAll()), "UTF-8");
    }

    private InputStream generateAnswersInputStream() throws IOException {
        AnswersXmlExport generator = new AnswersXmlExport();
        return IOUtils.toInputStream(generator.export(quizAnswerRepository.findAll()), "UTF-8");
    }

    @Transactional
    public void importAll() throws IOException {
        if (userRepository.findAll().isEmpty()) {
            try {
                File directory = new File(loadDir);

                File usersFile = new File(directory.getPath() + "/" + "users.xml");
                UsersXmlImport usersXmlImport = new UsersXmlImport();
                usersXmlImport.importUsers(new FileInputStream(usersFile), userService);

                File questionsFile = new File(directory.getPath() + "/" + "questions.xml");
                QuestionsXmlImport questionsXmlImport = new QuestionsXmlImport();
                questionsXmlImport.importQuestions(new FileInputStream(questionsFile), questionService);

                File topicsFile = new File(directory.getPath() + "/" + "topics.xml");
                TopicsXmlImport topicsXmlImport = new TopicsXmlImport();
                topicsXmlImport.importTopics(new FileInputStream(topicsFile), questionService);

                File quizzesFile = new File(directory.getPath() + "/" + "quizzes.xml");
                QuizzesXmlImport quizzesXmlImport = new QuizzesXmlImport();
                quizzesXmlImport.importQuizzes(new FileInputStream(quizzesFile), quizService, questionRepository);

                File answersFile = new File(directory.getPath() + "/" + "answers.xml");
                AnswersXmlImport answersXmlImport = new AnswersXmlImport();
                answersXmlImport.importAnswers(new FileInputStream(answersFile), answerService, questionRepository, quizRepository, quizAnswerRepository, userRepository);
            } catch (FileNotFoundException e) {

            }
        }
    }

}
