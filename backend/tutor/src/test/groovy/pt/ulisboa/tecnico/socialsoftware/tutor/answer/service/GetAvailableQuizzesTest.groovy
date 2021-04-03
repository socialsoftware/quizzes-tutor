package pt.ulisboa.tecnico.socialsoftware.tutor.answer.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.dtos.quiz.QuizType
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.domain.AuthUser
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.User
import spock.lang.Unroll

@DataJpaTest
class GetAvailableQuizzesTest extends SpockTest {
    def user
    def courseDto
    def quiz

    def setup() {
        createExternalCourseAndExecution()

        courseDto = externalCourseExecution.getDto()

        user = new User(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL, User.Role.STUDENT, false, AuthUser.Type.TECNICO)
        user.addCourse(externalCourseExecution)
        userRepository.save(user)
    }

    @Unroll
    def "returns available quiz with no quiz answer and with: quizType=#quizType | OneWay=#oneWay | qRCodeOnly=#qRCodeOnly | availableDate=#availableDate | conclusionDate=#conclusionDate | resultsDate=#resultsDate"() {
        given: 'a quiz'
        quiz = new Quiz()
        quiz.setKey(1)
        quiz.setTitle(QUIZ_TITLE)
        quiz.setCourseExecution(externalCourseExecution)
        quiz.setOneWay(oneWay)
        quiz.setQrCodeOnly(qRCodeOnly)
        quiz.setType(quizType.toString())
        quiz.setAvailableDate(availableDate)
        quiz.setConclusionDate(conclusionDate)
        quiz.setResultsDate(resultsDate)
        quizRepository.save(quiz)

        when:
        def quizDtos = answerService.getAvailableQuizzes(user.getId(), externalCourseExecution.getId())

        then: 'the return statement contains one quiz'
        quizDtos.size() == 1
        def quizDto = quizDtos.get(0)
        quizDto.getId() != null
        quizDto.getTitle() == QUIZ_TITLE
        quizDto.isOneWay() == oneWay
        quizDto.isQrCodeOnly() == qRCodeOnly
        quizDto.getQuestions().size() == 0

        where:
        quizType                | oneWay | qRCodeOnly | availableDate     | conclusionDate      | resultsDate
        QuizType.PROPOSED  | true   | false      | LOCAL_DATE_BEFORE | null                | null
        QuizType.PROPOSED  | false  | false      | LOCAL_DATE_BEFORE | null                | null
        QuizType.IN_CLASS  | true   | false      | LOCAL_DATE_BEFORE | LOCAL_DATE_TOMORROW | null
        QuizType.IN_CLASS  | true   | false      | LOCAL_DATE_BEFORE | LOCAL_DATE_TOMORROW | LOCAL_DATE_LATER
        QuizType.IN_CLASS  | false  | false      | LOCAL_DATE_BEFORE | LOCAL_DATE_TOMORROW | null
        QuizType.IN_CLASS  | false  | false      | LOCAL_DATE_BEFORE | LOCAL_DATE_TOMORROW | LOCAL_DATE_LATER
    }

    @Unroll
    def "does not return quiz with no quiz answer and with: quizType=#quizType | OneWay=#oneWay | qRCodeOnly=#qRCodeOnly | availableDate=#availableDate | conclusionDate=#conclusionDate | resultsDate=#resultsDate"() {
        quiz = new Quiz()
        quiz.setKey(1)
        quiz.setTitle(QUIZ_TITLE)
        quiz.setCourseExecution(externalCourseExecution)
        quiz.setOneWay(oneWay)
        quiz.setQrCodeOnly(qRCodeOnly)
        quiz.setType(quizType.toString())
        quiz.setAvailableDate(availableDate)
        quiz.setConclusionDate(conclusionDate)
        quiz.setResultsDate(resultsDate)
        quizRepository.save(quiz)

        when:
        def quizDtos = answerService.getAvailableQuizzes(user.getId(), externalCourseExecution.getId())

        then: 'no quiz is returned'
        quizDtos.size() == 0

        where:
        quizType                | oneWay | qRCodeOnly | availableDate         | conclusionDate       | resultsDate
        QuizType.PROPOSED  | false  | false      | LOCAL_DATE_TOMORROW   | null                 | null
        QuizType.PROPOSED  | false  | true       | LOCAL_DATE_YESTERDAY  | null                 | null
        QuizType.PROPOSED  | true   | false      | LOCAL_DATE_TOMORROW   | null                 | null
        QuizType.PROPOSED  | true   | true       | LOCAL_DATE_YESTERDAY  | null                 | null
        QuizType.IN_CLASS  | false  | false      | LOCAL_DATE_TOMORROW   | LOCAL_DATE_LATER     | null
        QuizType.IN_CLASS  | false  | false      | LOCAL_DATE_BEFORE     | LOCAL_DATE_YESTERDAY | null
        QuizType.IN_CLASS  | false  | false      | LOCAL_DATE_BEFORE     | LOCAL_DATE_YESTERDAY | LOCAL_DATE_LATER
        QuizType.IN_CLASS  | false  | true       | LOCAL_DATE_YESTERDAY  | LOCAL_DATE_TOMORROW  | null
        QuizType.IN_CLASS  | true   | false      | LOCAL_DATE_TOMORROW   | LOCAL_DATE_LATER     | null
        QuizType.IN_CLASS  | true   | false      | LOCAL_DATE_BEFORE     | LOCAL_DATE_YESTERDAY | null
        QuizType.IN_CLASS  | true   | false      | LOCAL_DATE_BEFORE     | LOCAL_DATE_YESTERDAY | LOCAL_DATE_LATER
        QuizType.IN_CLASS  | true   | true       | LOCAL_DATE_YESTERDAY  | LOCAL_DATE_TOMORROW  | null
    }

