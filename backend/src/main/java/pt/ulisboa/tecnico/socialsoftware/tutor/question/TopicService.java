package pt.ulisboa.tecnico.socialsoftware.tutor.question;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.CourseExecutionService;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.TopicsXmlExport;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.TopicsXmlImport;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Course;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.TopicDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.TopicMoveDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.TopicNodeDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.TopicSourceDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.TopicTreeDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.CourseRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.TopicRepository;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*;

@Service
public class TopicService {
    @Autowired
    private QuestionService questionService;

    @Autowired
    private CourseExecutionService courseExecutionService;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private TopicRepository topicRepository;

    @Retryable(
            value = {SQLException.class},
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public List<TopicDto> findTopics(int courseId) {
        Course course = courseRepository.findById(courseId).orElseThrow(() -> new TutorException(COURSE_NOT_FOUND, courseId));
        return topicRepository.findTopics(course.getId()).stream().sorted(Comparator.comparing(Topic::getName)).map(TopicDto::new).collect(Collectors.toList());
    }

    @Retryable(
            value = {SQLException.class},
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public TopicDto createTopic(int courseId, TopicDto topicDto) {

        Course course = courseRepository.findById(courseId).orElseThrow(() -> new TutorException(COURSE_NOT_FOUND, courseId));

        if (topicRepository.findTopicByName(course.getId(), topicDto.getName()) != null) {
            throw new TutorException(DUPLICATE_TOPIC, topicDto.getName());
        }

        List<Topic> courseTopics = topicRepository.findTopics(course.getId());
        Integer parentId = topicDto.getParentId();
        if (parentId != null)
            findInCourse(courseTopics, parentId);

        Topic topic = new Topic(course, topicDto);
        topic.setParentId(parentId);
        topic.setSequence(nextSequence(courseTopics, parentId, null));
        topicRepository.save(topic);
        return new TopicDto(topic);
    }

    /** The course's topics as a tree: siblings in their saved order, each with the document sections it is taught from. */
    @Retryable(
            value = {SQLException.class},
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public List<TopicNodeDto> findTopicTree(int courseId) {
        Course course = courseRepository.findById(courseId).orElseThrow(() -> new TutorException(COURSE_NOT_FOUND, courseId));
        return topicRepository.findTopics(course.getId()).stream()
                .sorted(Comparator.comparing((Topic topic) -> topic.getSequence() == null ? Integer.MAX_VALUE : topic.getSequence())
                        .thenComparing(Topic::getName))
                .map(TopicNodeDto::new)
                .collect(Collectors.toList());
    }

    @Retryable(
            value = {SQLException.class},
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public TopicNodeDto moveTopic(Integer topicId, TopicMoveDto move) {
        Topic topic = topicRepository.findTopicWithCourseById(topicId).orElseThrow(() -> new TutorException(TOPIC_NOT_FOUND, topicId));
        List<Topic> courseTopics = topicRepository.findTopics(topic.getCourse().getId());

        Integer parentId = move.getParentId();
        if (parentId != null) {
            Topic parent = findInCourse(courseTopics, parentId);
            if (isSelfOrDescendant(parent, topicId, courseTopics))
                throw new TutorException(TOPIC_PARENT_CYCLE);
        }

        topic.setParentId(parentId);
        topic.setSequence(move.getSequence() != null ? move.getSequence() : nextSequence(courseTopics, parentId, topicId));
        return new TopicNodeDto(topic);
    }

    /** Replaces the document sections a topic is taught from. */
    @Retryable(
            value = {SQLException.class},
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public TopicNodeDto updateTopicSources(Integer topicId, List<TopicSourceDto> sources) {
        Topic topic = topicRepository.findById(topicId).orElseThrow(() -> new TutorException(TOPIC_NOT_FOUND, topicId));

        topic.replaceSources(sources == null ? List.of() : sources);
        return new TopicNodeDto(topic);
    }

    /** The document sections of a topic and of every topic below it, without repeats. */
    @Retryable(
            value = {SQLException.class},
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public List<TopicSourceDto> findSourcesOfSubtree(Integer topicId) {
        Topic root = topicRepository.findTopicWithCourseById(topicId).orElseThrow(() -> new TutorException(TOPIC_NOT_FOUND, topicId));
        List<Topic> courseTopics = topicRepository.findTopics(root.getCourse().getId());

        Set<Integer> subtree = new HashSet<>(Set.of(root.getId()));
        boolean grew = true;
        while (grew) {
            grew = false;
            for (Topic topic : courseTopics) {
                if (topic.getParentId() != null && subtree.contains(topic.getParentId()) && subtree.add(topic.getId()))
                    grew = true;
            }
        }

        Map<List<String>, TopicSourceDto> distinct = new LinkedHashMap<>();
        courseTopics.stream()
                .filter(topic -> subtree.contains(topic.getId()))
                .sorted(Comparator.comparing(Topic::getId))
                .flatMap(topic -> topic.getSources().stream())
                .forEach(source -> distinct.putIfAbsent(List.of(source.getMaterialId(), source.getSectionPath()), new TopicSourceDto(source)));
        return new ArrayList<>(distinct.values());
    }

    /**
     * Creates the topics of a tree the teacher accepted (or edited) and links them to their document
     * sections, all or nothing. A node that names an existing topic only adds its sources to it.
     */
    @Retryable(
            value = {SQLException.class},
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public List<TopicNodeDto> saveTopicTree(int courseId, TopicTreeDto tree) {
        Course course = courseRepository.findById(courseId).orElseThrow(() -> new TutorException(COURSE_NOT_FOUND, courseId));
        List<TopicTreeDto.Node> nodes = tree == null || tree.getNodes() == null ? List.of() : tree.getNodes();

        Map<String, TopicTreeDto.Node> byKey = new LinkedHashMap<>();
        for (TopicTreeDto.Node node : nodes) {
            if (node.getKey() == null || node.getKey().isBlank() || byKey.put(node.getKey(), node) != null)
                throw new TutorException(TOPIC_TREE_INVALID, "a node has no key, or two nodes share one");
        }

        List<Topic> courseTopics = new ArrayList<>(topicRepository.findTopics(course.getId()));
        Map<String, Topic> resolved = new LinkedHashMap<>();
        for (TopicTreeDto.Node node : nodes)
            resolveNode(node, byKey, resolved, new HashSet<>(), course, courseTopics);

        return resolved.values().stream().map(TopicNodeDto::new).collect(Collectors.toList());
    }

    private Topic resolveNode(TopicTreeDto.Node node, Map<String, TopicTreeDto.Node> byKey, Map<String, Topic> resolved,
                              Set<String> visiting, Course course, List<Topic> courseTopics) {
        Topic done = resolved.get(node.getKey());
        if (done != null)
            return done;
        if (!visiting.add(node.getKey()))
            throw new TutorException(TOPIC_TREE_INVALID, "a node is its own ancestor");

        Integer parentId = null;
        if (node.getParentKey() != null) {
            TopicTreeDto.Node parentNode = byKey.get(node.getParentKey());
            if (parentNode == null)
                throw new TutorException(TOPIC_TREE_INVALID, "unknown parent " + node.getParentKey());
            parentId = resolveNode(parentNode, byKey, resolved, visiting, course, courseTopics).getId();
        }

        Topic topic;
        if (node.getExistingTopicId() != null) {
            topic = findInCourse(courseTopics, node.getExistingTopicId());
        } else {
            String name = node.getName() == null ? "" : node.getName().trim();
            if (courseTopics.stream().anyMatch(existing -> existing.getName().equals(name)))
                throw new TutorException(DUPLICATE_TOPIC, name);

            topic = new Topic(course, new TopicDto(name));
            topic.setParentId(parentId);
            topic.setSequence(nextSequence(courseTopics, parentId, null));
            topicRepository.save(topic);
            courseTopics.add(topic);
        }

        if (node.getSources() != null)
            node.getSources().forEach(source -> topic.addSource(source.getMaterialId(), source.getSectionPath()));

        visiting.remove(node.getKey());
        resolved.put(node.getKey(), topic);
        return topic;
    }

    private Topic findInCourse(List<Topic> courseTopics, Integer topicId) {
        return courseTopics.stream()
                .filter(topic -> topic.getId().equals(topicId))
                .findFirst()
                .orElseThrow(() -> new TutorException(TOPIC_NOT_FOUND, topicId));
    }

    /** True when `candidate` is the topic `ancestorId` itself or lies below it. */
    private boolean isSelfOrDescendant(Topic candidate, Integer ancestorId, List<Topic> courseTopics) {
        Map<Integer, Topic> byId = courseTopics.stream().collect(Collectors.toMap(Topic::getId, topic -> topic));
        Set<Integer> seen = new HashSet<>();

        Topic current = candidate;
        while (current != null && seen.add(current.getId())) {
            if (current.getId().equals(ancestorId))
                return true;
            current = current.getParentId() == null ? null : byId.get(current.getParentId());
        }
        return false;
    }

    private int nextSequence(List<Topic> courseTopics, Integer parentId, Integer ignoredTopicId) {
        return courseTopics.stream()
                .filter(topic -> Objects.equals(topic.getParentId(), parentId)
                        && !topic.getId().equals(ignoredTopicId) && topic.getSequence() != null)
                .mapToInt(Topic::getSequence)
                .max()
                .orElse(-1) + 1;
    }

    @Retryable(
            value = {SQLException.class},
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public List<QuestionDto> getTopicQuestions(Integer topicId) {
        Topic topic = topicRepository.findById(topicId).orElseThrow(() -> new TutorException(TOPIC_NOT_FOUND, topicId));

        return topic.getQuestions().stream().map(QuestionDto::new).collect(Collectors.toList());
    }

    @Retryable(
            value = {SQLException.class},
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public TopicDto updateTopic(Integer topicId, TopicDto topicDto) {
        Topic topic = topicRepository.findById(topicId).orElseThrow(() -> new TutorException(TOPIC_NOT_FOUND, topicId));

        topic.setName(topicDto.getName());
        return new TopicDto(topic);
    }

    @Retryable(
            value = {SQLException.class},
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void removeTopic(Integer topicId) {
        Topic topic = topicRepository.findById(topicId)
                .orElseThrow(() -> new TutorException(TOPIC_NOT_FOUND, topicId));

        // Its subtopics move up to where it was, so removing a chapter does not lose its sections
        for (Topic child : topicRepository.findByParentId(topicId))
            child.setParentId(topic.getParentId());

        topic.remove();
        topicRepository.delete(topic);
    }

    @Retryable(
            value = {SQLException.class},
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public String exportTopics() {
        TopicsXmlExport xmlExport = new TopicsXmlExport();

        return xmlExport.export(topicRepository.findAll());
    }

    @Retryable(
            value = {SQLException.class},
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void importTopics(String topicsXML) {
        TopicsXmlImport xmlImporter = new TopicsXmlImport();

        xmlImporter.importTopics(topicsXML, this, questionService, courseRepository);
    }

}

