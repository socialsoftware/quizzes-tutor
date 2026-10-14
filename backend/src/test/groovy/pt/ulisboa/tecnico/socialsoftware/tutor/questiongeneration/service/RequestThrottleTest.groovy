package pt.ulisboa.tecnico.socialsoftware.tutor.questiongeneration.service

import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.domain.AuthUser
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic
import pt.ulisboa.tecnico.socialsoftware.tutor.questiongeneration.RequestThrottle
import pt.ulisboa.tecnico.socialsoftware.tutor.questiongeneration.domain.GenerationJob
import pt.ulisboa.tecnico.socialsoftware.tutor.questiongeneration.dto.AqgJobDto
import pt.ulisboa.tecnico.socialsoftware.tutor.questiongeneration.dto.GenerationRequestDto
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.Teacher

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*

@DataJpaTest
class RequestThrottleTest extends SpockTest {
    def teacher
    def other
    def topic
    def jobs = 0

    def setup() {
        aqgClient.reset()
        aqgClient.generateReply = new AqgJobDto('aqg-1', 'PENDING', 'mcq-v3', 'model', 'STRICT', [], null)
        createExternalCourseAndExecution()

        teacher = new Teacher(USER_2_NAME, USER_2_USERNAME, USER_2_EMAIL, false, AuthUser.Type.TECNICO)
        userRepository.save(teacher)
        other = new Teacher(USER_3_NAME, USER_3_USERNAME, USER_3_EMAIL, false, AuthUser.Type.TECNICO)
        userRepository.save(other)

        topic = new Topic()
        topic.setName('HTTP')
        topic.setCourse(externalCourse)
        topic.addSource('material-1', 'material-1:0')
        topicRepository.save(topic)
    }

    def cleanup() {
        // the bean outlives the test: back to no limits
        requestThrottle.configure(0, 0, 1000)
    }

    def ask(teacherId) {
        // each job of the service has its own id (aqg_job_id is unique)
        aqgClient.generateReply = new AqgJobDto("aqg-${++jobs}".toString(), 'PENDING', 'mcq-v3', 'model', 'STRICT', [], null)
        def dto = new GenerationRequestDto()
        dto.setTopicId(topic.getId())
        dto.setCount(1)
        return questionGenerationService.requestGeneration(externalCourseExecution.getId(), teacherId, dto)
    }

    // ---------------------------------------------------------------- the throttle by itself

    def "a second request too soon is refused and says how long to wait"() {
        given:
        def throttle = new RequestThrottle(60, 5, 2)
        throttle.checkGeneration(7)
        throttle.recordGeneration(7)

        when:
        throttle.checkGeneration(7)

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == GENERATION_TOO_FAST
        exception.getMessage().matches('.*\\b(5\\d|60) more second.*')
    }

    def "another teacher is not held back, and a request that was not recorded does not make anyone wait"() {
        given:
        def throttle = new RequestThrottle(60, 5, 2)
        throttle.recordGeneration(7)

        when:
        throttle.checkGeneration(8)
        throttle.checkGeneration(9)
        throttle.checkGeneration(9)

        then:
        noExceptionThrown()
    }

    def "suggestions are limited per teacher too"() {
        given:
        def throttle = new RequestThrottle(0, 30, 2)
        throttle.checkAndRecordSuggestion(7)

        when:
        throttle.checkAndRecordSuggestion(7)

        then:
        thrown(TutorException)

        when:
        throttle.checkAndRecordSuggestion(8)

        then:
        noExceptionThrown()
    }

    def "with no pause configured nothing is refused"() {
        given:
        def throttle = new RequestThrottle(0, 0, 2)

        when:
        5.times {
            throttle.checkGeneration(7)
            throttle.recordGeneration(7)
            throttle.checkAndRecordSuggestion(7)
        }

        then:
        noExceptionThrown()
    }

    // ---------------------------------------------------------------- inside the service

    def "a teacher cannot ask again before the pause is over, and the service is not bothered"() {
        given:
        requestThrottle.configure(60, 0, 1000)
        ask(teacher.getId())

        when:
        ask(teacher.getId())

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == GENERATION_TOO_FAST
        generationJobRepository.count() == 1L
    }

    def "the pause is per teacher"() {
        given:
        requestThrottle.configure(60, 0, 1000)
        ask(teacher.getId())

        when:
        ask(other.getId())

        then:
        generationJobRepository.count() == 2L
    }

    def "a request the service refused does not start the pause"() {
        given:
        requestThrottle.configure(60, 0, 1000)
        aqgClient.generateReply = null
        aqgClient.generateFailure = new TutorException(AQG_SERVICE_ERROR, 'The question generation queue is full')

        when:
        ask(teacher.getId())

        then:
        thrown(TutorException)

        when:
        aqgClient.generateFailure = null
        aqgClient.generateReply = new AqgJobDto('aqg-2', 'PENDING', 'mcq-v3', 'model', 'STRICT', [], null)
        ask(teacher.getId())

        then:
        generationJobRepository.count() == 1L
    }

    def "a teacher cannot pile up requests nobody collected"() {
        given:
        requestThrottle.configure(0, 0, 2)
        ask(teacher.getId())
        ask(teacher.getId())

        when:
        ask(teacher.getId())

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == GENERATION_TOO_MANY_PENDING
        generationJobRepository.count() == 2L
    }

    def "collected requests no longer count and other teachers are not affected"() {
        given:
        requestThrottle.configure(0, 0, 2)
        def first = ask(teacher.getId())
        ask(teacher.getId())
        def job = generationJobRepository.findById(first.getId()).get()
        job.markImported(1, 0)

        when:
        ask(teacher.getId())
        ask(other.getId())

        then:
        generationJobRepository.count() == 4L
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}
