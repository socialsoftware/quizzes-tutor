package pt.ulisboa.tecnico.socialsoftware.tutor.questiongeneration.service

import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.domain.AuthUser
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.TopicDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.TopicSourceDto
import pt.ulisboa.tecnico.socialsoftware.tutor.questiongeneration.dto.AqgJobDto
import pt.ulisboa.tecnico.socialsoftware.tutor.questiongeneration.dto.AqgMaterialDto
import pt.ulisboa.tecnico.socialsoftware.tutor.questiongeneration.dto.AqgSectionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.questiongeneration.dto.GenerationRequestDto
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.Teacher

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*

@DataJpaTest
class TopicGenerationTest extends SpockTest {
    def teacher

    def setup() {
        aqgClient.reset()
        createExternalCourseAndExecution()

        teacher = new Teacher(USER_2_NAME, USER_2_USERNAME, USER_2_EMAIL, false, AuthUser.Type.TECNICO)
        userRepository.save(teacher)
        aqgClient.generateReply = new AqgJobDto('aqg-1', 'PENDING', 'mcq-v2', 'model', 'STRICT', [], null)
    }

    def topic(String name, Integer parentId = null, List<TopicSourceDto> sources = []) {
        def dto = new TopicDto()
        dto.setName(name)
        dto.setParentId(parentId)
        def created = topicService.createTopic(externalCourse.getId(), dto)
        if (sources) topicService.updateTopicSources(created.getId(), sources)
        return created
    }

    def fromTopic(Integer topicId, Map overrides = [:]) {
        def dto = new GenerationRequestDto()
        dto.setTopicId(topicId)
        dto.setFromTopic(overrides.containsKey('fromTopic') ? overrides.fromTopic : true)
        dto.setCount(1)
        dto.setMaterialIds(overrides.materialIds ?: [])
        dto.setSections(overrides.sections ?: [])
        return dto
    }

    def generate(GenerationRequestDto dto) {
        return questionGenerationService.requestGeneration(externalCourseExecution.getId(), teacher.getId(), dto)
    }

    def "the sections of a topic go to the service exactly, grouped by material"() {
        given:
        def networks = topic('Networks', null, [new TopicSourceDto('m1', 'Networks > HTTP'),
                                                new TopicSourceDto('m2', 'Intro'),
                                                new TopicSourceDto('m1', 'Networks > DNS')])

        when:
        generate(fromTopic(networks.getId()))

        then:
        def sent = aqgClient.lastGenerateRequest
        sent.topic() == 'Networks'
        sent.materialIds() == ['m1', 'm2']
        sent.sections() == []
        sent.materialSections() == [m1: ['Networks > HTTP', 'Networks > DNS'], m2: ['Intro']]
    }

    def "the sections of its subtopics are used too"() {
        given:
        def chapter = topic('Chapter', null, [new TopicSourceDto('m1', 'Chapter')])
        def section = topic('Section', chapter.getId(), [new TopicSourceDto('m1', 'Chapter > Section')])
        topic('Elsewhere', null, [new TopicSourceDto('m1', 'Other')])

        when:
        generate(fromTopic(chapter.getId()))

        then:
        aqgClient.lastGenerateRequest.materialSections() == [m1: ['Chapter', 'Chapter > Section']]
    }

    def "the sections chosen from a topic are remembered with the job"() {
        given:
        def networks = topic('Networks', null, [new TopicSourceDto('m1', 'Networks > HTTP')])
        def job = generate(fromTopic(networks.getId()))

        expect:
        generationJobRepository.findById(job.getId()).get().getMaterialSections() == [m1: ['Networks > HTTP']]
    }

    def "a regeneration reads the same sections of the topic"() {
        given:
        def networks = topic('Networks', null, [new TopicSourceDto('m1', 'Networks > HTTP')])
        def job = generate(fromTopic(networks.getId()))
        def draft = new AqgJobDto.Question('What does 404 mean?', [
                new AqgJobDto.Option('Resource not found', true),
                new AqgJobDto.Option('Server error', false),
                new AqgJobDto.Option('Unauthorized', false),
                new AqgJobDto.Option('Redirect', false)], 'It is the missing resource code')
        aqgClient.jobReply = new AqgJobDto('aqg-1', 'DONE', 'mcq-v2', 'model', 'STRICT',
                [new AqgJobDto.Outcome('OK', draft, 0, [], ['m1:0'])], null)
        questionGenerationService.getGenerationJob(externalCourseExecution.getId(), job.getId())
        def generation = questionGenerationRepository.findAll().get(0)
        aqgClient.generateReply = new AqgJobDto('aqg-2', 'PENDING', 'mcq-v2', 'model', 'STRICT', [], null)

        when:
        questionGenerationService.regenerate(generation.getId(), teacher.getId(), 'Make it harder')

        then:
        aqgClient.lastGenerateRequest.materialSections() == [m1: ['Networks > HTTP']]
        aqgClient.lastGenerateRequest.materialIds() == ['m1']
    }

    def "a topic without sections cannot be generated from"() {
        given:
        def empty = topic('Empty')

        when:
        generate(fromTopic(empty.getId()))

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == GENERATION_TOPIC_WITHOUT_SOURCES
    }

    def "asking for the topic's sections needs a topic"() {
        when:
        def dto = fromTopic(null)
        dto.setTopic('HTTP')
        generate(dto)

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == GENERATION_MISSING_TOPIC
    }

    def "choosing materials and sections by hand still works and sends no per-material sections"() {
        given:
        def networks = topic('Networks', null, [new TopicSourceDto('m1', 'Networks > HTTP')])

        when:
        generate(fromTopic(networks.getId(), [fromTopic: false, materialIds: ['m9'], sections: ['Chapter']]))

        then:
        def sent = aqgClient.lastGenerateRequest
        sent.materialIds() == ['m9']
        sent.sections() == ['Chapter']
        sent.materialSections() == [:]
    }

    def "a topic with sections is not used unless it is asked for"() {
        given:
        def networks = topic('Networks', null, [new TopicSourceDto('m1', 'Networks > HTTP')])

        when:
        generate(fromTopic(networks.getId(), [fromTopic: false]))

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == GENERATION_MISSING_MATERIALS
    }

    def "a document's sections become a proposal of topics"() {
        given:
        aqgClient.materials = [new AqgMaterialDto('m1', externalCourse.getId(), 'book.pdf', 'READY', 3, 'pymupdf4llm', 1.0, null)]
        aqgClient.sections = [new AqgSectionDto('Networks', 'Networks', 1),
                              new AqgSectionDto('Networks > HTTP', 'HTTP', 2)]
        def existing = topic('HTTP')

        when:
        def result = questionGenerationService.suggestTopicTree(externalCourseExecution.getId(), 'm1')

        then:
        result.nodes*.name == ['Networks', 'HTTP']
        result.nodes[1].parentKey == 'Networks'
        result.nodes[1].existingTopicId == existing.getId()
        result.nodes[0].sources*.materialId == ['m1']
        topicRepository.count() == 1L
    }

    def "a proposal is only made for a material of the course"() {
        when:
        questionGenerationService.suggestTopicTree(externalCourseExecution.getId(), 'not-mine')

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == GENERATION_MATERIAL_NOT_FOUND
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}
