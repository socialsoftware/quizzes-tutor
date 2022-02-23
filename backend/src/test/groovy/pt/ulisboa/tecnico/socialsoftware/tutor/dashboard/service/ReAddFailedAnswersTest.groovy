package pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.service

import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import spock.lang.Unroll

@DataJpaTest
class ReAddFailedAnswersTest extends SpockTest {

    def 're-add a removed failed answer from a valid time period' () {
        given:
        // student incorrectly answers a question in a quiz and invokes the failed answer service to re-add the failed answers in that period of time
        // (the failed answers are updated and removed previously)
        expect: true
    }

    def 're-adds failed answers from other time period' () {
        given:
        // student incorrectly answers a question in a quiz and invokes the failed answer service to re-add the failed answers in other period of time
        // (the failed answers are updated and removed previously)
        expect: true
    }

    @Unroll
    def "cannot re-add failed answers for invalid course (#nonExistentId) | invalid student (#studentId) | invalid period (#startDate, #endDate) "() {
        // test with invalid/null studentId, invalid/null courseExecutionId and invalid/null startDate, endDate
        // invalid course execution includes a valid course where the student is not enrolled
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}
