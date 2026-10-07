package pt.ulisboa.tecnico.socialsoftware.tutor.questiongeneration

import pt.ulisboa.tecnico.socialsoftware.tutor.questiongeneration.dto.AqgSectionDto
import spock.lang.Specification

class TopicTreeSuggesterTest extends Specification {
    def sections(String... paths) {
        return paths.collect { new AqgSectionDto(it, it.split(' > ').last(), 2) }
    }

    def "every heading becomes a topic under the heading above it"() {
        when:
        def result = TopicTreeSuggester.suggest('m1',
                sections('Networks', 'Networks > HTTP', 'Networks > DNS > Records'), [:])

        then:
        result.nodes*.key == ['Networks', 'Networks > HTTP', 'Networks > DNS', 'Networks > DNS > Records']
        result.nodes*.name == ['Networks', 'HTTP', 'DNS', 'Records']
        result.nodes*.parentKey == [null, 'Networks', 'Networks', 'Networks > DNS']
    }

    def "only the sections with text are linked, each to its own topic"() {
        when:
        def result = TopicTreeSuggester.suggest('m1', sections('Networks > DNS > Records', 'Networks > HTTP'), [:])
        def byName = result.nodes.collectEntries { [(it.name): it] }

        then: "a heading without text of its own has no section"
        byName['Networks'].sources.isEmpty()
        byName['DNS'].sources.isEmpty()

        and:
        byName['Records'].sources*.sectionPath == ['Networks > DNS > Records']
        byName['Records'].sources*.materialId == ['m1']
        byName['Records'].chunkCount == 2
        byName['HTTP'].sources*.sectionPath == ['Networks > HTTP']
    }

    def "topics keep the reading order of the document"() {
        when:
        def result = TopicTreeSuggester.suggest('m1', sections('B', 'A', 'C'), [:])

        then:
        result.nodes*.name == ['B', 'A', 'C']
    }

    def "a repeated heading is told apart by its parent, because topic names are unique"() {
        when:
        def result = TopicTreeSuggester.suggest('m1',
                sections('Part A > Introduction', 'Part B > Introduction', 'Part C > Introduction'), [:])

        then:
        result.nodes.findAll { it.sources }*.name == ['Introduction', 'Introduction (Part B)', 'Introduction (Part C)']
        result.nodes*.name.toSet().size() == result.nodes.size()
    }

    def "a heading whose name already is a topic of the course joins that topic"() {
        when:
        def result = TopicTreeSuggester.suggest('m1', sections('Networks', 'Networks > HTTP'), ['HTTP': 7])
        def byName = result.nodes.collectEntries { [(it.name): it] }

        then:
        byName['HTTP'].existingTopicId == 7
        byName['Networks'].existingTopicId == null
    }

    def "a repeated heading that gets another name does not join the topic of the first one"() {
        when:
        def result = TopicTreeSuggester.suggest('m1',
                sections('Part A > Introduction', 'Part B > Introduction'), ['Introduction': 3])
        def byName = result.nodes.collectEntries { [(it.name): it] }

        then:
        byName['Introduction'].existingTopicId == 3
        byName['Introduction (Part B)'].existingTopicId == null
    }

    def "a document without sections proposes nothing"() {
        expect:
        TopicTreeSuggester.suggest('m1', [], [:]).nodes.isEmpty()
    }
}
