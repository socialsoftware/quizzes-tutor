package pt.ulisboa.tecnico.socialsoftware.tutor.questiongeneration.service

import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.domain.AuthUser
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic
import pt.ulisboa.tecnico.socialsoftware.tutor.questiongeneration.ApiKeyCipher
import pt.ulisboa.tecnico.socialsoftware.tutor.questiongeneration.TeacherApiKeyService
import pt.ulisboa.tecnico.socialsoftware.tutor.questiongeneration.dto.AqgJobDto
import pt.ulisboa.tecnico.socialsoftware.tutor.questiongeneration.dto.LlmModelChoiceDto
import pt.ulisboa.tecnico.socialsoftware.tutor.questiongeneration.dto.GenerationRequestDto
import pt.ulisboa.tecnico.socialsoftware.tutor.questiongeneration.dto.LlmModelTestDto
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.Teacher
import org.springframework.test.util.AopTestUtils
import spock.lang.Unroll

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*

@DataJpaTest
class TeacherApiKeyTest extends SpockTest {
    static final String KEY = 'nvapi-SECRET-key-0123456789'

    def teacher
    def other
    def topic
    def configured

    def setup() {
        aqgClient.reset()
        aqgClient.generateReply = new AqgJobDto('aqg-1', 'PENDING', 'mcq-v3', 'model', 'STRICT', [], null)
        createExternalCourseAndExecution()

        teacher = new Teacher(USER_2_NAME, USER_2_USERNAME, USER_2_EMAIL, false, AuthUser.Type.TECNICO)
        userRepository.save(teacher)
        other = new Teacher(USER_3_NAME, USER_3_USERNAME, USER_3_EMAIL, false, AuthUser.Type.TECNICO)
        userRepository.save(other)

        topic = new Topic()
        topic.setName('HTTP')
        topic.setCourse(externalCourse)
        topic.addSource('material-1', 'material-1:0')
        topicRepository.save(topic)
        configured = target().@cipher
    }

    def cleanup() {
        target().@cipher = configured
    }

    // the service is behind a transactional proxy: the cipher is swapped on the object itself
    def target() {
        return AopTestUtils.getUltimateTargetObject(teacherApiKeyService)
    }

    def request() {
        def dto = new GenerationRequestDto()
        dto.setTopicId(topic.getId())
        dto.setCount(1)
        return dto
    }

    def "a key is checked with the provider, then kept encrypted, and only its end is shown back"() {
        when:
        def keys = teacherApiKeyService.saveKey(teacher.getId(), 'nvidia_nim', "  ${KEY}  ")

        then:
        aqgClient.lastTestedKey == KEY
        aqgClient.lastTestedModel.provider() == 'nvidia_nim'
        keys.available()
        keys.keys()*.provider() == ['nvidia_nim']
        keys.keys()[0].name() == 'NVIDIA NIM'
        keys.keys()[0].lastFour() == '3456789'.substring(3)
        keys.providers()*.id().containsAll(['nvidia_nim', 'openai', 'anthropic', 'gemini', 'groq'])
        !keys.providers()*.id().contains('ollama')
        !keys.toString().contains('SECRET')
        def row = teacherApiKeyRepository.findByUserIdAndProvider(teacher.getId(), 'nvidia_nim').get()
        !row.getEncryptedKey().contains('SECRET')
        !row.toString().contains(row.getEncryptedKey())
        teacherApiKeyService.keysOf(teacher.getId()) == [nvidia_nim: KEY]
    }

    def "a key the provider does not accept is not kept, and the answer does not repeat it"() {
        given:
        aqgClient.keyTestReply = new LlmModelTestDto(false, 0.2d, null, 'AuthenticationError: 401 bad key')

        when:
        teacherApiKeyService.saveKey(teacher.getId(), 'openai', KEY)

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == TEACHER_API_KEY_REJECTED
        exception.getMessage().contains('401')
        !exception.getMessage().contains(KEY)
        teacherApiKeyRepository.count() == 0L
    }

