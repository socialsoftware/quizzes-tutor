package pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest

@DataJpaTest
class UpdateDifficultQuestionTest extends SpockTest {

    def setup() {
        // Create course execution
        // Create a question
        // Create a difficult question
    }

    def "update difficult question removing difficult status"() {
        // Change difficulty percentage and check the database
        expect: true
    }

    def "update difficult question after one week"() {
        // Advance time and check the database
        expect: true
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}
