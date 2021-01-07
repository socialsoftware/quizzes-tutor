package pt.ulisboa.tecnico.socialsoftware.tutor.question.domain

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration;
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import spock.lang.Unroll;

@DataJpaTest
class MultipleChoiceQuestionTest extends SpockTest {

    @Unroll
    def "convertSequenceToLetter static method performs correctly"() {

        when:
        def code = MultipleChoiceQuestion.convertSequenceToLetter(sequenceNumber)

        then: 'the return statement contains one quiz'
        result == code

        where:
        sequenceNumber  | result
        0               | "A"
        3               | "D"
        10              | "K"
        null            | "-"
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration { }
}
