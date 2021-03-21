package pt.ulisboa.tecnico.socialsoftware.tutor.answer.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.dto.CourseExecutionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.MultipleChoiceQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.User
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.domain.AuthUser
import spock.lang.Unroll

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.CANNOT_START_QRCODE_QUIZ
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.QUIZ_ALREADY_COMPLETED
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.QUIZ_NOT_YET_AVAILABLE
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.QUIZ_NO_LONGER_AVAILABLE

@DataJpaTest
class StartQuizTest extends SpockTest {
    def user
    def courseDto
    def quiz
    def question

    def setup() {
        createExternalCourseAndExecution()

        courseDto = new CourseExecutionDto(externalCourseExecution)

        user = new User(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL, User.Role.STUDENT, false, AuthUser.Type.TECNICO)
        user.addCourse(externalCourseExecution)
        userRepository.save(user)

        question = new Question()
        question.setKey(1)
        question.setCourse(externalCourse)
        question.setTitle(QUESTION_1_TITLE)
        question.setContent(QUESTION_1_CONTENT)
        def questionDetails = new MultipleChoiceQuestion()
        question.setQuestionDetails(questionDetails)
        questionDetailsRepository.save(questionDetails)
        questionRepository.save(question)
    }

    @Unroll
    def "returns statement and generates quiz answer: quizType=#quizType | OneWay=#oneWay | qRCodeOnly=#qRCodeOnly | availableDate=#availableDate | conclusionDate=#conclusionDate | resultsDate=#resultsDate"() {
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
        and: 'a quiz question'
        def quizQuestion = new QuizQuestion()
        quizQuestion.setSequence(1)
        quizQuestion.setQuiz(quiz)
        quizQuestion.setQuestion(question)
        quizQuestionRepository.save(quizQuestion)

        when:
        def statementQuizDto = answerService.startQuiz(user.getId(), quiz.getId())

        then: 'the return statement contains one quiz'
        statementQuizDto.id != null
        statementQuizDto.quizAnswerId != null
        statementQuizDto.title == QUIZ_TITLE
        statementQuizDto.oneWay == oneWay
        statementQuizDto.timed == quizType.equals(Quiz.QuizType.IN_CLASS)
        statementQuizDto.questions.size() == 1
        def questionDto = statementQuizDto.questions.get(0)
        questionDto.content == content
        statementQuizDto.answers.size() == 1
        def answerDto = statementQuizDto.answers.get(0)
        answerDto.questionAnswerId != null
        answerDto.quizQuestionId == quizQuestion.id

        where:
        quizType                | oneWay | qRCodeOnly | availableDate     | conclusionDate      | resultsDate      || content
        Quiz.QuizType.PROPOSED  | true   | false      | LOCAL_DATE_BEFORE | null                | null             || null
        Quiz.QuizType.PROPOSED  | false  | false      | LOCAL_DATE_BEFORE | null                | null             || QUESTION_1_CONTENT
        Quiz.QuizType.IN_CLASS  | true   | false      | LOCAL_DATE_BEFORE | LOCAL_DATE_TOMORROW | null             || null
        Quiz.QuizType.IN_CLASS  | true   | false      | LOCAL_DATE_BEFORE | LOCAL_DATE_TOMORROW | LOCAL_DATE_LATER || null
        Quiz.QuizType.IN_CLASS  | false  | false      | LOCAL_DATE_BEFORE | LOCAL_DATE_TOMORROW | null             || QUESTION_1_CONTENT
        Quiz.QuizType.IN_CLASS  | false  | false      | LOCAL_DATE_BEFORE | LOCAL_DATE_TOMORROW | LOCAL_DATE_LATER || QUESTION_1_CONTENT
        Quiz.QuizType.GENERATED | false  | false      | LOCAL_DATE_BEFORE | null                | null             || QUESTION_1_CONTENT
    }

