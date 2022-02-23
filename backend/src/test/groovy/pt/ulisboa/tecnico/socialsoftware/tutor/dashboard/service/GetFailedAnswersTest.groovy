package pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.service

import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import spock.lang.Unroll

@DataJpaTest
class GetFailedAnswersTest extends SpockTest {

    def 'get failed answers' () {
        given:
        // student incorrectly answers a question and does answer another question in a quiz and invokes the failed answer service to get both answers
        // (the failed answers are updated previously)
        expect: true
    }

    def 'get empty failed answers for an answer that does not belong to a completed quiz' () {
        given:
        // student answers a question but does not conclude quiz and invokes the failed answer service to get the failed answers
        expect: true
    }

    def 'get empty failed answers for a correct quiz' () {
        given:
        // student does answers correctly to a quiz and invokes the failed answer service to get the failed answers
        expect: true
    }

    @Unroll
    def "cannot get failed answers for invalid course (#nonExistentId) | invalid student (#studentId) "() {
        // test with invalid/null studentId and invalid/null courseExecutionId
        // invalid course execution includes a valid course where the student is not enrolled
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}