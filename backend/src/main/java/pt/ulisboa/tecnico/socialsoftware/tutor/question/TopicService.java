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
import java.util.stream.Stream;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*;

@Service
public class TopicService {
    // Siblings in their saved order; topics from before the tree existed after them, by name
    private static final Comparator<Topic> TREE_ORDER =
            Comparator.comparing((Topic topic) -> topic.getSequence() == null ? Integer.MAX_VALUE : topic.getSequence())
                    .thenComparing(Topic::getName);

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

    /** The course's topics as a tree: siblings in their saved order, each with the pieces of documents it is taught from. */
    @Retryable(
            value = {SQLException.class},
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public List<TopicNodeDto> findTopicTree(int courseId) {
        Course course = courseRepository.findById(courseId).orElseThrow(() -> new TutorException(COURSE_NOT_FOUND, courseId));
        return topicRepository.findTopics(course.getId()).stream()
                .sorted(TREE_ORDER)
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

    /**
     * Replaces the pieces of documents a topic is taught from. A piece that was under another
     * topic moves to this one, since a piece belongs to one topic at most.
     */
    @Retryable(
            value = {SQLException.class},
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public TopicNodeDto updateTopicSources(Integer topicId, List<TopicSourceDto> sources) {
        Topic topic = topicRepository.findTopicWithCourseById(topicId).orElseThrow(() -> new TutorException(TOPIC_NOT_FOUND, topicId));
        List<TopicSourceDto> newSources = sources == null ? List.of() : sources;

        Set<String> chunkIds = newSources.stream().map(TopicSourceDto::getChunkId).collect(Collectors.toSet());
        detachChunks(topicRepository.findTopics(topic.getCourse().getId()), chunkIds);
        topic.replaceSources(newSources);
        return new TopicNodeDto(topic);
    }

    /** The pieces of documents under a topic and under every topic below it, in tree order. */
    @Retryable(
            value = {SQLException.class},
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public List<TopicSourceDto> findSourcesOfSubtree(Integer topicId) {
        Topic root = topicRepository.findTopicWithCourseById(topicId).orElseThrow(() -> new TutorException(TOPIC_NOT_FOUND, topicId));
        List<Topic> courseTopics = topicRepository.findTopics(root.getCourse().getId());

        Map<String, TopicSourceDto> distinct = new LinkedHashMap<>();
        collectSources(root, courseTopics, distinct, new HashSet<>());
        return new ArrayList<>(distinct.values());
    }

    // Depth first, children in their saved order, so the text of a chapter keeps the order of its sections
    private void collectSources(Topic topic, List<Topic> courseTopics, Map<String, TopicSourceDto> into, Set<Integer> seen) {
        if (!seen.add(topic.getId()))
            return;
        topic.getSources().forEach(source -> into.putIfAbsent(source.getChunkId(), new TopicSourceDto(source)));
        courseTopics.stream()
                .filter(child -> topic.getId().equals(child.getParentId()))
                .sorted(TREE_ORDER)
                .forEach(child -> collectSources(child, courseTopics, into, seen));
    }

    /**
     * Creates the topics of a tree the teacher accepted (or edited) and puts their pieces of
     * documents under them, all or nothing. A node that stands for an existing topic only gets its
     * pieces. Pieces that were under other topics move.
     */
    @Retryable(
            value = {SQLException.class},
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public List<TopicNodeDto> saveTopicTree(int courseId, TopicTreeDto tree) {
        Course course = courseRepository.findById(courseId).orElseThrow(() -> new TutorException(COURSE_NOT_FOUND, courseId));
        List<TopicTreeDto.Node> nodes = nodesOf(tree);

        List<Topic> courseTopics = new ArrayList<>(topicRepository.findTopics(course.getId()));
        detachChunks(courseTopics, chunkIdsOf(nodes));
        return resolveTree(nodes, course, courseTopics);
    }

    /**
     * Puts the pieces of one document under topics, replacing where they were before: a piece of
     * the document that is in no node of `tree` ends up under no topic, so it is left out of
     * question generation. `documentChunkIds` are the pieces the document has.
     */
    @Retryable(
            value = {SQLException.class},
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public List<TopicNodeDto> distributeMaterial(int courseId, String materialId, Set<String> documentChunkIds, TopicTreeDto tree) {
        Course course = courseRepository.findById(courseId).orElseThrow(() -> new TutorException(COURSE_NOT_FOUND, courseId));
        List<TopicTreeDto.Node> nodes = nodesOf(tree);

        Set<String> placed = new HashSet<>();
        for (TopicTreeDto.Node node : nodes) {
            for (TopicSourceDto source : node.getSources() == null ? List.<TopicSourceDto>of() : node.getSources()) {
                if (!materialId.equals(source.getMaterialId()) || !documentChunkIds.contains(source.getChunkId()))
                    throw new TutorException(INVALID_TOPIC_SOURCE);
                if (!placed.add(source.getChunkId()))
                    throw new TutorException(TOPIC_TREE_INVALID, "a piece of the document is under two topics");
            }
        }

        List<Topic> courseTopics = new ArrayList<>(topicRepository.findTopics(course.getId()));
        detachChunks(courseTopics, documentChunkIds);
        return resolveTree(nodes, course, courseTopics);
    }

    /** How many pieces of each document are under some topic of the course. */
    @Retryable(
            value = {SQLException.class},
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Map<String, Integer> countPlacedChunks(int courseId) {
        Map<String, Integer> counts = new LinkedHashMap<>();
        topicRepository.findTopics(courseId).forEach(topic -> topic.getSources()
                .forEach(source -> counts.merge(source.getMaterialId(), 1, Integer::sum)));
        return counts;
    }

    /** The topic each piece of a document is under, by chunk id; pieces under no topic are absent. */
    @Retryable(
            value = {SQLException.class},
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Map<String, Integer> findTopicsOfChunks(int courseId, String materialId) {
        Map<String, Integer> topicOfChunk = new LinkedHashMap<>();
        topicRepository.findTopics(courseId).forEach(topic -> topic.getSources().stream()
                .filter(source -> materialId.equals(source.getMaterialId()))
                .forEach(source -> topicOfChunk.put(source.getChunkId(), topic.getId())));
        return topicOfChunk;
    }

    /** Takes a document's pieces from every topic, e.g. after the document was read again and its pieces changed. */
    @Retryable(
            value = {SQLException.class},
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void detachMaterial(int courseId, String materialId) {
        topicRepository.findTopics(courseId).forEach(topic -> topic.getSources().removeIf(source -> materialId.equals(source.getMaterialId())));
    }

    private List<TopicTreeDto.Node> nodesOf(TopicTreeDto tree) {
        List<TopicTreeDto.Node> nodes = tree == null || tree.getNodes() == null ? List.of() : tree.getNodes();
        Set<String> keys = new HashSet<>();
        for (TopicTreeDto.Node node : nodes) {
            if (node.getKey() == null || node.getKey().isBlank() || !keys.add(node.getKey()))
                throw new TutorException(TOPIC_TREE_INVALID, "a node has no key, or two nodes share one");
        }
        return nodes;
    }

    private Set<String> chunkIdsOf(List<TopicTreeDto.Node> nodes) {
        return nodes.stream()
                .flatMap(node -> node.getSources() == null ? Stream.<TopicSourceDto>empty() : node.getSources().stream())
                .map(TopicSourceDto::getChunkId)
                .collect(Collectors.toSet());
    }

    /**
     * Takes the given pieces away from every topic and writes that to the database at once: a piece
     * belongs to one topic at most (unique chunk_id), and the new links are inserted before the
     * old ones would otherwise be deleted.
     */
    private void detachChunks(List<Topic> courseTopics, Set<String> chunkIds) {
        if (chunkIds.isEmpty())
            return;
        boolean changed = false;
        for (Topic topic : courseTopics)
            changed |= topic.removeSources(chunkIds);
        if (changed)
            topicRepository.flush();
    }

    private List<TopicNodeDto> resolveTree(List<TopicTreeDto.Node> nodes, Course course, List<Topic> courseTopics) {
        Map<String, TopicTreeDto.Node> byKey = new LinkedHashMap<>();
        nodes.forEach(node -> byKey.put(node.getKey(), node));

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
            node.getSources().forEach(source -> topic.addSource(source.getMaterialId(), source.getChunkId()));

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

        // Its subtopics move up to where it was; its own pieces of documents end up under no topic
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

