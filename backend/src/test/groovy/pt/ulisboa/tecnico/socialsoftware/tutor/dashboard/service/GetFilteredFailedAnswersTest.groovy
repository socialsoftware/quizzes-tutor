package pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.service

import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import spock.lang.Unroll

@DataJpaTest
class GetFilteredFailedAnswersTest extends SpockTest {

    def 'get filtered failed answers' () {
        given:
        // student incorrectly answers two questions in a quiz and invokes the failed answer service to get both answers
        // (the failed answers are updated)
        expect: true
    }

    def 'get some filtered failed answers' () {
        given:
        // student incorrectly answers two questions in two different quizzes and invokes the failed answer service to get one of the answers
        // (the failed answers are updated previously)
        expect: true
    }

    def 'get empty filtered failed answers' () {
        given:
        // student incorrectly answers a question in a quiz and invokes the failed answer service to get no answers
        // (the failed answers are updated previously)
        expect: true
    }

    @Unroll
    def "cannot get filtered failed answers for invalid course (#nonExistentId) | invalid student (#studentId) | invalid period (#startDate, #endDate) "() {
        // test with invalid/null studentId, invalid/null courseExecutionId and invalid/null startDate, endDate
        // invalid course execution includes a valid course where the student is not enrolled
    }


    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}
