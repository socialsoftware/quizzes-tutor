package pt.ulisboa.tecnico.socialsoftware.tutor.answer.service

import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.domain.AuthUser
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.MultipleChoiceQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.dto.QuizDto
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.Student
import pt.ulisboa.tecnico.socialsoftware.tutor.utils.DateHandler
import spock.lang.Unroll

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*

@DataJpaTest
class SingleSessionTest extends SpockTest {
    static final String KEY = 'a' * 64

    def student
    def other
    def question

    def setup() {
        createExternalCourseAndExecution()

        student = new Student(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL, false, AuthUser.Type.TECNICO)
        student.addCourse(externalCourseExecution)
        userRepository.save(student)
        other = new Student(USER_2_NAME, USER_2_USERNAME, USER_2_EMAIL, false, AuthUser.Type.TECNICO)
        other.addCourse(externalCourseExecution)
        userRepository.save(other)

        question = new Question()
        question.setKey(1)
        question.setCourse(externalCourse)
        question.setTitle(QUESTION_1_TITLE)
        question.setContent(QUESTION_1_CONTENT)
        def details = new MultipleChoiceQuestion()
        question.setQuestionDetails(details)
        questionDetailsRepository.save(details)
        questionRepository.save(question)
    }

    def quiz(boolean singleSession, boolean qrCodeOnly = false) {
        def quiz = new Quiz()
        quiz.setKey(1)
        quiz.setTitle(QUIZ_TITLE)
        quiz.setCourseExecution(externalCourseExecution)
        quiz.setQrCodeOnly(qrCodeOnly)
        quiz.setType(Quiz.QuizType.IN_CLASS.toString())
        quiz.setAvailableDate(DateHandler.now().minusMinutes(1))
        quiz.setConclusionDate(DateHandler.now().plusHours(1))
        quiz.setResultsDate(DateHandler.now().plusHours(1))
        quiz.setExamRules(singleSession, false, null)
        quizRepository.save(quiz)
        def quizQuestion = new QuizQuestion()
        quizQuestion.setSequence(1)
        quizQuestion.setQuiz(quiz)
        quizQuestion.setQuestion(question)
        quizQuestionRepository.save(quizQuestion)
        return quiz
    }

    def "a single-session quiz opens the first time, and cannot be opened again"() {
        given:
        def quiz = quiz(true)

        when:
        def first = answerService.startQuiz(student.getId(), quiz.getId())

        then:
        first.quizAnswerId != null
        quizAnswerRepository.findAll().get(0).getSessionOpenedAt() != null

        when: "the student closes the page and comes back"
        answerService.startQuiz(student.getId(), quiz.getId())

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == QUIZ_ALREADY_OPENED
        quizAnswerRepository.findAll().get(0).getBlockedOpenings() == 1

        when: "and again"
        answerService.startQuiz(student.getId(), quiz.getId())

        then:
        thrown(TutorException)
        quizAnswerRepository.findAll().get(0).getBlockedOpenings() == 2
    }

    def "a single-session quiz opened by QR code cannot be opened again either"() {
        given:
        def quiz = quiz(true, true)

        when:
        answerService.getQuizByQRCode(student.getId(), quiz.getId())
        answerService.getQuizByQRCode(student.getId(), quiz.getId())

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == QUIZ_ALREADY_OPENED
    }

    def "each student has their own single session"() {
        given:
        def quiz = quiz(true)

        when:
        answerService.startQuiz(student.getId(), quiz.getId())
        answerService.startQuiz(other.getId(), quiz.getId())

        then:
        noExceptionThrown()
    }

    def "an ordinary quiz can be opened as many times as the student wants"() {
        given:
        def quiz = quiz(false)

        when:
        3.times { answerService.startQuiz(student.getId(), quiz.getId()) }

        then:
        noExceptionThrown()
        quizAnswerRepository.findAll().get(0).getSessionOpenedAt() == null
        quizAnswerRepository.findAll().get(0).getBlockedOpenings() == 0
    }

    def "a teacher can let the student open it again"() {
        given:
        def quiz = quiz(true)
        answerService.startQuiz(student.getId(), quiz.getId())
        def quizAnswer = quizAnswerRepository.findAll().get(0)

        when:
        answerService.reopenQuizAnswer(quiz.getId(), quizAnswer.getId())
        answerService.startQuiz(student.getId(), quiz.getId())

        then:
        noExceptionThrown()
    }

    def "a quiz answer of another quiz cannot be reopened through this one"() {
        given:
        def quiz = quiz(true)
        answerService.startQuiz(student.getId(), quiz.getId())
        def quizAnswer = quizAnswerRepository.findAll().get(0)

        when:
        answerService.reopenQuizAnswer(quiz.getId() + 1000, quizAnswer.getId())

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == QUIZ_ANSWER_NOT_FOUND
    }

    def "the teacher sees how many times the student tried"() {
        given:
        def quiz = quiz(true)
        answerService.startQuiz(student.getId(), quiz.getId())
        try { answerService.startQuiz(student.getId(), quiz.getId()) } catch (TutorException ignored) { }

        expect:
        new pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.QuizAnswerDto(quizAnswerRepository.findAll().get(0)).getBlockedOpenings() == 1
    }

    def "the rules are kept with the quiz, and the Config Key goes only to teachers"() {
        given:
        def dto = new QuizDto()
        dto.setTitle(QUIZ_TITLE)
        dto.setType('IN_CLASS')
        dto.setSingleSession(true)
        dto.setSafeBrowserOnly(true)
        dto.setSebConfigKey(KEY.toUpperCase())
        dto.setAvailableDate(STRING_DATE_TODAY)
        dto.setConclusionDate(STRING_DATE_TOMORROW)
        dto.setResultsDate(STRING_DATE_LATER)
        dto.setQuestions([])

        when:
        def created = quizService.createQuiz(externalCourseExecution.getId(), dto)
        def saved = quizRepository.findById(created.getId()).get()

        then:
        created.isSingleSession()
        created.isSafeBrowserOnly()
        created.getSebConfigKey() == KEY
        saved.getSebConfigKey() == KEY

        and: "what a student is given never holds the key"
        new QuizDto(saved, false).getSebConfigKey() == null
        new QuizDto(saved, false).isSafeBrowserOnly()
    }

    @Unroll
    def "the Safe Exam Browser needs a Config Key of 64 hexadecimal characters: #key"() {
        given:
        def dto = new QuizDto()
        dto.setTitle(QUIZ_TITLE)
        dto.setType('IN_CLASS')
        dto.setSafeBrowserOnly(true)
        dto.setSebConfigKey(key)
        dto.setAvailableDate(STRING_DATE_TODAY)
        dto.setConclusionDate(STRING_DATE_TOMORROW)
        dto.setResultsDate(STRING_DATE_LATER)
        dto.setQuestions([])

        when:
        quizService.createQuiz(externalCourseExecution.getId(), dto)

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == QUIZ_SEB_KEY_INVALID

        where:
        key << [null, '', 'abc', 'g' * 64, 'a' * 63]
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}
