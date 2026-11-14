package pt.ulisboa.tecnico.socialsoftware.tutor.questiongeneration.service

import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.questiongeneration.TeacherApiKeyService
import pt.ulisboa.tecnico.socialsoftware.tutor.questiongeneration.dto.LlmModelChoiceDto
import pt.ulisboa.tecnico.socialsoftware.tutor.questiongeneration.dto.LlmSettingsDto
import spock.lang.Unroll

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.LLM_SETTINGS_MISSING

@DataJpaTest
class LlmSettingsTest extends SpockTest {
    def settings(LlmModelChoiceDto primary) {
        return new LlmSettingsDto(primary, [new LlmModelChoiceDto('ollama', 'llama3.2:3b')], 'http://ollama:11434', 8192, 'off', 120d, false, 2, true, 12000)
    }

    def setup() {
        aqgClient.reset()
    }

    def "the settings go to the generation service as they are"() {
        given:
        def sent = settings(new LlmModelChoiceDto('nvidia_nim', 'nvidia/nemotron-3-ultra-550b-a55b'))

        when:
        def view = llmSettingsService.saveSettings(sent)

        then:
        aqgClient.savedLlmSettings == sent
        view.settings() == sent
        view.keys() == [ollama: true, nvidia_nim: false]
    }

    def "settings without a primary model are refused before calling the service"() {
        when:
        llmSettingsService.saveSettings(settings(null))

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == LLM_SETTINGS_MISSING
        aqgClient.savedLlmSettings == null
    }

    def "a model is tested and downloaded through the service"() {
        given:
        def model = new LlmModelChoiceDto('ollama', 'qwen3:4b')

        when:
        def result = llmSettingsService.testModel(model)
        def pulls = llmSettingsService.pullOllamaModel(model)

        then:
        result.ok()
        aqgClient.lastTestedModel == model
        pulls.pulls() == ['qwen3:4b': 'downloading']
    }

    def "the models of a provider come from the service, and a provider is needed"() {
        expect:
        llmSettingsService.getProviderModels('nvidia_nim', false).models() == ['model-of-nvidia_nim']

        when:
        llmSettingsService.getProviderModels(' ', false)

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == LLM_SETTINGS_MISSING
    }

    @Unroll
    def "a model without a #what is refused"() {
        when:
        llmSettingsService.testModel(model)

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == LLM_SETTINGS_MISSING
        aqgClient.lastTestedModel == null

        where:
        what       | model
        'provider' | new LlmModelChoiceDto(null, 'x')
        'name'     | new LlmModelChoiceDto('ollama', ' ')
        'anything' | null
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}

    def "a key the administrator gave here counts as a key for its provider, on top of those in the service's environment"() {
        given:
        createExternalCourseAndExecution()

        when:
        def before = llmSettingsService.getSettings()
        teacherApiKeyService.saveKey(TeacherApiKeyService.SITE, 'gemini', 'site-gemini-key-1234')
        def after = llmSettingsService.getSettings()

        then:
        before.keys().gemini == null
        after.keys().gemini
        after.keys().ollama
        after.providerNames().gemini == 'Google Gemini'
        !after.toString().contains('site-gemini-key-1234')
    }

    def "saving settings tells the service which providers the site has a key for"() {
        given:
        teacherApiKeyService.saveKey(TeacherApiKeyService.SITE, 'gemini', 'site-gemini-key-1234')

        when:
        llmSettingsService.saveSettings(settings(new LlmModelChoiceDto('gemini', 'gemini-2.5-flash')))

        then:
        aqgClient.lastProvidersWithKey == ['gemini']
    }

    def "a model is tested with the site's key for its provider, when there is one"() {
        given:
        teacherApiKeyService.saveKey(TeacherApiKeyService.SITE, 'gemini', 'site-gemini-key-1234')

        when:
        llmSettingsService.testModel(new LlmModelChoiceDto('gemini', 'gemini-2.5-flash'))

        then:
        aqgClient.lastTestedKey == 'site-gemini-key-1234'

        when: "another provider is tested with the service's own key"
        aqgClient.lastTestedKey = null
        llmSettingsService.testModel(new LlmModelChoiceDto('groq', 'llama-3.3-70b-versatile'))

        then:
        aqgClient.lastTestedKey == null
    }

    def "the models of a provider are read with the site's key for it, when there is one"() {
        given:
        teacherApiKeyService.saveKey(TeacherApiKeyService.SITE, 'gemini', 'site-gemini-key-1234')

        when:
        llmSettingsService.getProviderModels('gemini', false)

        then:
        aqgClient.lastModelsKey == 'site-gemini-key-1234'

        when:
        llmSettingsService.getProviderModels('groq', false)

        then:
        aqgClient.lastModelsKey == null
    }
}