    @Unroll
    def "#why is not a key"() {
        when:
        teacherApiKeyService.saveKey(teacher.getId(), 'nvidia_nim', key)

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == TEACHER_API_KEY_INVALID
        aqgClient.lastTestedKey == null

        where:
        why                     | key
        'nothing'               | null
        'a blank'               | '   '
        'something too short'   | 'short'
        'something with spaces' | 'nvapi key with spaces 12345'
        'something too long'    | 'k' * 501
    }

    @Unroll
    def "#provider is not a provider a key can be given for"() {
        when:
        teacherApiKeyService.saveKey(teacher.getId(), provider, KEY)

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == TEACHER_API_KEY_PROVIDER
        aqgClient.lastTestedKey == null

        where:
        provider << ['ollama', 'bedrock', 'carrier-pigeon', null]
    }

    def "a key can be given for any provider the service knows, checked with the usual model of that provider"() {
        when:
        def keys = teacherApiKeyService.saveKey(teacher.getId(), 'gemini', KEY)

        then:
        aqgClient.lastTestedModel == new LlmModelChoiceDto('gemini', 'gemini-2.5-flash')
        aqgClient.lastTestedKey == KEY
        keys.keys()*.name() == ['Google Gemini']
        teacherApiKeyService.keysOf(teacher.getId()) == [gemini: KEY]
    }

    def "the teacher can name the model to check the key with"() {
        when:
        teacherApiKeyService.saveKey(teacher.getId(), 'groq', KEY, ' llama-3.1-8b-instant ')

        then:
        aqgClient.lastTestedModel == new LlmModelChoiceDto('groq', 'llama-3.1-8b-instant')
    }

    def "a model name that is not one cannot be used to check a key"() {
        when:
        teacherApiKeyService.saveKey(teacher.getId(), 'groq', KEY, 'x; rm -rf /')

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == TEACHER_API_KEY_MODEL
        aqgClient.lastTestedKey == null
    }

    def "the keys are listed by the name of the provider"() {
        given:
        teacherApiKeyService.saveKey(teacher.getId(), 'nvidia_nim', KEY)
        teacherApiKeyService.saveKey(teacher.getId(), 'gemini', KEY)
        teacherApiKeyService.saveKey(teacher.getId(), 'groq', KEY)

        expect:
        teacherApiKeyService.getKeys(teacher.getId()).keys()*.name() == ['Google Gemini', 'Groq', 'NVIDIA NIM']
    }

    def "the administrator's keys are the site's: they go with every request, and a teacher's own win over them"() {
        given:
        teacherApiKeyService.saveKey(TeacherApiKeyService.SITE, 'gemini', 'site-gemini-key-1234')
        teacherApiKeyService.saveKey(TeacherApiKeyService.SITE, 'groq', 'site-groq-key-1234')
        teacherApiKeyService.saveKey(teacher.getId(), 'groq', 'teacher-groq-key-1234')

        expect:
        teacherApiKeyService.siteKeys() == [gemini: 'site-gemini-key-1234', groq: 'site-groq-key-1234']
        teacherApiKeyService.siteProviders().toSet() == ['gemini', 'groq'] as Set
        teacherApiKeyService.keysOf(teacher.getId()) == [gemini: 'site-gemini-key-1234', groq: 'teacher-groq-key-1234']
        teacherApiKeyService.keysOf(other.getId()) == [gemini: 'site-gemini-key-1234', groq: 'site-groq-key-1234']
        and: "a teacher's own providers are theirs only"
        teacherApiKeyService.providersWithKey(teacher.getId()) == ['groq']
        teacherApiKeyService.providersWithKey(other.getId()).isEmpty()
        and: "the page of a teacher never lists the site's keys"
        teacherApiKeyService.getKeys(other.getId()).keys().isEmpty()
        teacherApiKeyService.getKeys(TeacherApiKeyService.SITE).keys().size() == 2
    }

