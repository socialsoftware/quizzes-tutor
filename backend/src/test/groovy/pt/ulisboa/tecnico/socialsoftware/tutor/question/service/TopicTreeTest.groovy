package pt.ulisboa.tecnico.socialsoftware.tutor.question.service

import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.TopicDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.TopicMoveDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.TopicSourceDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.TopicTreeDto

@DataJpaTest
class TopicTreeTest extends SpockTest {
    def setup() {
        createExternalCourseAndExecution()
    }

    def create(String name, Integer parentId = null) {
        def dto = new TopicDto()
        dto.setName(name)
        dto.setParentId(parentId)
        return topicService.createTopic(externalCourse.getId(), dto)
    }

    def node(String key, String name, String parentKey = null, Integer existingTopicId = null, List<String> chunkIds = []) {
        def node = new TopicTreeDto.Node(key, name, parentKey)
        node.setExistingTopicId(existingTopicId)
        node.setSources(chunkIds.collect { new TopicSourceDto('m1', it) })
        return node
    }

    def move(Integer topicId, Integer parentId, Integer sequence = null) {
        def move = new TopicMoveDto()
        move.setParentId(parentId)
        move.setSequence(sequence)
        return topicService.moveTopic(topicId, move)
    }

    def "a topic is placed after its siblings"() {
        when:
        def chapter = create('Chapter 1')
        def first = create('Intro', chapter.getId())
        def second = create('Basics', chapter.getId())
        def otherChapter = create('Chapter 2')

        then:
        chapter.getParentId() == null
        first.getParentId() == chapter.getId()
        first.getSequence() == 0
        second.getSequence() == 1
        otherChapter.getSequence() == 1
    }

    def "siblings come in their saved order, not alphabetically"() {
        given:
        def chapter = create('B chapter')
        create('A chapter')
        create('Z child', chapter.getId())
        create('Y child', chapter.getId())

        when:
        def result = topicService.findTopicTree(externalCourse.getId())

        then:
        result.findAll { it.getParentId() == null }*.name == ['B chapter', 'A chapter']
        result.findAll { it.getParentId() == chapter.getId() }*.name == ['Z child', 'Y child']
    }

    def "a topic cannot be created under a topic of another course"() {
        given:
        def other = new Course('Other course', Course.Type.TECNICO)
        courseRepository.save(other)
        def foreign = new Topic()
        foreign.setName('Foreign')
        foreign.setCourse(other)
        topicRepository.save(foreign)

        when:
        create('Mine', foreign.getId())

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.TOPIC_NOT_FOUND
    }

    def "a topic moves under another one and back to the top"() {
        given:
        def chapter = create('Chapter')
        def section = create('Section')

        when:
        def moved = move(section.getId(), chapter.getId())

        then:
        moved.getParentId() == chapter.getId()
        moved.getSequence() == 0

        when:
        def back = move(section.getId(), null)

        then:
        back.getParentId() == null
    }

    def "a topic cannot move under itself or under one of its subtopics"() {
        given:
        def chapter = create('Chapter')
        def section = create('Section', chapter.getId())
        def subsection = create('Subsection', section.getId())

        when:
        move(chapter.getId(), parent == 'self' ? chapter.getId() : subsection.getId())

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.TOPIC_PARENT_CYCLE

        where:
        parent << ['self', 'descendant']
    }

    def "removing a topic moves its subtopics up to where it was"() {
        given:
        def chapter = create('Chapter')
        def section = create('Section', chapter.getId())
        def subsection = create('Subsection', section.getId())

        when:
        topicService.removeTopic(section.getId())

        then:
        topicRepository.findById(subsection.getId()).get().getParentId() == chapter.getId()
        topicRepository.findById(section.getId()).isEmpty()
    }

    def "a saved tree creates its topics under their parents, with their pieces of documents"() {
        given:
        def tree = new TopicTreeDto([
                node('a', 'Networks', null, null, ['m1:0']),
                node('b', 'HTTP', 'a', null, ['m1:1']),
                node('c', 'DNS', 'a', null, ['m1:2', 'm1:3']),
        ])

        when:
        def result = topicService.saveTopicTree(externalCourse.getId(), tree)

        then:
        result*.name == ['Networks', 'HTTP', 'DNS']
        def networks = result.find { it.name == 'Networks' }
        def http = result.find { it.name == 'HTTP' }
        def dns = result.find { it.name == 'DNS' }
        networks.getParentId() == null
        http.getParentId() == networks.getId()
        dns.getParentId() == networks.getId()
        [http.getSequence(), dns.getSequence()] == [0, 1]
        dns.getSources()*.chunkId == ['m1:2', 'm1:3']
        dns.getSources().every { it.getMaterialId() == 'm1' }
    }