    @Unroll
    def "cannot start quiz and there is no quiz answer: quizType=#quizType | OneWay=#oneWay | qRCodeOnly=#qRCodeOnly | availableDate=#availableDate | conclusionDate=#conclusionDate | resultsDate=#resultsDate"() {
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
        and: 'a quiz question'
        def quizQuestion = new QuizQuestion()
        quizQuestion.setSequence(1)
        quizQuestion.setQuiz(quiz)
        quizQuestion.setQuestion(question)
        quizQuestionRepository.save(quizQuestion)

        when:
        answerService.startQuiz(user.getId(), quiz.getId())

        then: 'an exception is thrown'
        def error = thrown(TutorException)
        error.errorMessage == errorMessage

        where:
        quizType                | oneWay | qRCodeOnly | availableDate         | conclusionDate       | resultsDate      || errorMessage
        Quiz.QuizType.PROPOSED  | false  | false      | LOCAL_DATE_TOMORROW   | null                 | null             || QUIZ_NOT_YET_AVAILABLE
        Quiz.QuizType.PROPOSED  | false  | true       | LOCAL_DATE_YESTERDAY  | null                 | null             || CANNOT_START_QRCODE_QUIZ
        Quiz.QuizType.PROPOSED  | true   | false      | LOCAL_DATE_TOMORROW   | null                 | null             || QUIZ_NOT_YET_AVAILABLE
        Quiz.QuizType.PROPOSED  | true   | true       | LOCAL_DATE_YESTERDAY  | null                 | null             || CANNOT_START_QRCODE_QUIZ
        Quiz.QuizType.IN_CLASS  | false  | false      | LOCAL_DATE_TOMORROW   | LOCAL_DATE_LATER     | null             || QUIZ_NOT_YET_AVAILABLE
        Quiz.QuizType.IN_CLASS  | false  | false      | LOCAL_DATE_BEFORE     | LOCAL_DATE_YESTERDAY | null             || QUIZ_NO_LONGER_AVAILABLE
        Quiz.QuizType.IN_CLASS  | false  | false      | LOCAL_DATE_BEFORE     | LOCAL_DATE_YESTERDAY | LOCAL_DATE_LATER || QUIZ_NO_LONGER_AVAILABLE
        Quiz.QuizType.IN_CLASS  | false  | true       | LOCAL_DATE_YESTERDAY  | LOCAL_DATE_TOMORROW  | null             || CANNOT_START_QRCODE_QUIZ
        Quiz.QuizType.IN_CLASS  | true   | false      | LOCAL_DATE_TOMORROW   | LOCAL_DATE_LATER     | null             || QUIZ_NOT_YET_AVAILABLE
        Quiz.QuizType.IN_CLASS  | true   | false      | LOCAL_DATE_BEFORE     | LOCAL_DATE_YESTERDAY | null             || QUIZ_NO_LONGER_AVAILABLE
        Quiz.QuizType.IN_CLASS  | true   | false      | LOCAL_DATE_BEFORE     | LOCAL_DATE_YESTERDAY | LOCAL_DATE_LATER || QUIZ_NO_LONGER_AVAILABLE
        Quiz.QuizType.IN_CLASS  | true   | true       | LOCAL_DATE_YESTERDAY  | LOCAL_DATE_TOMORROW  | null             || CANNOT_START_QRCODE_QUIZ
    }

