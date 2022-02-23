package pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest

@DataJpaTest
class GetDifficultQuestionTest extends SpockTest {

    def setup() {
        // Create course execution
        // Create two questions: one difficult and other not difficult
    }

    def "get difficult question"() {
        // Check if only one question is in the database (verify calculation difficulty calculation)
        expect: true
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}