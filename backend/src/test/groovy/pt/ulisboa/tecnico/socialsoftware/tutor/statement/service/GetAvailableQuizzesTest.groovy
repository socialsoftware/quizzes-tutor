package pt.ulisboa.tecnico.socialsoftware.tutor.statement.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.config.DateHandler
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseDto
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import spock.lang.Unroll

@DataJpaTest
class GetAvailableQuizzesTest extends SpockTest {
    def user
    def courseDto
    def quiz

    def setup() {
        courseDto = new CourseDto(courseExecution)

        user = new User(USER_1_NAME, USER_1_USERNAME, User.Role.STUDENT)
        user.addCourse(courseExecution)
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
        quiz.setAvailableDate(LOCAL_DATE_BEFORE)
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
        quizDto.getAvailableDate() == DateHandler.toISOString(LOCAL_DATE_BEFORE)
        quizDto.getConclusionDate() == DateHandler.toISOString(conclusionDate)
        quizDto.getQuestions().size() == 0

        where:
        quizType                | conclusionDate      | resultsDate
        Quiz.QuizType.PROPOSED  | null                | null
        Quiz.QuizType.PROPOSED  | null                | LOCAL_DATE_LATER
        Quiz.QuizType.PROPOSED  | LOCAL_DATE_TOMORROW | null
        Quiz.QuizType.PROPOSED  | LOCAL_DATE_TOMORROW | LOCAL_DATE_LATER
        Quiz.QuizType.IN_CLASS  | LOCAL_DATE_TOMORROW | null
        Quiz.QuizType.IN_CLASS  | LOCAL_DATE_TOMORROW | LOCAL_DATE_LATER
    }

    def 'returns a qrOnly quiz if it was scanned already (if it has a quizAnswer)'() {
        given: 'a quiz'
        quiz = new Quiz()
        quiz.setKey(1)
        quiz.setQrCodeOnly(true)
        quiz.setOneWay(true)
        quiz.setTitle(QUIZ_TITLE)
        quiz.setType(Quiz.QuizType.PROPOSED.toString())
        quiz.setAvailableDate(LOCAL_DATE_BEFORE)
        quiz.setConclusionDate(LOCAL_DATE_TOMORROW)
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
        quizDto.getAvailableDate() == DateHandler.toISOString(LOCAL_DATE_BEFORE)
        quizDto.getConclusionDate() == DateHandler.toISOString(LOCAL_DATE_TOMORROW)
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
        quizType                | qrOnly | availableDate             | conclusionDate
        Quiz.QuizType.PROPOSED  | true   | LOCAL_DATE_YESTERDAY      | LOCAL_DATE_TOMORROW
        Quiz.QuizType.IN_CLASS  | true   | LOCAL_DATE_YESTERDAY      | LOCAL_DATE_TOMORROW
        Quiz.QuizType.PROPOSED  | true   | LOCAL_DATE_YESTERDAY      | null
        Quiz.QuizType.PROPOSED  | false  | LOCAL_DATE_TOMORROW       | LOCAL_DATE_LATER
        Quiz.QuizType.IN_CLASS  | false  | LOCAL_DATE_TOMORROW       | LOCAL_DATE_LATER
        Quiz.QuizType.PROPOSED  | false  | LOCAL_DATE_BEFORE         | LOCAL_DATE_YESTERDAY
        Quiz.QuizType.IN_CLASS  | false  | LOCAL_DATE_BEFORE         | LOCAL_DATE_YESTERDAY
    }

    def 'does not return a completed quiz'() {
        given: 'a completed quiz'
        quiz = new Quiz()
        quiz.setKey(1)
        quiz.setTitle(QUIZ_TITLE)
        quiz.setType(Quiz.QuizType.PROPOSED.toString())
        quiz.setAvailableDate(LOCAL_DATE_BEFORE)
        quiz.setConclusionDate(LOCAL_DATE_TOMORROW)
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
