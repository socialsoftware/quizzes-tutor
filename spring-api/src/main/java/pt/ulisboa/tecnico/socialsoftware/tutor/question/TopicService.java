package pt.ulisboa.tecnico.socialsoftware.tutor.question;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.TopicsXmlExport;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.TopicsXmlImport;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.TopicDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.TopicRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ExceptionError.DUPLICATE_TOPIC;
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ExceptionError.TOPIC_NOT_FOUND;

@Service
public class TopicService {

    @Autowired
    private QuestionService questionService;

    @Autowired
    private TopicRepository topicRepository;

    @PersistenceContext
    EntityManager entityManager;

    @Transactional
    public List<TopicDto> findAllTopics() {
        return topicRepository.findAll().stream().sorted(Comparator.comparing(Topic::getName)).map(TopicDto::new).collect(Collectors.toList());
    }

    @Transactional
    public void createTopic(String name) {
        Topic topic = topicRepository.findByName(name);

        if (topic != null) {
            throw new TutorException(DUPLICATE_TOPIC, name);
        }

        topic = new Topic(name);

        entityManager.persist(topic);
    }

    @Transactional
    public void updateTopic(String oldName, String newName) {
        Topic topic = topicRepository.findByName(oldName);

        if (topic == null) {
            throw new TutorException(TOPIC_NOT_FOUND, oldName);
        }

        topic.setName(newName);
    }

    @Transactional
    public void removeTopic(String name) {
        Topic topic = topicRepository.findByName(name);

        if (topic == null) {
            throw new TutorException(TOPIC_NOT_FOUND, name);
        }

        topic.remove();

        entityManager.remove(topic);
    }

    @Transactional
    public String exportTopics() {
        TopicsXmlExport xmlExport = new TopicsXmlExport();

        return xmlExport.export(topicRepository.findAll());
    }

    @Transactional
    public void importTopics(String topicsXML) {
        TopicsXmlImport xmlImporter = new TopicsXmlImport();

        xmlImporter.importTopics(topicsXML, this, questionService);
    }
}

