package pt.ulisboa.tecnico.socialsoftware.tutor.questiongeneration.service

import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.questiongeneration.domain.GenerationJob
import pt.ulisboa.tecnico.socialsoftware.tutor.utils.DateHandler
import spock.lang.Unroll

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.GENERATION_USAGE_DAYS

@DataJpaTest
class GenerationUsageTest extends SpockTest {
    def setup() {
        createExternalCourseAndExecution()
    }

    def jobsAt(Integer executionId, int daysAgo, int count, int questions = 3) {
        count.times {
            def job = new GenerationJob("aqg-${System.nanoTime()}-${it}".toString(), executionId, 1, 'HTTP', [1], questions, 'EASY', 'STRICT', 'PENDING')
            job.@creationDate = DateHandler.now().minusDays(daysAgo)
            generationJobRepository.save(job)
        }
    }

    def "counts the requests and the questions asked for each day, oldest first, today included"() {
        given:
        jobsAt(externalCourseExecution.getId(), 0, 2, 5)
        jobsAt(externalCourseExecution.getId(), 1, 1, 4)
        jobsAt(externalCourseExecution.getId(), 3, 1, 1)

        when:
        def usage = questionGenerationService.getUsage(4)

        then:
        usage.days().size() == 4
        usage.days().last() == DateHandler.now().toLocalDate().toString()
        usage.courses().size() == 1
        def row = usage.courses()[0]
        row.courseExecutionId() == externalCourseExecution.getId()
        row.course() == externalCourse.getName()
        row.requests() == [1, 0, 1, 2]
        row.questions() == [1, 0, 4, 10]
        row.totalRequests() == 4
        row.totalQuestions() == 15
    }

    def "requests older than the days asked for are left out, and so are courses with none"() {
        given:
        jobsAt(externalCourseExecution.getId(), 0, 1)
        jobsAt(externalCourseExecution.getId() + 1000, 20, 3)

        when:
        def usage = questionGenerationService.getUsage(10)

        then:
        usage.days().size() == 10
        usage.courses()*.totalRequests() == [1]
    }

    def "the busiest courses come first"() {
        given:
        jobsAt(externalCourseExecution.getId(), 0, 1)
        jobsAt(externalCourseExecution.getId() + 1000, 0, 3)

        when:
        def usage = questionGenerationService.getUsage(10)

        then:
        usage.courses()*.totalRequests() == [3, 1]
        usage.courses()[0].course().startsWith('(deleted course')
    }

    @Unroll
    def "#days days is not a period that can be asked for"() {
        when:
        questionGenerationService.getUsage(days)

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == GENERATION_USAGE_DAYS

        where:
        days << [0, -1, 91]
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}
