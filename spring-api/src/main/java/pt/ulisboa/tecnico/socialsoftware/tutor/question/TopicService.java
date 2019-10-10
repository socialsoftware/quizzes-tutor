package pt.ulisboa.tecnico.socialsoftware.tutor.question;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
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
    private static Logger logger = LoggerFactory.getLogger(TopicService.class);

    @Autowired
    private QuestionService questionService;

    @Autowired
    private TopicRepository topicRepository;

    @PersistenceContext
    EntityManager entityManager;

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public List<TopicDto> findAllTopics() {
        return topicRepository.findAll().stream().sorted(Comparator.comparing(Topic::getName)).map(TopicDto::new).collect(Collectors.toList());
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public TopicDto createTopic(TopicDto topicDto) {
        if (topicRepository.findByName(topicDto.getName()) != null) {
            throw new TutorException(DUPLICATE_TOPIC, topicDto.getName());
        }

        Topic topic = new Topic(topicDto);
        this.entityManager.persist(topic);
        return new TopicDto(topic);
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public TopicDto updateTopic(Integer topicId, TopicDto topicDto) {
        Topic topic = topicRepository.findById(topicId).orElseThrow(() -> new TutorException(TOPIC_NOT_FOUND, topicId));

        topic.setName(topicDto.getName());
        return new TopicDto(topic);
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void removeTopic(Integer topicId) {
        Topic topic = topicRepository.findById(topicId)
                .orElseThrow(() -> new TutorException(TOPIC_NOT_FOUND, topicId));

        logger.debug("removeTopic topic: {}: ", topicId);

        topic.remove();
        entityManager.remove(topic);
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public String exportTopics() {
        TopicsXmlExport xmlExport = new TopicsXmlExport();

        return xmlExport.export(topicRepository.findAll());
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void importTopics(String topicsXML) {
        TopicsXmlImport xmlImporter = new TopicsXmlImport();

        xmlImporter.importTopics(topicsXML, this, questionService);
    }
}

