package pt.ulisboa.tecnico.socialsoftware.tutor.quiz.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.QuizDto
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.QuizService
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz
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

    @Autowired
    QuestionRepository questionRepository

    def quiz
    def creationDate
    def availableDate
    def conclusionDate
    def questionDto

    def setup() {
        quiz = new QuizDto()
        quiz.setNumber(1)
        creationDate = LocalDateTime.now()
        availableDate = LocalDateTime.now()
        conclusionDate = LocalDateTime.now().plusDays(1)
        quiz.setScramble(true)
        quiz.setCreationDate(creationDate)
        quiz.setAvailableDate(availableDate)
        quiz.setConclusionDate(conclusionDate)
        quiz.setYear(2019)
        quiz.setSeries(1)
        quiz.setVersion(VERSION)

        def question = new Question()
        question.setNumber(1)
        questionRepository.save(question)

        questionDto = new QuestionDto()
        questionDto.setId(question.getId())
        questionDto.setNumber(1)
        questionDto.setSequence(1)

        def questions = new ArrayList()
        questions.add(questionDto)
        quiz.setQuestions(questions)
    }

    def "create a quiz"() {
        given: 'student quiz with title'
        quiz.setTitle(QUIZ_TITLE)
        quiz.setType(Quiz.QuizType.STUDENT.name())

        when:
        quizService.createQuiz(quiz)

        then: "the correct quiz is inside the repository"
        quizRepository.count() == 1L
        def result = quizRepository.findAll().get(0)
        result.getId() != null
        result.getNumber() != null
        result.getScramble()
        result.getTitle() == QUIZ_TITLE
        result.getCreationDate() == creationDate
        result.getAvailableDate() == availableDate
        result.getConclusionDate() == conclusionDate
        result.getYear() == 2019
        result.getType() == Quiz.QuizType.STUDENT.name()
        result.getSeries() == 1
        result.getVersion() == VERSION
        result.getQuizQuestions().size() == 1
    }

    def "create a quiz no title"() {
        given: 'student quiz'
        quiz.setType(Quiz.QuizType.STUDENT.name())

        when:
        quizService.createQuiz(quiz)

        then:
        def exception = thrown(TutorException)
        exception.getError() == TutorException.ExceptionError.QUIZ_NOT_CONSISTENT
        quizRepository.count() == 0L
    }

    def "create a TEACHER quiz no available date"() {
        given: 'createQuiz a quiz'
        quiz.setTitle(QUIZ_TITLE)
        quiz.setAvailableDate(null);
        quiz.setType(Quiz.QuizType.TEACHER.name())

        when:
        quizService.createQuiz(quiz)

        then:
        def exception = thrown(TutorException)
        exception.getError() == TutorException.ExceptionError.QUIZ_NOT_CONSISTENT
        quizRepository.count() == 0L
    }

    def "create a TEACHER quiz with available date after conclusion"() {
        given: 'createQuiz a quiz'
        quiz.setTitle(QUIZ_TITLE)
        quiz.setConclusionDate(getAvailableDate().minusDays(1))
        quiz.setType(Quiz.QuizType.TEACHER.name())

        when:
        quizService.createQuiz(quiz)

        then:
        def exception = thrown(TutorException)
        exception.getError() == TutorException.ExceptionError.QUIZ_NOT_CONSISTENT
        quizRepository.count() == 0L
    }

    def "create a TEACHER quiz wrong sequence"() {
        given: 'createQuiz a quiz'
        quiz.setTitle(QUIZ_TITLE)
        quiz.setType(Quiz.QuizType.STUDENT.name())
        questionDto.setSequence(3)

        when:
        quizService.createQuiz(quiz)

        then:
        def exception = thrown(TutorException)
        exception.getError() == TutorException.ExceptionError.QUIZ_NOT_CONSISTENT
        quizRepository.count() == 0L
    }


    @TestConfiguration
    static class QuizServiceImplTestContextConfiguration {

        @Bean
        QuizService quizService() {
            return new QuizService()
        }
    }

}
