package pt.ulisboa.tecnico.socialsoftware.tutor.quiz.service

import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.web.servlet.HandlerMapping
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.SafeBrowserAccess
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.SafeBrowserCheck
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository.QuizRepository
import spock.lang.Specification

import java.security.MessageDigest

class SafeBrowserAccessTest extends Specification {
    static final String KEY = 'cd' * 32

    def quizzes = Stub(QuizRepository)
    def access = new SafeBrowserAccess()

    def setup() {
        access.@quizRepository = quizzes
        access.@safeBrowserCheck = new SafeBrowserCheck()
    }

    def quiz(boolean safeOnly) {
        def quiz = new Quiz()
        quiz.setExamRules(false, safeOnly, safeOnly ? KEY : null)
        return quiz
    }

    def request(Map variables, Map headers = [:]) {
        def request = new MockHttpServletRequest('GET', '/answers/7/start')
        request.setScheme('https')
        request.addHeader('Host', 'tutor.example.org')
        request.setAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE, variables)
        headers.each { name, value -> request.addHeader(name, value) }
        return request
    }

    def "the requests for a quiz that is only for the Safe Exam Browser are refused from another browser"() {
        given:
        quizzes.findById(7) >> Optional.of(quiz(true))

        when:
        access.preHandle(request([quizId: '7']), new MockHttpServletResponse(), null)

        then:
        thrown(TutorException)
    }

    def "and accepted with the proof of the browser"() {
        given:
        quizzes.findById(7) >> Optional.of(quiz(true))
        def proof = MessageDigest.getInstance('SHA-256').digest(('https://tutor.example.org/answers/7/start' + KEY).getBytes('UTF-8')).encodeHex().toString()

        expect:
        access.preHandle(request([quizId: '7'], ['X-SafeExamBrowser-ConfigKeyHash': proof]), new MockHttpServletResponse(), null)
    }

    def "a quiz for any browser lets every request through"() {
        given:
        quizzes.findById(7) >> Optional.of(quiz(false))

        expect:
        access.preHandle(request([quizId: '7']), new MockHttpServletResponse(), null)
    }

    def "a request that names no quiz, or one that does not exist, is left to the handler"() {
        given:
        quizzes.findById(_) >> Optional.empty()

        expect:
        access.preHandle(request([:]), new MockHttpServletResponse(), null)
        access.preHandle(request([quizId: 'x; drop']), new MockHttpServletResponse(), null)
        access.preHandle(request([quizId: '99']), new MockHttpServletResponse(), null)
    }
}