    @Unroll
    def "returns statement but quiz answer already exists: quizType=#quizType | OneWay=#oneWay | qRCodeOnly=#qRCodeOnly | availableDate=#availableDate | conclusionDate=#conclusionDate | resultsDate=#resultsDate | creationDate=#creationDate"() {
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
        and: 'a quiz question'
        def quizQuestion = new QuizQuestion()
        quizQuestion.setSequence(1)
        quizQuestion.setQuiz(quiz)
        quizQuestion.setQuestion(question)
        quizQuestionRepository.save(quizQuestion)
        and: 'a quiz answer'
        def quizAnswer = new QuizAnswer(user, quiz)
        quizAnswer.setCompleted(false)
        quizAnswer.setCreationDate(creationDate)
        quizAnswerRepository.save(quizAnswer)

        when:
        def statementQuizDto = answerService.startQuiz(user.getId(), quiz.getId())

        then: 'the return statement contains one quiz'
        statementQuizDto.id != null
        statementQuizDto.quizAnswerId != null
        statementQuizDto.title == QUIZ_TITLE
        statementQuizDto.oneWay == oneWay
        statementQuizDto.timed == quizType.equals(Quiz.QuizType.IN_CLASS)
        statementQuizDto.questions.size() == 1
        def questionDto = statementQuizDto.questions.get(0)
        questionDto.content == content
        statementQuizDto.answers.size() == 1
        def answerDto = statementQuizDto.answers.get(0)
        answerDto.questionAnswerId == quizAnswer.questionAnswers.get(0).id
        answerDto.quizQuestionId == quizQuestion.id

        where:
        quizType                | oneWay | qRCodeOnly | availableDate     | conclusionDate      | resultsDate      | creationDate         || content
        Quiz.QuizType.PROPOSED  | true   | false      | LOCAL_DATE_BEFORE | null                | null             | null                 || null
        Quiz.QuizType.PROPOSED  | false  | false      | LOCAL_DATE_BEFORE | null                | null             | null                 || QUESTION_1_CONTENT
        Quiz.QuizType.PROPOSED  | false  | true       | LOCAL_DATE_BEFORE | null                | null             | null                 || QUESTION_1_CONTENT
        Quiz.QuizType.PROPOSED  | false  | true       | LOCAL_DATE_BEFORE | null                | null             | LOCAL_DATE_YESTERDAY || QUESTION_1_CONTENT
        Quiz.QuizType.IN_CLASS  | true   | false      | LOCAL_DATE_BEFORE | LOCAL_DATE_TOMORROW | null             | null                 || null
        Quiz.QuizType.IN_CLASS  | true   | false      | LOCAL_DATE_BEFORE | LOCAL_DATE_TOMORROW | LOCAL_DATE_LATER | null                 || null
        Quiz.QuizType.IN_CLASS  | true   | true       | LOCAL_DATE_BEFORE | LOCAL_DATE_TOMORROW | null             | null                 || null
        Quiz.QuizType.IN_CLASS  | true   | true       | LOCAL_DATE_BEFORE | LOCAL_DATE_TOMORROW | LOCAL_DATE_LATER | null                 || null
        Quiz.QuizType.IN_CLASS  | false  | false      | LOCAL_DATE_BEFORE | LOCAL_DATE_TOMORROW | null             | null                 || QUESTION_1_CONTENT
        Quiz.QuizType.IN_CLASS  | false  | false      | LOCAL_DATE_BEFORE | LOCAL_DATE_TOMORROW | LOCAL_DATE_LATER | null                 || QUESTION_1_CONTENT
        Quiz.QuizType.IN_CLASS  | false  | true       | LOCAL_DATE_BEFORE | LOCAL_DATE_TOMORROW | LOCAL_DATE_LATER | LOCAL_DATE_YESTERDAY || QUESTION_1_CONTENT
        Quiz.QuizType.GENERATED | false  | false      | LOCAL_DATE_BEFORE | null                | null             | LOCAL_DATE_YESTERDAY || QUESTION_1_CONTENT
    }

