package pt.ulisboa.tecnico.socialsoftware.tutor.statement.service


import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.config.DateHandler
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseDto
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import spock.lang.Unroll

import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

@DataJpaTest
class GetAvailableQuizzesTest extends SpockTest {
    static final USERNAME = 'username'
    public static final String COURSE_NAME = "Software Architecture"
    public static final String ACRONYM = "AS1"
    public static final String ACADEMIC_TERM = "1 SEM"
    public static final String QUIZ_TITLE = "Quiz title"
    public static final LocalDateTime BEFORE = DateHandler.now().minusDays(2)
    public static final LocalDateTime YESTERDAY = DateHandler.now().minusDays(1)
    public static final LocalDateTime TOMORROW = DateHandler.now().plusDays(1)
    public static final LocalDateTime LATER = DateHandler.now().plusDays(2)

    def user
    def courseDto
    def quiz
    def course
    def courseExecution

    def setup() {
        course = new Course(COURSE_NAME, Course.Type.TECNICO)
        courseRepository.save(course)

        courseExecution = new CourseExecution(course, ACRONYM, ACADEMIC_TERM, Course.Type.TECNICO)
        courseExecutionRepository.save(courseExecution)

        courseDto = new CourseDto(courseExecution)

        user = new User('name', USERNAME, User.Role.STUDENT)
        user.getCourseExecutions().add(courseExecution)
        courseExecution.getUsers().add(user)

        userRepository.save(user)
        user.setKey(user.getId())
    }

    @Unroll
    def "returns available quiz with: quizType=#quizType | conclusionDate=#conclusionDate | resultsDate=#resultsDate"() {
        given: 'a quiz'
        quiz = new Quiz()
        quiz.setKey(1)
        quiz.setTitle(QUIZ_TITLE)
        quiz.setType(quizType.toString())
        quiz.setAvailableDate(BEFORE)
        quiz.setConclusionDate(conclusionDate)
        quiz.setResultsDate(resultsDate)
        quiz.setCourseExecution(courseExecution)
        quiz.setOneWay(true)
        quizRepository.save(quiz)

        when:
        def quizDtos = statementService.getAvailableQuizzes(user.getId(), courseExecution.getId())

        then: 'the return statement contains one quiz'
        quizDtos.size() == 1
        def quizDto = quizDtos.get(0)
        quizDto.getId() != null
        quizDto.getTitle() == QUIZ_TITLE
        quizDto.isOneWay()
        quizDto.getAvailableDate() == DateHandler.toISOString(BEFORE)
        quizDto.getConclusionDate() == DateHandler.toISOString(conclusionDate)
        quizDto.getQuestions().size() == 0

        where:
        quizType                | conclusionDate | resultsDate
        Quiz.QuizType.PROPOSED  | null           | null
        Quiz.QuizType.PROPOSED  | null           | LATER
        Quiz.QuizType.PROPOSED  | TOMORROW       | null
        Quiz.QuizType.PROPOSED  | TOMORROW       | LATER
        Quiz.QuizType.IN_CLASS  | TOMORROW       | null
        Quiz.QuizType.IN_CLASS  | TOMORROW       | LATER
    }

    def 'returns a qrOnly quiz if it was scanned already (if it has a quizAnswer)'() {
        given: 'a quiz'
        quiz = new Quiz()
        quiz.setKey(1)
        quiz.setQrCodeOnly(true)
        quiz.setOneWay(true)
        quiz.setTitle(QUIZ_TITLE)
        quiz.setType(Quiz.QuizType.PROPOSED.toString())
        quiz.setAvailableDate(BEFORE)
        quiz.setConclusionDate(TOMORROW)
        quiz.setCourseExecution(courseExecution)
        quizRepository.save(quiz)
        def quizAnswer = new QuizAnswer(user, quiz)
        quizAnswerRepository.save(quizAnswer)

        when:
        def quizDtos = statementService.getAvailableQuizzes(user.getId(), courseExecution.getId())

        then: 'the return statement contains one quiz'
        quizDtos.size() == 1
        def quizDto = quizDtos.get(0)
        quizDto.getId() != null
        quizDto.getTitle() == QUIZ_TITLE
        quizDto.isOneWay()
        quizDto.getAvailableDate() == DateHandler.toISOString(BEFORE)
        quizDto.getConclusionDate() == DateHandler.toISOString(TOMORROW)
        quizDto.getQuestions().size() == 0
    }

    @Unroll
    def "does not return quiz with: quizType=#quizType | qrOnly=#qrOnly | availableDate=#availableDate | conclusionDate=#conclusionDate"() {
        given: 'a quiz'
        quiz = new Quiz()
        quiz.setKey(1)
        quiz.setTitle(QUIZ_TITLE)
        quiz.setType(quizType.toString())
        quiz.setAvailableDate(availableDate)
        quiz.setConclusionDate(conclusionDate)
        quiz.setCourseExecution(courseExecution)
        quiz.setQrCodeOnly(qrOnly)
        quizRepository.save(quiz)

        when:
        def quizDtos = statementService.getAvailableQuizzes(user.getId(), courseExecution.getId())

        then: 'no quiz is returned'
        quizDtos.size() == 0

        where:
        quizType                | qrOnly | availableDate  | conclusionDate
        Quiz.QuizType.PROPOSED  | true   | YESTERDAY      | TOMORROW
        Quiz.QuizType.IN_CLASS  | true   | YESTERDAY      | TOMORROW
        Quiz.QuizType.PROPOSED  | true   | YESTERDAY      | null
        Quiz.QuizType.PROPOSED  | false  | TOMORROW       | LATER
        Quiz.QuizType.IN_CLASS  | false  | TOMORROW       | LATER
        Quiz.QuizType.PROPOSED  | false  | BEFORE         | YESTERDAY
        Quiz.QuizType.IN_CLASS  | false  | BEFORE         | YESTERDAY
    }

    def 'does not return a completed quiz'() {
        given: 'a completed quiz'
        quiz = new Quiz()
        quiz.setKey(1)
        quiz.setTitle(QUIZ_TITLE)
        quiz.setType(Quiz.QuizType.PROPOSED.toString())
        quiz.setAvailableDate(BEFORE)
        quiz.setConclusionDate(TOMORROW)
        quiz.setCourseExecution(courseExecution)
        quizRepository.save(quiz)
        def quizAnswer = new QuizAnswer(user, quiz)
        quizAnswer.setCompleted(true)
        quizAnswerRepository.save(quizAnswer)

        when:
        def quizDtos = statementService.getAvailableQuizzes(user.getId(), courseExecution.getId())

        then: 'no quiz is returned'
        quizDtos.size() == 0
     }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}
