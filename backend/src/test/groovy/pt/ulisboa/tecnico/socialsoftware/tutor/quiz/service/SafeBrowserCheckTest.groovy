package pt.ulisboa.tecnico.socialsoftware.tutor.quiz.service

import org.springframework.mock.web.MockHttpServletRequest
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.SafeBrowserCheck
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz
import spock.lang.Specification

import java.security.MessageDigest

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.QUIZ_SAFE_BROWSER_REQUIRED

class SafeBrowserCheckTest extends Specification {
    static final String KEY = 'ab' * 32
    static final String URL = 'https://tutor.example.org/answers/7/start'

    def check = new SafeBrowserCheck()

    // What the Safe Exam Browser sends: the SHA-256 of the address followed by the Config Key, in hex
    static String proof(String address, String key = KEY) {
        return MessageDigest.getInstance('SHA-256').digest((address + key).getBytes('UTF-8')).encodeHex().toString()
    }

    def request(String uri = '/answers/7/start', Map headers = [:]) {
        def request = new MockHttpServletRequest('GET', uri)
        request.setScheme('https')
        request.addHeader('Host', 'tutor.example.org')
        headers.each { name, value -> request.addHeader(name, value) }
        return request
    }

    def quiz(boolean safeOnly = true) {
        def quiz = new Quiz()
        quiz.setExamRules(false, safeOnly, safeOnly ? KEY : null)
        return quiz
    }

    def "a quiz open to any browser is not checked"() {
        expect:
        check.check(quiz(false), request())
    }

    def "the header the browser adds, made from the address of the request, is accepted"() {
        expect:
        check.isFromSafeBrowser(KEY, request('/answers/7/start', ['X-SafeExamBrowser-ConfigKeyHash': proof(URL)]))
    }

    def "the address includes the query, and a proxy's forwarded address is the one the browser asked for"() {
        given:
        def request = new MockHttpServletRequest('GET', '/answers/7/start')
        request.setScheme('http')
        request.addHeader('Host', 'backend:8080')
        request.addHeader('X-Forwarded-Proto', 'https')
        request.addHeader('X-Forwarded-Host', 'tutor.example.org')
        request.setQueryString('a=1')
        request.addHeader('X-SafeExamBrowser-ConfigKeyHash', proof('https://tutor.example.org/answers/7/start?a=1'))

        expect:
        check.isFromSafeBrowser(KEY, request)
    }

    def "the header made from the page that made the request is accepted, without its fragment"() {
        expect:
        check.isFromSafeBrowser(KEY, request('/answers/7/submit', [
                'X-SafeExamBrowser-ConfigKeyHash': proof('https://tutor.example.org/student/quiz'),
                'Referer'                        : 'https://tutor.example.org/student/quiz#top']))
    }

    def "the pair of headers the page sends from the browser's JavaScript API is accepted"() {
        expect:
        check.isFromSafeBrowser(KEY, request('/answers/7/start', [
                'X-SEB-Page-Url'       : 'https://tutor.example.org/student/quiz#x',
                'X-SEB-Config-Key-Hash': proof('https://tutor.example.org/student/quiz')]))
    }

    def "capital letters in the hash do not matter"() {
        expect:
        check.isFromSafeBrowser(KEY, request('/answers/7/start', ['X-SafeExamBrowser-ConfigKeyHash': proof(URL).toUpperCase()]))
    }

    def "an ordinary browser is refused"() {
        when:
        check.check(quiz(), request())

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == QUIZ_SAFE_BROWSER_REQUIRED
    }

    def "a proof made with another key, for another address or of the wrong shape is refused"() {
        expect:
        !check.isFromSafeBrowser(KEY, request('/answers/7/start', ['X-SafeExamBrowser-ConfigKeyHash': proof(URL, 'cd' * 32)]))
        !check.isFromSafeBrowser(KEY, request('/answers/7/start', ['X-SafeExamBrowser-ConfigKeyHash': proof('https://tutor.example.org/answers/8/start')]))
        !check.isFromSafeBrowser(KEY, request('/answers/7/start', ['X-SafeExamBrowser-ConfigKeyHash': 'not a hash']))
        !check.isFromSafeBrowser(KEY, request('/answers/7/start', ['X-SEB-Config-Key-Hash': proof('x')]))
        !check.isFromSafeBrowser(KEY, request('/answers/7/start', ['X-SEB-Page-Url': 'https://x']))
    }

    def "a quiz with no key cannot be satisfied, whatever the request says"() {
        expect:
        !check.isFromSafeBrowser(null, request('/answers/7/start', ['X-SafeExamBrowser-ConfigKeyHash': proof(URL, '')]))
        !check.isFromSafeBrowser('', request('/answers/7/start', ['X-SafeExamBrowser-ConfigKeyHash': proof(URL, '')]))
    }
}
