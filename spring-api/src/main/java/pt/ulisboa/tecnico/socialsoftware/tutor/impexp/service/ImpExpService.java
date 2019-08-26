package pt.ulisboa.tecnico.socialsoftware.tutor.impexp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.repository.QuizAnswerRepository;

import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.*;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository;

import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.TopicRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository.QuizRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.repository.UserRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.utils.PropertiesManager;

import org.apache.commons.io.IOUtils;

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

    @Transactional
    public String exportAll() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());

        String exportDir = PropertiesManager.getProperties().getProperty("export.dir");
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
        InputStream in = IOUtils.toInputStream(usersExporter.export(userRepository.findAll()), "UTF-8");
        return in;
    }

    private InputStream generateQuestionsInputStream() throws IOException {
        QuestionsXmlExport generator = new QuestionsXmlExport();
        InputStream in = IOUtils.toInputStream(generator.export(questionRepository.findAll()), "UTF-8");
        return in;
    }

    private InputStream generateTopicsInputStream() throws IOException {
        TopicsXmlExport generator = new TopicsXmlExport();
        InputStream in = IOUtils.toInputStream(generator.export(topicRepository.findAll()), "UTF-8");
        return in;
    }

    private InputStream generateQuizzesInputStream() throws IOException {
        QuizzesXmlExport generator = new QuizzesXmlExport();
        InputStream in = IOUtils.toInputStream(generator.export(quizRepository.findAll()), "UTF-8");
        return in;
    }

    private InputStream generateAnswersInputStream() throws IOException {
        AnswersXmlExport generator = new AnswersXmlExport();
        InputStream in = IOUtils.toInputStream(generator.export(quizAnswerRepository.findAll()), "UTF-8");
        return in;
    }

}