    @Unroll
    def "returns available quiz with quiz answer and with: quizType=#quizType | OneWay=#oneWay | qRCodeOnly=#qRCodeOnly | availableDate=#availableDate | conclusionDate=#conclusionDate | resultsDate=#resultsDate | creationDate=#creationDate"() {
        given: 'a quiz'
        quiz = new Quiz()
        quiz.setKey(1)
        quiz.setTitle(QUIZ_TITLE)
        quiz.setCourseExecution(externalCourseExecution)
        quiz.setOneWay(oneWay)
        quiz.setQrCodeOnly(qRCodeOnly)
        quiz.setType(quizType.toString())
        quiz.setAvailableDate(availableDate)
        quiz.setConclusionDate(conclusionDate)
        quiz.setResultsDate(resultsDate)
        quizRepository.save(quiz)
        def quizAnswer = new QuizAnswer(user, quiz)
        quizAnswer.setCompleted(false)
        quizAnswer.setCreationDate(creationDate)
        quizAnswerRepository.save(quizAnswer)

        when:
        def quizDtos = answerService.getAvailableQuizzes(user.getId(), externalCourseExecution.getId())

        then: 'the return statement contains one quiz'
        quizDtos.size() == 1
        def quizDto = quizDtos.get(0)
        quizDto.getId() != null
        quizDto.getTitle() == QUIZ_TITLE
        quizDto.isOneWay() == oneWay
        quizDto.isQrCodeOnly() == qRCodeOnly
        quizDto.getQuestions().size() == 0

        where:
        quizType                | oneWay | qRCodeOnly | availableDate     | conclusionDate      | resultsDate      | creationDate
        QuizType.PROPOSED  | true   | false      | LOCAL_DATE_BEFORE | null                | null             | null
        QuizType.PROPOSED  | false  | false      | LOCAL_DATE_BEFORE | null                | null             | null
        QuizType.IN_CLASS  | true   | false      | LOCAL_DATE_BEFORE | LOCAL_DATE_TOMORROW | null             | null
        QuizType.IN_CLASS  | true   | false      | LOCAL_DATE_BEFORE | LOCAL_DATE_TOMORROW | LOCAL_DATE_LATER | null
        QuizType.IN_CLASS  | false  | false      | LOCAL_DATE_BEFORE | LOCAL_DATE_TOMORROW | null             | null
        QuizType.IN_CLASS  | false  | false      | LOCAL_DATE_BEFORE | LOCAL_DATE_TOMORROW | LOCAL_DATE_LATER | null
        QuizType.GENERATED | false  | false      | LOCAL_DATE_BEFORE | null                | null             | LOCAL_DATE_YESTERDAY
    }

