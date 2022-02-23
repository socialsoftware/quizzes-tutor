package pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.service

import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import spock.lang.Unroll

@DataJpaTest
class RemoveFailedAnswerTest extends SpockTest {

    def 'remove a failed answer' () {
        given:
        // student incorrectly answers a question and invokes the failed answer service to remove the answer
        // (the failed answers are updated previously)
        expect: true
    }

    def 'remove one of two failed answers' () {
        given:
        // student incorrectly answers two questions and invokes the failed answer service to remove one of the answers
        // (the failed answers are updated previously)
        expect: true
    }

    def 'cannot remove failed answers that were not yet updated' () {
        given:
        // student incorrectly answers a question and invokes the failed answer service to remove the failed answer without updating the failed answers
        expect: true
    }

    @Unroll
    def "cannot remove failed answers for invalid failed answer (#failedAnswerId)"() {
        // test with invalid/null failedAnswerId
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}
