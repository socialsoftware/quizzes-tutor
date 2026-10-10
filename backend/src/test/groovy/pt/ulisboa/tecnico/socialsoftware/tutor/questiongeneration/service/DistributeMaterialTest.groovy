package pt.ulisboa.tecnico.socialsoftware.tutor.questiongeneration.service

import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.TopicDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.TopicSourceDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.TopicTreeDto

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*

@DataJpaTest
class DistributeMaterialTest extends SpockTest {
    def networks

    def setup() {
        aqgClient.reset()
        createExternalCourseAndExecution()
        aqgClient.addMaterial('book', externalCourse.getId(), ['Networks', 'Networks > HTTP', 'Networks > DNS', 'Foreword'])
        aqgClient.addMaterial('slides', externalCourse.getId(), ['Intro'])

        def dto = new TopicDto()
        dto.setName('Networks')
        networks = topicService.createTopic(externalCourse.getId(), dto)
    }

    def node(String key, String name, String parentKey = null, Integer existingTopicId = null, List<String> chunkIds = [], String material = 'book') {
        def node = new TopicTreeDto.Node(key, name, parentKey)
        node.setExistingTopicId(existingTopicId)
        node.setSources(chunkIds.collect { new TopicSourceDto(material, it) })
        return node
    }

    def distribute(List nodes, String material = 'book') {
        return questionGenerationService.distributeMaterial(externalCourseExecution.getId(), material, new TopicTreeDto(nodes))
    }

    def chunksOf(String topicName) {
        return topicRepository.findTopics(externalCourse.getId()).find { it.getName() == topicName }.getSources()*.chunkId as Set
    }

    def "a document is put under existing and new topics in one go"() {
        when:
        distribute([
                node('t', 'Networks', null, networks.getId(), ['book:0']),
                node('n1', 'HTTP', 't', null, ['book:1']),
                node('n2', 'DNS', 't', null, ['book:2']),
        ])

        then: "the new topics are created under the existing one"
        def topics = topicRepository.findTopics(externalCourse.getId())
        topics*.getName() as Set == ['Networks', 'HTTP', 'DNS'] as Set
        topics.findAll { it.getName() != 'Networks' }.every { it.getParentId() == networks.getId() }

        and: "each piece is under its topic and the one left out is under none"
        chunksOf('Networks') == ['book:0'] as Set
        chunksOf('HTTP') == ['book:1'] as Set
        chunksOf('DNS') == ['book:2'] as Set

        and: "the pieces are shown with their topics"
        def http = topics.find { it.getName() == 'HTTP' }.getId()
        def dns = topics.find { it.getName() == 'DNS' }.getId()
        questionGenerationService.getMaterialChunks(externalCourseExecution.getId(), 'book')*.topicId() ==
                [networks.getId(), http, dns, null]
    }

    def "distributing a document again replaces where its pieces were, and only its pieces"() {
        given:
        topicService.updateTopicSources(networks.getId(), [new TopicSourceDto('book', 'book:0'), new TopicSourceDto('book', 'book:1'),
                                                           new TopicSourceDto('slides', 'slides:0')])

        when:
        distribute([node('t', 'Networks', null, networks.getId(), ['book:2'])])

        then:
        chunksOf('Networks') == ['book:2', 'slides:0'] as Set
    }

    def "a piece moves from one topic to another"() {
        given:
        def dto = new TopicDto()
        dto.setName('Protocols')
        def protocols = topicService.createTopic(externalCourse.getId(), dto)
        topicService.updateTopicSources(networks.getId(), [new TopicSourceDto('book', 'book:1')])

        when:
        distribute([node('p', 'Protocols', null, protocols.getId(), ['book:1'])])

        then:
        chunksOf('Networks').isEmpty()
        chunksOf('Protocols') == ['book:1'] as Set
    }

    def "how many pieces of each document are under a topic is shown with the materials"() {
        given:
        distribute([node('t', 'Networks', null, networks.getId(), ['book:0', 'book:1'])])

        when:
        def materials = questionGenerationService.getMaterials(externalCourseExecution.getId())

        then:
        materials.find { it.getId() == 'book' }.getPlacedChunks() == 2
        materials.find { it.getId() == 'slides' }.getPlacedChunks() == 0
    }

    def "#what is refused"() {
        when:
        distribute(nodes)

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == error

        and: "nothing changed"
        topicRepository.findTopics(externalCourse.getId())*.getName() == ['Networks']

        where:
        what                                  | nodes                                                                                    || error
        'a piece of another document'         | [node('t', 'Networks', null, null, ['slides:0'], 'slides')]                              || INVALID_TOPIC_SOURCE
        'a piece the document does not have'  | [node('n', 'New', null, null, ['book:99'])]                                              || INVALID_TOPIC_SOURCE
        'a piece under two topics'            | [node('a', 'A', null, null, ['book:0']), node('b', 'B', null, null, ['book:0'])]          || TOPIC_TREE_INVALID
        'a new topic named as an existing one'| [node('n', 'Networks', null, null, ['book:0'])]                                          || DUPLICATE_TOPIC
        'a new topic without a name'          | [node('n', ' ', null, null, ['book:0'])]                                                 || INVALID_NAME_FOR_TOPIC
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}
