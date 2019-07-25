package pt.ulisboa.tecnico.socialsoftware.tutor.quiz.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.dto.QuizDto
import spock.lang.Specification
import spock.lang.Subject

@SpringBootTest
class CreateQuizQuizServiceMethodSpockTest extends Specification {
    @Autowired
    QuizService service


    def 'create quiz' () {
        given:
        def quizDto = new QuizDto()
        quizDto.setType('Title')

        when:
        def quiz = service.create(quizDto)

        then:
        quiz.getSeries() != null
        quiz.getTitle() == quizDto.getTitle()
    }

}
