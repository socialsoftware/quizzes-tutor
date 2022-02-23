package pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.service

import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import spock.lang.Unroll

@DataJpaTest
class UpdateFailedAnswersTest extends SpockTest {

    def 'update a failed answer' () {
        given:
        // student incorrectly answers a question in a quiz and invokes the failed answer service to update the failed answers
        expect: true
    }

    def 'update an unanswered failed answer' () {
        given:
        // student does not answer a question in a quiz and invokes the failed answer service to update the failed answers
        expect: true
    }

    def 'update two failed answers from different quizzes' () {
        given:
        // student incorrectly answers a question in two quizzes and invokes the failed answer service to update the failed answers
        expect: true
    }

    def 'update two failed answers from different quizzes but regarding the same question' () {
        given:
        // student incorrectly answers the same question in two quizzes and invokes the failed answer service to update the failed answers
        expect: true
    }

    def 'update empty failed answers for an answer that does not belong to a completed quiz' () {
        given:
        // student answers a question but does not conclude quiz and invokes the failed answer service to update the failed answers
        expect: true
    }

    def 'update empty failed answers for a correct completed quiz' () {
        given:
        // student does answers correctly to a quiz and invokes the failed answer service to update the failed answers
        expect: true
    }

    @Unroll
    def "cannot create a failed answer for invalid course (#nonExistentId) | invalid student (#studentId) "() {
        // test with invalid/null studentId and invalid/null courseExecutionId.
        // invalid course execution includes a valid course where the student is not enrolled
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}