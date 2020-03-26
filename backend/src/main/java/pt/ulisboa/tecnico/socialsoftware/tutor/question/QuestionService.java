package pt.ulisboa.tecnico.socialsoftware.tutor.question;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.*;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Image;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.TopicDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.ImageRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.TopicRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*;

@Service
public class QuestionService {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private TopicRepository topicRepository;

    @Autowired
    private ImageRepository imageRepository;

    @Retryable(
      value = { SQLException.class },
      backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public QuestionDto findQuestionById(Integer questionId) {
        return questionRepository.findById(questionId).map(QuestionDto::new)
                .orElseThrow(() -> new TutorException(QUESTION_NOT_FOUND, questionId));
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public CourseDto findQuestionCourse(Integer questionId) {
        return questionRepository.findById(questionId)
                .map(Question::getCourse)
                .map(CourseDto::new)
                .orElseThrow(() -> new TutorException(QUESTION_NOT_FOUND, questionId));
    }

    @Retryable(
      value = { SQLException.class },
      backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public QuestionDto findQuestionByKey(Integer key) {
        return questionRepository.findByKey(key).map(QuestionDto::new)
                .orElseThrow(() -> new TutorException(QUESTION_NOT_FOUND, key));
    }

    @Retryable(
      value = { SQLException.class },
      backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public List<QuestionDto> findQuestions(int courseId) {
        return questionRepository.findQuestions(courseId).stream().map(QuestionDto::new).collect(Collectors.toList());
    }

    @Retryable(
      value = { SQLException.class },
      backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public List<QuestionDto> findAvailableQuestions(int courseId) {
        return questionRepository.findAvailableQuestions(courseId).stream().map(QuestionDto::new).collect(Collectors.toList());
    }

    @Retryable(
      value = { SQLException.class },
      backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public QuestionDto createQuestion(int courseId, QuestionDto questionDto) {
        Course course = courseRepository.findById(courseId).orElseThrow(() -> new TutorException(COURSE_NOT_FOUND, courseId));

        if (questionDto.getCreationDate() == null) {
            questionDto.setCreationDate(LocalDateTime.now().format(Course.formatter));
        }

        Question question = new Question(course, questionDto);
        questionRepository.save(question);
        return new QuestionDto(question);
    }


    @Retryable(
      value = { SQLException.class },
      backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public QuestionDto updateQuestion(Integer questionId, QuestionDto questionDto) {
        Question question = questionRepository.findById(questionId).orElseThrow(() -> new TutorException(QUESTION_NOT_FOUND, questionId));
        question.update(questionDto);
        return new QuestionDto(question);
    }


    @Retryable(
      value = { SQLException.class },
      backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void removeQuestion(Integer questionId) {
        Question question = questionRepository.findById(questionId).orElseThrow(() -> new TutorException(QUESTION_NOT_FOUND, questionId));
        question.remove();
        questionRepository.delete(question);
    }

    @Retryable(
      value = { SQLException.class },
      backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void questionSetStatus(Integer questionId, Question.Status status) {
        Question question = questionRepository.findById(questionId).orElseThrow(() -> new TutorException(QUESTION_NOT_FOUND, questionId));
        question.setStatus(status);
    }


    @Retryable(
      value = { SQLException.class },
      backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void uploadImage(Integer questionId, String type) {
        Question question = questionRepository.findById(questionId).orElseThrow(() -> new TutorException(QUESTION_NOT_FOUND, questionId));

        Image image = question.getImage();

        if (image == null) {
            image = new Image();

            question.setImage(image);

            imageRepository.save(image);
        }

        question.getImage().setUrl(question.getKey() + "." + type);
    }

    @Retryable(
      value = { SQLException.class },
      backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void updateQuestionTopics(Integer questionId, TopicDto[] topics) {
        Question question = questionRepository.findById(questionId).orElseThrow(() -> new TutorException(QUESTION_NOT_FOUND, questionId));

        question.updateTopics(Arrays.stream(topics).map(topicDto -> topicRepository.findTopicByName(question.getCourse().getId(), topicDto.getName())).collect(Collectors.toSet()));
    }

    public String exportQuestionsToXml() {
        XMLQuestionExportVisitor xmlExporter = new XMLQuestionExportVisitor();

        return xmlExporter.export(questionRepository.findAll());
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void importQuestionsFromXml(String questionsXML) {
        QuestionsXmlImport xmlImporter = new QuestionsXmlImport();

        xmlImporter.importQuestions(questionsXML, this, courseRepository);
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public String exportQuestionsToLatex() {
        LatexQuestionExportVisitor latexExporter = new LatexQuestionExportVisitor();

        return latexExporter.export(questionRepository.findAll());
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public ByteArrayOutputStream exportCourseQuestions(int courseId) {
        Course course = courseRepository.findById(courseId).orElseThrow(() -> new TutorException(COURSE_NOT_FOUND, courseId));

        course.getQuestions();

        String name = course.getName();
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ZipOutputStream zos = new ZipOutputStream(baos)) {

            List<Question> questions = new ArrayList<>(course.getQuestions());

            XMLQuestionExportVisitor xmlExport = new XMLQuestionExportVisitor();
            InputStream in = IOUtils.toInputStream(xmlExport.export(questions), StandardCharsets.UTF_8);
            zos.putNextEntry(new ZipEntry(name + ".xml"));
            copyToZipStream(zos, in);
            zos.closeEntry();

            LatexQuestionExportVisitor latexExport = new LatexQuestionExportVisitor();
            zos.putNextEntry(new ZipEntry(name + ".tex"));
            in = IOUtils.toInputStream(latexExport.export(questions), StandardCharsets.UTF_8);
            copyToZipStream(zos, in);
            zos.closeEntry();

            zos.close();

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

}

