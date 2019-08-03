package pt.ulisboa.tecnico.socialsoftware.tutor.quiz.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.dto.QuizDto
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository.QuizRepository
import spock.lang.Specification

import java.time.LocalDateTime

@DataJpaTest
class CreateQuizServiceSpockTest extends Specification {
    public static final String QUESTION_CONTENT = 'question content'
    public static final String QUIZ_TITLE = 'quiz title'
    public static final String VERSION = 'B'

    @Autowired
    QuizService quizService

    @Autowired
    QuizRepository quizRepository

    def "create a quiz"() {
        given: 'createQuestion a quiz'
        def quiz = new QuizDto()
        quiz.setTitle(QUIZ_TITLE)
        def date =LocalDateTime.now()
        quiz.setDate(date)
        quiz.setYear(2019)
        quiz.setType(Quiz.QuizType.EXAM.name())
        quiz.setSeries(1)
        quiz.setVersion(VERSION)

        when:
        quizService.create(quiz)

        then: "the correct quiz is inside the repository"
        quizRepository.count() == 1L
        def result = quizRepository.findAll().get(0)
        result.getId() != null
        result.getTitle() == QUIZ_TITLE
        result.getDate() == date
        result.getYear() == 2019
        result.getType() == Quiz.QuizType.EXAM.name()
        result.getSeries() == 1
        result.getVersion() == VERSION
        result.getQuizQuestions().size() == 0

    }

    @TestConfiguration
    static class QuizServiceImplTestContextConfiguration {

        @Bean
        QuizService quizService() {
            return new QuizService()
        }
    }

}