    @Unroll
    def "does not return returns statement and quiz answer already exists: quizType=#quizType | OneWay=#oneWay | qRCodeOnly=#qRCodeOnly | availableDate=#availableDate | conclusionDate=#conclusionDate | resultsDate=#resultsDate | creationDate=#creationDate | completed=#completed"() {
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
        and: 'a quiz question'
        def quizQuestion = new QuizQuestion()
        quizQuestion.setSequence(1)
        quizQuestion.setQuiz(quiz)
        quizQuestion.setQuestion(question)
        quizQuestionRepository.save(quizQuestion)
        and: 'a quiz answer'
        def quizAnswer = new QuizAnswer(user, quiz)
        quizAnswer.setCompleted(completed)
        quizAnswer.setCreationDate(creationDate)
        quizAnswerRepository.save(quizAnswer)

        when:
        answerService.startQuiz(user.getId(), quiz.getId())

        then: 'an exception is thrown'
        def error = thrown(TutorException)
        error.errorMessage == errorMessage

        where:
        quizType                 | oneWay | qRCodeOnly | availableDate         | conclusionDate       | resultsDate      | creationDate         | completed || errorMessage
        Quiz.QuizType.PROPOSED   | false  | false      | LOCAL_DATE_BEFORE     | null                 | null             | LOCAL_DATE_YESTERDAY | true      || QUIZ_ALREADY_COMPLETED
        Quiz.QuizType.PROPOSED   | false  | true       | LOCAL_DATE_YESTERDAY  | null                 | null             | LOCAL_DATE_YESTERDAY | true      || QUIZ_ALREADY_COMPLETED
        Quiz.QuizType.PROPOSED   | true   | false      | LOCAL_DATE_YESTERDAY  | null                 | null             | LOCAL_DATE_YESTERDAY | false     || QUIZ_ALREADY_COMPLETED
        Quiz.QuizType.PROPOSED   | true   | false      | LOCAL_DATE_YESTERDAY  | null                 | null             | LOCAL_DATE_YESTERDAY | true      || QUIZ_ALREADY_COMPLETED
        Quiz.QuizType.PROPOSED   | true   | true       | LOCAL_DATE_YESTERDAY  | null                 | null             | LOCAL_DATE_YESTERDAY | false     || QUIZ_ALREADY_COMPLETED
        Quiz.QuizType.PROPOSED   | true   | true       | LOCAL_DATE_YESTERDAY  | null                 | null             | LOCAL_DATE_YESTERDAY | true      || QUIZ_ALREADY_COMPLETED
        Quiz.QuizType.IN_CLASS   | false  | false      | LOCAL_DATE_YESTERDAY  | LOCAL_DATE_LATER     | null             | LOCAL_DATE_YESTERDAY | true      || QUIZ_ALREADY_COMPLETED
        Quiz.QuizType.IN_CLASS   | false  | false      | LOCAL_DATE_BEFORE     | LOCAL_DATE_TOMORROW  | null             | LOCAL_DATE_YESTERDAY | true      || QUIZ_ALREADY_COMPLETED
        Quiz.QuizType.IN_CLASS   | false  | false      | LOCAL_DATE_BEFORE     | LOCAL_DATE_TOMORROW  | LOCAL_DATE_LATER | LOCAL_DATE_YESTERDAY | true      || QUIZ_ALREADY_COMPLETED
        Quiz.QuizType.IN_CLASS   | false  | true       | LOCAL_DATE_YESTERDAY  | LOCAL_DATE_TOMORROW  | null             | LOCAL_DATE_YESTERDAY | true      || QUIZ_ALREADY_COMPLETED
        Quiz.QuizType.IN_CLASS   | true   | false      | LOCAL_DATE_YESTERDAY  | LOCAL_DATE_LATER     | null             | LOCAL_DATE_YESTERDAY | true      || QUIZ_ALREADY_COMPLETED
        Quiz.QuizType.IN_CLASS   | true   | false      | LOCAL_DATE_YESTERDAY  | LOCAL_DATE_LATER     | null             | LOCAL_DATE_YESTERDAY | false     || QUIZ_ALREADY_COMPLETED
        Quiz.QuizType.IN_CLASS   | true   | false      | LOCAL_DATE_BEFORE     | LOCAL_DATE_TOMORROW  | null             | LOCAL_DATE_YESTERDAY | true      || QUIZ_ALREADY_COMPLETED
        Quiz.QuizType.IN_CLASS   | true   | false      | LOCAL_DATE_BEFORE     | LOCAL_DATE_TOMORROW  | null             | LOCAL_DATE_YESTERDAY | false     || QUIZ_ALREADY_COMPLETED
        Quiz.QuizType.IN_CLASS   | true   | false      | LOCAL_DATE_BEFORE     | LOCAL_DATE_TOMORROW  | LOCAL_DATE_LATER | LOCAL_DATE_YESTERDAY | true      || QUIZ_ALREADY_COMPLETED
        Quiz.QuizType.IN_CLASS   | true   | false      | LOCAL_DATE_BEFORE     | LOCAL_DATE_TOMORROW  | LOCAL_DATE_LATER | LOCAL_DATE_YESTERDAY | false     || QUIZ_ALREADY_COMPLETED
        Quiz.QuizType.IN_CLASS   | true   | true       | LOCAL_DATE_YESTERDAY  | LOCAL_DATE_TOMORROW  | null             | LOCAL_DATE_YESTERDAY | true      || QUIZ_ALREADY_COMPLETED
        Quiz.QuizType.IN_CLASS   | true   | true       | LOCAL_DATE_BEFORE     | LOCAL_DATE_TOMORROW  | null             | LOCAL_DATE_YESTERDAY | false     || QUIZ_ALREADY_COMPLETED
        Quiz.QuizType.GENERATED  | false  | false      | LOCAL_DATE_YESTERDAY  | null                 | null             | LOCAL_DATE_TODAY     | true      || QUIZ_ALREADY_COMPLETED
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}
