package pt.ulisboa.tecnico.socialsoftware.tutor.question.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Option
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository
import spock.lang.Specification

@DataJpaTest
class CreateQuestionServiceMethodSpockTest extends Specification {

    @Autowired
    TestEntityManager entityManager

    @Autowired
    QuestionRepository questionRepository

    def "create a question with no image and one option"() {
        given: "create a question"
        def question = new Question()
        question.setContent('question content')
        question.setActive(true)
        question.setDifficulty(1)
        def option = new Option()
        option.setContent("option content")
        option.setCorrect(true)
        option.setQuestion(question)

        entityManager.persist(question)

        expect: "the correct question is inside the repository"
        questionRepository.count() == 1L
    }

}
