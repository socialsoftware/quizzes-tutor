package pt.ulisboa.tecnico.socialsoftware.tutor.question.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.QuestionsXmlExport;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.QuestionsXmlImport;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.TopicsXmlExport;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.TopicsXmlImport;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Image;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.TopicRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class QuestionService {

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private TopicRepository topicRepository;

    @PersistenceContext
    EntityManager entityManager;

    @Transactional
    public QuestionDto findQuestionById(Integer questionId) {
        return questionRepository.findById(questionId).map(QuestionDto::new)
                .orElseThrow(() -> new TutorException(TutorException.ExceptionError.QUESTION_NOT_FOUND, questionId.toString()));
    }

    @Transactional
    public QuestionDto findQuestionByNumber(Integer number) {
        return questionRepository.findByNumber(number).map(QuestionDto::new)
                .orElseThrow(() -> new TutorException(TutorException.ExceptionError.QUESTION_NOT_FOUND, number.toString()));
    }

    @Transactional
    public List<QuestionDto> findAllQuestions(Integer pageIndex, Integer pageSize) {
        return questionRepository.findAll(PageRequest.of(pageIndex, pageSize)).getContent().stream().map(QuestionDto::new).collect(Collectors.toList());
    }

    @Transactional
    public List<QuestionDto> findActiveQuestions() {
        return questionRepository.getActiveQuestions().stream().map(QuestionDto::new).collect(Collectors.toList());
    }

    @Transactional
    public QuestionDto createQuestion(QuestionDto questionDto) {
        if (questionDto.getNumber() == null) {
            Integer maxQuestionNumber = questionRepository.getMaxQuestionNumber() != null ?
                    questionRepository.getMaxQuestionNumber() : 0;
            questionDto.setNumber(maxQuestionNumber + 1);
        }

        Question question = new Question(questionDto);
        this.entityManager.persist(question);
        return new QuestionDto(question);
    }

    @Transactional
    public void updateQuestion(Integer questionId, QuestionDto questionDto) {
        Question question = questionRepository.findById(questionId).orElseThrow(() -> new TutorException(TutorException.ExceptionError.QUESTION_NOT_FOUND, questionId.toString()));
        question.update(questionDto);
    }

    @Transactional
    public void removeQuestion(Integer questionId) {
        Question question = questionRepository.findById(questionId).orElseThrow(() -> new TutorException(TutorException.ExceptionError.QUESTION_NOT_FOUND, questionId.toString()));
        question.remove();
        entityManager.remove(question);
    }

    @Transactional
    public void questionSwitchActive(Integer questionId) {
        Question question = questionRepository.findById(questionId).orElseThrow(() -> new TutorException(TutorException.ExceptionError.QUESTION_NOT_FOUND, questionId.toString()));
        question.switchActive();
    }

    @Transactional
    public void uploadImage(Integer questionId, String type) {
        Question question = questionRepository.findById(questionId).orElseThrow(() -> new TutorException(TutorException.ExceptionError.QUESTION_NOT_FOUND, questionId.toString()));

        Image image = question.getImage();

        if (image == null) {
            image = new Image();

            question.addImage(image);

            entityManager.persist(image);
        }

        question.getImage().setUrl(question.getNumber() + "." + type);
    }

    @Transactional
    public List<String> findAllTopics() {
        return topicRepository.findAll().stream().map(topic -> topic.getName()).sorted().collect(Collectors.toList());
    }

    @Transactional
    public void createTopic(String name) {
        Topic topic = topicRepository.findByName(name);

        if (topic != null) {
            throw new TutorException(TutorException.ExceptionError.DUPLICATE_TOPIC, name);
        }

        topic = new Topic(name);

        entityManager.persist(topic);
    }

    @Transactional
    public void updateTopic(String oldName, String newName) {
        Topic topic = topicRepository.findByName(oldName);

        if (topic == null) {
            throw new TutorException(TutorException.ExceptionError.TOPIC_NOT_FOUND, oldName);
        }

        topic.setName(newName);
    }

    @Transactional
    public void removeTopic(String name) {
        Topic topic = topicRepository.findByName(name);

        if (topic == null) {
            throw new TutorException(TutorException.ExceptionError.TOPIC_NOT_FOUND, name);
        }

        topic.remove();

        entityManager.remove(topic);
    }

    @Transactional
    public void updateQuestionTopics(Integer questionId, String[] topics) {
        Question question = questionRepository.findById(questionId).orElseThrow(() -> new TutorException(TutorException.ExceptionError.QUESTION_NOT_FOUND, questionId.toString()));

        question.updateTopics(Arrays.stream(topics).map(name -> topicRepository.findByName(name)).collect(Collectors.toSet()));
    }

    public String exportQuestions() {
        QuestionsXmlExport xmlExporter = new QuestionsXmlExport();

        return xmlExporter.export(questionRepository.findAll());
    }

    @Transactional
    public void importQuestions(String questionsXML) {
        QuestionsXmlImport xmlImporter = new QuestionsXmlImport();

        xmlImporter.importQuestions(questionsXML, this);
    }

    @Transactional
    public String exportTopics() {
        TopicsXmlExport xmlExport = new TopicsXmlExport();

        return xmlExport.export(topicRepository.findAll());
    }

    @Transactional
    public void importTopics(String topicsXML) {
        TopicsXmlImport xmlImporter = new TopicsXmlImport();

        xmlImporter.importTopics(topicsXML, this);
    }
}