    def "a child listed before its parent is still created under it"() {
        given:
        def tree = new TopicTreeDto([node('b', 'HTTP', 'a'), node('a', 'Networks')])

        when:
        def result = topicService.saveTopicTree(externalCourse.getId(), tree)

        then:
        result.find { it.name == 'HTTP' }.getParentId() == result.find { it.name == 'Networks' }.getId()
    }

    def "a node standing for an existing topic only adds its pieces to it"() {
        given:
        def existing = create('Networks')
        def tree = new TopicTreeDto([node('a', 'Anything', null, existing.getId(), ['m1:1'])])

        when:
        topicService.saveTopicTree(externalCourse.getId(), tree)

        then:
        def saved = topicRepository.findById(existing.getId()).get()
        saved.getName() == 'Networks'
        saved.getSources()*.chunkId == ['m1:1']
        topicRepository.count() == 1L
    }

    def "a new node with the name of an existing topic is refused"() {
        given:
        create('Networks')

        when:
        topicService.saveTopicTree(externalCourse.getId(), new TopicTreeDto([node('a', 'Networks')]))

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.DUPLICATE_TOPIC
    }

    def "two new nodes with the same name are refused"() {
        when:
        topicService.saveTopicTree(externalCourse.getId(), new TopicTreeDto([node('a', 'Intro'), node('b', 'Intro')]))

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.DUPLICATE_TOPIC
    }

    def "a tree with an unknown parent, a loop or a repeated key is refused"() {
        when:
        topicService.saveTopicTree(externalCourse.getId(), new TopicTreeDto(nodes))

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.TOPIC_TREE_INVALID

        where:
        nodes << [
                [node('a', 'A', 'missing')],
                [node('a', 'A', 'b'), node('b', 'B', 'a')],
                [node('a', 'A'), node('a', 'B')],
        ]
    }

    def "the same piece is not linked twice to a topic"() {
        given:
        def topic = create('Networks')
        def sources = [new TopicSourceDto('m1', 'm1:0'), new TopicSourceDto('m1', 'm1:0')]

        when:
        def result = topicService.updateTopicSources(topic.getId(), sources)

        then:
        result.getSources().size() == 1
    }

    def "updating the pieces of a topic replaces them"() {
        given:
        def topic = create('Networks')
        topicService.updateTopicSources(topic.getId(), [new TopicSourceDto('m1', 'm1:0')])

        when:
        def result = topicService.updateTopicSources(topic.getId(), [new TopicSourceDto('m2', 'm2:4')])

        then:
        result.getSources()*.materialId == ['m2']
        result.getSources()*.chunkId == ['m2:4']
    }

    def "a piece put under a topic leaves the topic it was under"() {
        given:
        def first = create('First')
        def second = create('Second')
        topicService.updateTopicSources(first.getId(), [new TopicSourceDto('m1', 'm1:0'), new TopicSourceDto('m1', 'm1:1')])

        when:
        topicService.updateTopicSources(second.getId(), [new TopicSourceDto('m1', 'm1:1')])

        then:
        topicRepository.findById(first.getId()).get().getSources()*.chunkId == ['m1:0']
        topicRepository.findById(second.getId()).get().getSources()*.chunkId == ['m1:1']
    }

    def "a piece needs a material and a chunk"() {
        given:
        def topic = create('Networks')

        when:
        topicService.updateTopicSources(topic.getId(), [new TopicSourceDto(material, chunk)])

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.INVALID_TOPIC_SOURCE

        where:
        material | chunk
        null     | 'm1:0'
        ' '      | 'm1:0'
        'm1'     | null
        'm1'     | ''
    }

    def "the pieces of a topic include those of its subtopics, in tree order"() {
        given:
        def chapter = create('Chapter')
        def second = create('Second', chapter.getId())
        def first = create('First', chapter.getId())
        move(first.getId(), chapter.getId(), 0)
        move(second.getId(), chapter.getId(), 1)
        def unrelated = create('Unrelated')
        topicService.updateTopicSources(chapter.getId(), [new TopicSourceDto('m1', 'm1:0')])
        topicService.updateTopicSources(second.getId(), [new TopicSourceDto('m1', 'm1:5')])
        topicService.updateTopicSources(first.getId(), [new TopicSourceDto('m1', 'm1:3')])
        topicService.updateTopicSources(unrelated.getId(), [new TopicSourceDto('m1', 'm1:9')])

        when:
        def result = topicService.findSourcesOfSubtree(chapter.getId())

        then:
        result*.chunkId == ['m1:0', 'm1:3', 'm1:5']
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}
