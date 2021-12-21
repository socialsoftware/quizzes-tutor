package pt.ulisboa.tecnico.socialsoftware.tutor.question;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.StatementQuestionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.discussion.domain.Reply;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.LatexQuestionExportVisitor;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.QuestionsXmlImport;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.XMLQuestionExportVisitor;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Course;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Image;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionQuery;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.TopicDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.*;
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsubmission.domain.QuestionSubmission;
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsubmission.repository.QuestionSubmissionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository.QuizQuestionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.utils.DateHandler;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
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
    private TopicService topicService;

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private QuizQuestionRepository quizQuestionRepository;

    @Autowired
    private OptionRepository optionRepository;

    @Autowired
    private CodeFillInOptionRepository codeFillInOptionRepository;

    @Autowired
    private CodeFillInQuestionRepository codeFillInQuestionRepository;

    @Autowired
    private CodeOrderSlotRepository codeOrderSlotRepository;

    @Autowired
    private QuestionSubmissionRepository questionSubmissionRepository;

    @Retryable(
            value = {SQLException.class},
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public QuestionDto findQuestionById(Integer questionId) {
        return questionRepository.findById(questionId).map(QuestionDto::new)
                .orElseThrow(() -> new TutorException(QUESTION_NOT_FOUND, questionId));
    }

    @Retryable(
            value = {SQLException.class},
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public StatementQuestionDto getStatementQuestionDto(Integer questionId) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new TutorException(QUESTION_NOT_FOUND, questionId));
        return new StatementQuestionDto(question);
    }

    @Retryable(
      value = { SQLException.class },
      backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public QuestionDto findQuestionByKey(Integer key) {
        return questionRepository.findByKey(key).map(QuestionDto::new)
                .orElseThrow(() -> new TutorException(QUESTION_NOT_FOUND, key));
    }

    @Retryable(
            value = {SQLException.class},
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public List<QuestionDto> findQuestions(int courseId) {
        return questionRepository.findQuestions(courseId).stream().filter(q -> !q.isInSubmission()).map(QuestionDto::new).collect(Collectors.toList());
    }

    @Retryable(
            value = {SQLException.class},
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public List<QuestionDto> findQuestionsByQuery(int courseId, QuestionQuery query) {
        if (query.getBeginCreationDate() != null && query.getEndCreationDate() != null
                && DateHandler.toLocalDateTime(query.getBeginCreationDate()).isAfter(DateHandler.toLocalDateTime(query.getEndCreationDate()))) {
            throw new TutorException(INVALID_DATE_INTERVAL);
        }

        Collection<Question> questions;
        if  (query.getContent().trim().length() == 0) {
            questions = questionRepository.findQuestions(courseId);
        } else {
            questions = questionRepository.findQuestionsByContent(courseId, query.getContent().trim());
            questions.addAll(optionRepository.findQuestionsByOptionContent(courseId, query.getContent().trim()));
            questions.addAll(codeFillInQuestionRepository.findQuestionsByCodeFillInQuestionContent(courseId, query.getContent().trim()));
            questions.addAll(codeFillInOptionRepository.findQuestionsByCodeFillInOptionContent(courseId, query.getContent().trim()));
            questions.addAll(codeOrderSlotRepository.findQuestionsByCodeOrderSlotContent(courseId, query.getContent().trim()));
        }

        List<QuestionDto> result = new ArrayList<>();
        for (Question question: questions) {
            if (!question.isInSubmission()
                    && (query.getBeginCreationDate() == null
                        || (question.getCreationDate() != null && DateHandler.toLocalDateTime(query.getBeginCreationDate()).isBefore(question.getCreationDate())))
                    && (query.getEndCreationDate() == null
                        || (question.getCreationDate() != null && DateHandler.toLocalDateTime(query.getEndCreationDate()).isAfter(question.getCreationDate())))
                    && (!query.isClarificationsOnly() || question.getDiscussions().stream().flatMap(discussion -> discussion.getReplies().stream()).anyMatch(Reply::isPublic))
                    && (!query.isNoAnswersOnly() || question.getNumberOfAnswers() == 0)
                    && ((question.getDifficulty() == null && query.getDifficulty()[0] == 0 && query.getDifficulty()[1] == 100)
                        || (question.getDifficulty() != null && question.getDifficulty() >= query.getDifficulty()[0]  && question.getDifficulty() <= query.getDifficulty()[1]))
                    && (query.getStatus().size() == 0 || query.getStatus().size() == 3 || query.getStatus().contains(question.getStatus().name()))
                    && (query.getTopics().isEmpty() || question.hasTopics(query.getTopics()))) {
                result.add(new QuestionDto(question));
            }
        }

        return result;
    }

    @Retryable(
            value = {SQLException.class},
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public List<QuestionDto> findAvailableQuestions(int courseId) {
        return questionRepository.findAvailableQuestions(courseId).stream().map(QuestionDto::new).collect(Collectors.toList());
    }

    @Retryable(
            value = {SQLException.class},
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public QuestionDto createQuestion(int courseId, QuestionDto questionDto) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new TutorException(COURSE_NOT_FOUND, courseId));
        Question question = new Question(course, questionDto);
        questionRepository.save(question);
        return new QuestionDto(question);
    }

    @Retryable(
            value = {SQLException.class},
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public QuestionDto updateQuestion(Integer questionId, QuestionDto questionDto) {
        Question question = questionRepository
                .findById(questionId)
                .orElseThrow(() -> new TutorException(QUESTION_NOT_FOUND, questionId));
        question.update(questionDto);
        return new QuestionDto(question);
    }

    @Retryable(
            value = {SQLException.class},
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void removeQuestion(Integer questionId) {
        Question question = questionRepository.findById(questionId).orElseThrow(() -> new TutorException(QUESTION_NOT_FOUND, questionId));
        QuestionSubmission questionSubmission = questionSubmissionRepository.findQuestionSubmissionByQuestionId(question.getId());

        if (questionSubmission != null) {
            throw new TutorException(CANNOT_DELETE_SUBMITTED_QUESTION);
        }

        question.remove();
        questionRepository.delete(question);
    }

    @Retryable(
            value = {SQLException.class},
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void questionSetStatus(Integer questionId, Question.Status status) {
        Question question = questionRepository.findById(questionId).orElseThrow(() -> new TutorException(QUESTION_NOT_FOUND, questionId));
        question.setStatus(status);
    }

    @Retryable(
            value = {SQLException.class},
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void uploadImage(Integer questionId, String type) {
        Question question = questionRepository.findById(questionId).orElseThrow(() -> new TutorException(QUESTION_NOT_FOUND, questionId));

        Image image = question.getImage();
        String imageName = question.getCourse().getName().replaceAll("\\s", "") +
                question.getCourse().getType() +
                question.getKey() +
                "." + type;

        if (image == null) {
            image = new Image(imageName);

            question.setImage(image);

            imageRepository.save(image);
        } else {
            question.getImage().setUrl(imageName);
        }
    }

    @Retryable(
            value = {SQLException.class},
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void updateQuestionTopics(Integer questionId, TopicDto[] topics) {
        Question question = questionRepository.findById(questionId).orElseThrow(() -> new TutorException(QUESTION_NOT_FOUND, questionId));

        question.updateTopics(Arrays.stream(topics).map(topicDto -> topicRepository.findTopicByName(question.getCourse().getId(), topicDto.getName())).collect(Collectors.toSet()));
    }

    public String exportQuestionsToXml() {
        XMLQuestionExportVisitor xmlExporter = new XMLQuestionExportVisitor();

        return xmlExporter.export(questionRepository.findAll());
    }

    @Retryable(
            value = {SQLException.class},
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void importQuestionsFromXml(String questionsXML) {
        QuestionsXmlImport xmlImporter = new QuestionsXmlImport();

        xmlImporter.importQuestions(questionsXML, this, this.topicService, courseRepository);
    }


    @Retryable(
            value = {SQLException.class},
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public String exportQuestionsToLatex() {
        LatexQuestionExportVisitor latexExporter = new LatexQuestionExportVisitor();

        return latexExporter.export(questionRepository.findAll());
    }

    @Retryable(
            value = {SQLException.class},
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public ByteArrayOutputStream exportCourseQuestions(int courseId) {
        Course course = courseRepository.findById(courseId).orElseThrow(() -> new TutorException(COURSE_NOT_FOUND, courseId));

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
    public void deleteQuizQuestion(QuizQuestion quizQuestion) {
        Question question = quizQuestion.getQuestion();
        quizQuestion.remove();
        quizQuestionRepository.delete(quizQuestion);

        if (question.getQuizQuestions().isEmpty() && questionSubmissionRepository.findQuestionSubmissionByQuestionId(question.getId()) == null) {
            this.removeQuestion(question.getId());
        }
    }

    @Retryable(
            value = {SQLException.class},
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void deleteQuestion(Question question) {
        if(question.getQuestionDetails() != null) {
            question.getQuestionDetails().delete();
        }

        if (question.getImage() != null) {
            imageRepository.delete(question.getImage());
        }

        question.getTopics().forEach(topic -> topic.getQuestions().remove(question));
        question.getTopics().clear();

        questionRepository.delete(question);
    }
}

