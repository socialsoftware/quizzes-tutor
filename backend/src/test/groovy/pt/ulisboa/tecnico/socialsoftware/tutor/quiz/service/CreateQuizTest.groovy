package pt.ulisboa.tecnico.socialsoftware.tutor.quiz.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecutionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.dto.QuizDto
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.QuizService
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository.QuizRepository
import spock.lang.Specification

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@DataJpaTest
class CreateQuizTest extends Specification {
    public static final String COURSE_NAME = "Software Architecture"
    public static final String ACRONYM = "AS1"
    public static final String ACADEMIC_TERM = "1 SEM"
    public static final String QUESTION_CONTENT = 'question content'
    public static final String QUIZ_TITLE = 'quiz title'
    public static final String VERSION = 'B'

    @Autowired
    QuizService quizService

    @Autowired
    CourseRepository courseRepository

    @Autowired
    CourseExecutionRepository courseExecutionRepository

    @Autowired
    QuizRepository quizRepository

    @Autowired
    QuestionRepository questionRepository

    def course
    def courseExecution
    def quiz
    def creationDate
    def availableDate
    def conclusionDate
    def questionDto
    def formatter

    def setup() {
        formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")

        course = new Course(COURSE_NAME, Course.Type.TECNICO)
        courseRepository.save(course)

        courseExecution = new CourseExecution(course, ACRONYM, ACADEMIC_TERM, Course.Type.TECNICO)
        courseExecutionRepository.save(courseExecution)

        quiz = new QuizDto()
        quiz.setKey(1)
        creationDate = LocalDateTime.now()
        availableDate = LocalDateTime.now()
        conclusionDate = LocalDateTime.now().plusDays(1)
        quiz.setScramble(true)
        quiz.setOneWay(true)
        quiz.setQrCodeOnly(true)
        quiz.setAvailableDate(availableDate.format(formatter))
        quiz.setConclusionDate(conclusionDate.format(formatter))
        quiz.setSeries(1)
        quiz.setVersion(VERSION)

        def question = new Question()
        question.setKey(1)
        question.setCourse(course)
        course.addQuestion(question)
        questionRepository.save(question)

        questionDto = new QuestionDto()
        questionDto.setId(question.getId())
        questionDto.setKey(1)
        questionDto.setSequence(1)

        def questions = new ArrayList()
        questions.add(questionDto)
        quiz.setQuestions(questions)
    }

    def "create a quiz"() {
        given: 'student quiz with title'
        quiz.setTitle(QUIZ_TITLE)
        quiz.setType(Quiz.QuizType.GENERATED)

        when:
        quizService.createQuiz(courseExecution.getId(), quiz)

        then: "the correct quiz is inside the repository"
        quizRepository.count() == 1L
        def result = quizRepository.findAll().get(0)
        result.getId() != null
        result.getKey() != null
        result.getScramble()
        result.isOneWay()
        result.isQrCodeOnly()
        result.getTitle() == QUIZ_TITLE
        result.getCreationDate() != null
        result.getAvailableDate().format(formatter) == availableDate.format(formatter)
        result.getConclusionDate().format(formatter) == conclusionDate.format(formatter)
        result.getType() == Quiz.QuizType.GENERATED
        result.getSeries() == 1
        result.getVersion() == VERSION
        result.getQuizQuestions().size() == 1
    }

    def "create a quiz no title"() {
        given: 'student quiz'
        quiz.setType(Quiz.QuizType.GENERATED)

        when:
        quizService.createQuiz(courseExecution.getId(), quiz)

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.QUIZ_NOT_CONSISTENT
        quizRepository.count() == 0L
    }

    def "create a TEACHER quiz no available date"() {
        given: 'createQuiz a quiz'
        quiz.setTitle(QUIZ_TITLE)
        quiz.setAvailableDate(null)
        quiz.setType(Quiz.QuizType.PROPOSED)

        when:
        quizService.createQuiz(courseExecution.getId(), quiz)

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.QUIZ_NOT_CONSISTENT
        quizRepository.count() == 0L
    }

    def "create a TEACHER quiz with available date after conclusion"() {
        given: 'createQuiz a quiz'
        quiz.setTitle(QUIZ_TITLE)
        quiz.setConclusionDate(getAvailableDate().minusDays(1).format(formatter))
        quiz.setType(Quiz.QuizType.PROPOSED)

        when:
        quizService.createQuiz(courseExecution.getId(), quiz)

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.QUIZ_NOT_CONSISTENT
        quizRepository.count() == 0L
    }

    def "create a TEACHER quiz wrong sequence"() {
        given: 'createQuiz a quiz'
        quiz.setTitle(QUIZ_TITLE)
        quiz.setType(Quiz.QuizType.GENERATED)
        questionDto.setSequence(3)

        when:
        quizService.createQuiz(courseExecution.getId(), quiz)

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.QUIZ_NOT_CONSISTENT
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