    @Unroll
    def "does not return quiz with quiz answer and with: quizType=#quizType | OneWay=#oneWay | qRCodeOnly=#qRCodeOnly | availableDate=#availableDate | conclusionDate=#conclusionDate | resultsDate=#resultsDate | creationDate=#creationDate | completed=#completed"() {
        quiz = new Quiz()
        quiz.setKey(1)
        quiz.setTitle(QUIZ_TITLE)
        quiz.setCourseExecution(externalCourseExecution)
        quiz.setOneWay(oneWay)
        quiz.setQrCodeOnly(qRCodeOnly)
        quiz.setType(quizType.toString())
        quiz.setAvailableDate(availableDate)
        quiz.setConclusionDate(conclusionDate)
        quiz.setResultsDate(resultsDate)
        quizRepository.save(quiz)
        def quizAnswer = new QuizAnswer(user, quiz)
        quizAnswer.setCompleted(completed)
        quizAnswer.setCreationDate(creationDate)
        quizAnswerRepository.save(quizAnswer)
        when:
        def quizDtos = answerService.getAvailableQuizzes(user.getId(), externalCourseExecution.getId())

        then: 'no quiz is returned'
        quizDtos.size() == 0

        where:
        quizType                 | oneWay | qRCodeOnly | availableDate         | conclusionDate       | resultsDate      | creationDate         | completed
        QuizType.PROPOSED   | false  | true       | LOCAL_DATE_BEFORE     | null                 | null             | null                 | false
        QuizType.PROPOSED   | false  | true       | LOCAL_DATE_BEFORE     | null                 | null             | LOCAL_DATE_YESTERDAY | false
        QuizType.PROPOSED   | false  | false      | LOCAL_DATE_BEFORE     | null                 | null             | LOCAL_DATE_YESTERDAY | true
        QuizType.PROPOSED   | false  | true       | LOCAL_DATE_YESTERDAY  | null                 | null             | LOCAL_DATE_YESTERDAY | true
        QuizType.PROPOSED   | true   | false      | LOCAL_DATE_YESTERDAY  | null                 | null             | LOCAL_DATE_YESTERDAY | false
        QuizType.PROPOSED   | true   | false      | LOCAL_DATE_YESTERDAY  | null                 | null             | LOCAL_DATE_YESTERDAY | true
        QuizType.PROPOSED   | true   | true       | LOCAL_DATE_YESTERDAY  | null                 | null             | LOCAL_DATE_YESTERDAY | false
        QuizType.PROPOSED   | true   | true       | LOCAL_DATE_YESTERDAY  | null                 | null             | LOCAL_DATE_YESTERDAY | true
        QuizType.IN_CLASS   | true   | true       | LOCAL_DATE_BEFORE     | LOCAL_DATE_TOMORROW  | null             | null                 | false
        QuizType.IN_CLASS   | true   | true       | LOCAL_DATE_BEFORE     | LOCAL_DATE_TOMORROW  | LOCAL_DATE_LATER | null                 | false
        QuizType.IN_CLASS   | false  | true       | LOCAL_DATE_BEFORE     | LOCAL_DATE_TOMORROW  | LOCAL_DATE_LATER | LOCAL_DATE_YESTERDAY | false
        QuizType.IN_CLASS   | false  | false      | LOCAL_DATE_YESTERDAY  | LOCAL_DATE_LATER     | null             | LOCAL_DATE_YESTERDAY | true
        QuizType.IN_CLASS   | false  | false      | LOCAL_DATE_BEFORE     | LOCAL_DATE_TOMORROW  | null             | LOCAL_DATE_YESTERDAY | true
        QuizType.IN_CLASS   | false  | false      | LOCAL_DATE_BEFORE     | LOCAL_DATE_TOMORROW  | LOCAL_DATE_LATER | LOCAL_DATE_YESTERDAY | true
        QuizType.IN_CLASS   | false  | true       | LOCAL_DATE_YESTERDAY  | LOCAL_DATE_TOMORROW  | null             | LOCAL_DATE_YESTERDAY | true
        QuizType.IN_CLASS   | true   | false      | LOCAL_DATE_YESTERDAY  | LOCAL_DATE_LATER     | null             | LOCAL_DATE_YESTERDAY | true
        QuizType.IN_CLASS   | true   | false      | LOCAL_DATE_YESTERDAY  | LOCAL_DATE_LATER     | null             | LOCAL_DATE_YESTERDAY | false
        QuizType.IN_CLASS   | true   | false      | LOCAL_DATE_BEFORE     | LOCAL_DATE_TOMORROW  | null             | LOCAL_DATE_YESTERDAY | true
        QuizType.IN_CLASS   | true   | false      | LOCAL_DATE_BEFORE     | LOCAL_DATE_TOMORROW  | null             | LOCAL_DATE_YESTERDAY | false
        QuizType.IN_CLASS   | true   | false      | LOCAL_DATE_BEFORE     | LOCAL_DATE_TOMORROW  | LOCAL_DATE_LATER | LOCAL_DATE_YESTERDAY | true
        QuizType.IN_CLASS   | true   | false      | LOCAL_DATE_BEFORE     | LOCAL_DATE_TOMORROW  | LOCAL_DATE_LATER | LOCAL_DATE_YESTERDAY | false
        QuizType.IN_CLASS   | true   | true       | LOCAL_DATE_YESTERDAY  | LOCAL_DATE_TOMORROW  | null             | LOCAL_DATE_YESTERDAY | true
        QuizType.IN_CLASS   | true   | true       | LOCAL_DATE_BEFORE     | LOCAL_DATE_TOMORROW  | null             | LOCAL_DATE_YESTERDAY | false
        QuizType.GENERATED  | false  | false      | LOCAL_DATE_YESTERDAY  | null                 | null             | LOCAL_DATE_TODAY     | true
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}