    def "a request for questions carries the site's keys when the teacher has none of their own"() {
        given:
        teacherApiKeyService.saveKey(TeacherApiKeyService.SITE, 'gemini', 'site-gemini-key-1234')

        when:
        questionGenerationService.requestGeneration(externalCourseExecution.getId(), teacher.getId(), request())

        then:
        aqgClient.lastGenerateRequest.apiKeys() == [gemini: 'site-gemini-key-1234']
    }

    def "a new key replaces the old one, and a key can be removed"() {
        given:
        teacherApiKeyService.saveKey(teacher.getId(), 'nvidia_nim', KEY)

        when:
        teacherApiKeyService.saveKey(teacher.getId(), 'nvidia_nim', 'nvapi-another-key-abcdef')

        then:
        teacherApiKeyRepository.count() == 1L
        teacherApiKeyService.keysOf(teacher.getId()) == [nvidia_nim: 'nvapi-another-key-abcdef']

        when:
        def keys = teacherApiKeyService.removeKey(teacher.getId(), 'nvidia_nim')

        then:
        keys.keys().isEmpty()
        teacherApiKeyRepository.count() == 0L
        teacherApiKeyService.keysOf(teacher.getId()).isEmpty()
    }

    def "a teacher cannot see or remove the keys of another"() {
        given:
        teacherApiKeyService.saveKey(teacher.getId(), 'nvidia_nim', KEY)

        expect:
        teacherApiKeyService.getKeys(other.getId()).keys().isEmpty()
        teacherApiKeyService.keysOf(other.getId()).isEmpty()

        when:
        teacherApiKeyService.removeKey(other.getId(), 'nvidia_nim')

        then:
        teacherApiKeyService.keysOf(teacher.getId()) == [nvidia_nim: KEY]
    }

    def "a key moved to another teacher's row cannot be read"() {
        given:
        teacherApiKeyService.saveKey(teacher.getId(), 'nvidia_nim', KEY)
        def stolen = teacherApiKeyRepository.findByUserIdAndProvider(teacher.getId(), 'nvidia_nim').get()
        def copy = new pt.ulisboa.tecnico.socialsoftware.tutor.questiongeneration.domain.TeacherApiKey(other.getId(), 'nvidia_nim')
        copy.replace(stolen.getEncryptedKey(), stolen.getLastFour())
        teacherApiKeyRepository.save(copy)

        when:
        teacherApiKeyService.keysOf(other.getId())

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == TEACHER_API_KEY_UNREADABLE
    }

    def "a server with no master key cannot keep keys, and sends none"() {
        given:
        target().@cipher = new ApiKeyCipher('')

        when:
        teacherApiKeyService.saveKey(teacher.getId(), 'nvidia_nim', KEY)

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == TEACHER_API_KEYS_NOT_CONFIGURED
        !teacherApiKeyService.getKeys(teacher.getId()).available()
        teacherApiKeyService.keysOf(teacher.getId()).isEmpty()
    }

    def "a request for questions carries the keys of the teacher who asks and nobody else's"() {
        given:
        teacherApiKeyService.saveKey(teacher.getId(), 'nvidia_nim', KEY)
        teacherApiKeyService.saveKey(other.getId(), 'nvidia_nim', 'nvapi-the-others-key-999')

        when:
        questionGenerationService.requestGeneration(externalCourseExecution.getId(), teacher.getId(), request())

        then:
        aqgClient.lastGenerateRequest.apiKeys() == [nvidia_nim: KEY]
        !aqgClient.lastGenerateRequest.toString().contains(KEY)
    }

    def "a teacher with no key of their own sends none"() {
        when:
        questionGenerationService.requestGeneration(externalCourseExecution.getId(), teacher.getId(), request())

        then:
        aqgClient.lastGenerateRequest.apiKeys().isEmpty()
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}
