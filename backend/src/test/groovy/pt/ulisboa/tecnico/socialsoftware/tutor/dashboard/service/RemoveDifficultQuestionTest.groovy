package pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest

@DataJpaTest
class RemoveDifficultQuestionTest extends SpockTest {

    def setup() {
        // Create course execution
        // Create a question
        // Create a difficult question
    }

    def "student removes a difficult question from the dashboard"() {
        // Check the database after student removal
        expect: true
    }

    def "student removes a difficult question and one week later it is appears again"() {
        // Student removes difficult question
        // Advance time and check the database
        expect: true
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}