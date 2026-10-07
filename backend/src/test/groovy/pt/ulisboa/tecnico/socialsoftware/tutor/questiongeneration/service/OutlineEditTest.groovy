package pt.ulisboa.tecnico.socialsoftware.tutor.questiongeneration.service

import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.TopicDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.TopicSourceDto
import pt.ulisboa.tecnico.socialsoftware.tutor.questiongeneration.dto.AqgMaterialDto
import pt.ulisboa.tecnico.socialsoftware.tutor.questiongeneration.dto.AqgOutlineEditDto
import pt.ulisboa.tecnico.socialsoftware.tutor.questiongeneration.dto.AqgOutlineEditResultDto

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.GENERATION_MATERIAL_NOT_FOUND

@DataJpaTest
class OutlineEditTest extends SpockTest {
    def setup() {
        aqgClient.reset()
        createExternalCourseAndExecution()
        aqgClient.materials = [new AqgMaterialDto('m1', externalCourse.getId(), 'a.pdf', 'READY', 3, 'pymupdf4llm', 1.0, null)]
    }

    def topic(String name, List<TopicSourceDto> sources) {
        def dto = new TopicDto()
        dto.setName(name)
        def created = topicService.createTopic(externalCourse.getId(), dto)
        topicService.updateTopicSources(created.getId(), sources)
        return created
    }

    def pathsOf(Integer topicId) {
        return topicRepository.findById(topicId).get().getSources().collect { "${it.getMaterialId()}:${it.getSectionPath()}".toString() }.sort()
    }

    def edit(Map pathMap, String op = 'rename', String path = 'x') {
        aqgClient.editReply = new AqgOutlineEditResultDto(pathMap, [])
        return questionGenerationService.editOutline(externalCourseExecution.getId(), 'm1',
                new AqgOutlineEditDto(op, path, null, null, null))
    }

    def "a renamed section keeps its topics"() {
        given:
        def networks = topic('Networks', [new TopicSourceDto('m1', 'Networks > DNS'), new TopicSourceDto('m1', 'Other')])

        when:
        edit(['Networks > DNS': ['Networks > Name service']])

        then:
        pathsOf(networks.getId()) == ['m1:Networks > Name service', 'm1:Other']
    }

    def "a section that was cut stays linked to both halves"() {
        given:
        def networks = topic('Networks', [new TopicSourceDto('m1', 'Networks > HTTP')])

        when:
        edit(['Networks > HTTP': ['Networks > HTTP', 'Networks > Cookies']], 'split')

        then:
        pathsOf(networks.getId()) == ['m1:Networks > Cookies', 'm1:Networks > HTTP']
    }

    def "sections merged into the same one are linked once"() {
        given:
        def networks = topic('Networks', [new TopicSourceDto('m1', 'A'), new TopicSourceDto('m1', 'B')])

        when:
        edit(['B': ['A']], 'merge')

        then:
        pathsOf(networks.getId()) == ['m1:A']
    }

    def "every topic that used the section follows it"() {
        given:
        def one = topic('One', [new TopicSourceDto('m1', 'Shared')])
        def two = topic('Two', [new TopicSourceDto('m1', 'Shared')])

        when:
        edit(['Shared': ['Renamed']])

        then:
        pathsOf(one.getId()) == ['m1:Renamed']
        pathsOf(two.getId()) == ['m1:Renamed']
    }

    def "links to other documents are left alone"() {
        given:
        def networks = topic('Networks', [new TopicSourceDto('m1', 'Intro'), new TopicSourceDto('m2', 'Intro')])

        when:
        edit(['Intro': ['Start']])

        then:
        pathsOf(networks.getId()) == ['m1:Start', 'm2:Intro']
    }

    def "the edit is sent to the service as the teacher made it"() {
        given:
        topic('Networks', [new TopicSourceDto('m1', 'A')])

        when:
        edit([:], 'shift', 'A > B')

        then:
        aqgClient.lastEdit.op() == 'shift'
        aqgClient.lastEdit.path() == 'A > B'
    }

    def "an edit that changed no path touches no link"() {
        given:
        def networks = topic('Networks', [new TopicSourceDto('m1', 'A')])

        when:
        edit([:])

        then:
        pathsOf(networks.getId()) == ['m1:A']
    }

    def "a document of another course cannot be edited"() {
        when:
        questionGenerationService.editOutline(externalCourseExecution.getId(), 'other',
                new AqgOutlineEditDto('merge', 'A', null, null, null))

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == GENERATION_MATERIAL_NOT_FOUND
        aqgClient.lastEdit == null
    }

    def "the outline and the text of a section come from the service"() {
        given:
        aqgClient.paragraphs = []

        expect:
        questionGenerationService.getOutline(externalCourseExecution.getId(), 'm1') == []
        questionGenerationService.getSectionText(externalCourseExecution.getId(), 'm1', 'A') == []
    }

    def "the outline of a document of another course is refused"() {
        when:
        questionGenerationService.getOutline(externalCourseExecution.getId(), 'other')

        then:
        thrown(TutorException)
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}
