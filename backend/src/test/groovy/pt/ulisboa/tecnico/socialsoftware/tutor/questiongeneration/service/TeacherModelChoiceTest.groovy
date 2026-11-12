package pt.ulisboa.tecnico.socialsoftware.tutor.questiongeneration.service

import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.domain.AuthUser
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic
import pt.ulisboa.tecnico.socialsoftware.tutor.questiongeneration.dto.AqgGenerateRequest
import pt.ulisboa.tecnico.socialsoftware.tutor.questiongeneration.dto.AqgJobDto
import pt.ulisboa.tecnico.socialsoftware.tutor.questiongeneration.dto.GenerationRequestDto
import pt.ulisboa.tecnico.socialsoftware.tutor.questiongeneration.dto.LlmModelChoiceDto
import pt.ulisboa.tecnico.socialsoftware.tutor.questiongeneration.dto.LlmSettingsDto
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.Teacher
import spock.lang.Unroll

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.GENERATION_MODEL_NOT_ALLOWED

@DataJpaTest
class TeacherModelChoiceTest extends SpockTest {
    def teacher
    def topic

    def setup() {
        aqgClient.reset()
        aqgClient.generateReply = new AqgJobDto('aqg-1', 'PENDING', 'mcq-v3', 'model', 'STRICT', [], null)
        aqgClient.savedLlmSettings = new LlmSettingsDto(new LlmModelChoiceDto('nvidia_nim', 'usual/model'), [],
                'http://ollama:11434', null, 'default', 120d, false, 2, true, 12000,
                [new LlmModelChoiceDto('nvidia_nim', 'free/model'), new LlmModelChoiceDto('ollama', 'llama3.2:3b')])
        createExternalCourseAndExecution()

        teacher = new Teacher(USER_2_NAME, USER_2_USERNAME, USER_2_EMAIL, false, AuthUser.Type.TECNICO)
        userRepository.save(teacher)

        topic = new Topic()
        topic.setName('HTTP')
        topic.setCourse(externalCourse)
        topic.addSource('material-1', 'material-1:0')
        topicRepository.save(topic)
    }

    def request(String provider = null, String model = null) {
        def dto = new GenerationRequestDto()
        dto.setTopicId(topic.getId())
        dto.setCount(1)
        dto.setProvider(provider)
        dto.setModel(model)
        return dto
    }

    def ask(String provider = null, String model = null) {
        return questionGenerationService.requestGeneration(externalCourseExecution.getId(), teacher.getId(), request(provider, model))
    }

    def "a teacher is offered the usual model and the ones the administrator lists"() {
        when:
        def models = questionGenerationService.getModels(teacher.getId())

        then:
        models.defaultModel().provider() == 'nvidia_nim'
        models.defaultModel().model() == 'usual/model'
        models.models()*.model() == ['free/model', 'llama3.2:3b']
        models.models()*.ownKey() == [false, false]
        models.ownKeyProviders().isEmpty()
    }

    def "with a key of their own a teacher is told which models their key would pay for"() {
        given:
        teacherApiKeyService.saveKey(teacher.getId(), 'nvidia_nim', 'nvapi-SECRET-key-0123456789')

        when:
        def models = questionGenerationService.getModels(teacher.getId())

        then:
        models.models()*.ownKey() == [true, false]
        models.ownKeyProviders() == ['nvidia_nim']
        !models.toString().contains('SECRET')
    }

    def "a model on the administrator's list can be picked, and the service is told"() {
        when:
        def job = ask('nvidia_nim', 'free/model')

        then:
        aqgClient.lastGenerateRequest.chosenModel() == new AqgGenerateRequest.ChosenModel('nvidia_nim', 'free/model')
        job.getChosenModel() == 'nvidia_nim/free/model'
        generationJobRepository.findAll().get(0).getChosenModel() == 'free/model'
    }

    def "with no pick the usual models are used"() {
        when:
        ask()

        then:
        aqgClient.lastGenerateRequest.chosenModel() == null
    }

    def "a model that is not listed cannot be picked without a key of the provider"() {
        when:
        ask('nvidia_nim', 'other/model')

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == GENERATION_MODEL_NOT_ALLOWED
        generationJobRepository.count() == 0L
        aqgClient.lastGenerateRequest == null
    }

    def "with their own key of a provider a teacher can pick any model of it, and only of it"() {
        given:
        teacherApiKeyService.saveKey(teacher.getId(), 'openai', 'sk-SECRET-key-0123456789')

        when:
        ask('openai', 'gpt-4o')

        then:
        aqgClient.lastGenerateRequest.chosenModel() == new AqgGenerateRequest.ChosenModel('openai', 'gpt-4o')
        aqgClient.lastGenerateRequest.apiKeys().keySet() == ['openai'] as Set

        when:
        ask('anthropic', 'claude-sonnet-5')

        then:
        thrown(TutorException)
    }

    @Unroll
    def "#what is not a model that can be picked"() {
        given:
        teacherApiKeyService.saveKey(teacher.getId(), 'openai', 'sk-SECRET-key-0123456789')

        when:
        ask(provider, model)

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == GENERATION_MODEL_NOT_ALLOWED

        where:
        what                    | provider   | model
        'a provider alone'      | 'openai'   | null
        'a model alone'         | null       | 'gpt-4o'
        'an unknown provider'   | 'nowhere'  | 'gpt-4o'
        'a name with a space'   | 'openai'   | 'gpt 4o'
        'a name with a command' | 'openai'   | 'gpt-4o; rm -rf /'
        'a name too long'       | 'openai'   | 'g' * 201
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}
