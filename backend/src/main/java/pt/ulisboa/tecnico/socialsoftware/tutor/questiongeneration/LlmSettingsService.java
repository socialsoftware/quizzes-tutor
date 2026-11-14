package pt.ulisboa.tecnico.socialsoftware.tutor.questiongeneration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.questiongeneration.dto.LlmModelChoiceDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.questiongeneration.dto.LlmModelTestDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.questiongeneration.dto.LlmSettingsDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.questiongeneration.dto.LlmSettingsViewDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.questiongeneration.dto.OllamaModelsDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.questiongeneration.dto.ProviderModelsDto;

import java.util.LinkedHashMap;

import java.util.Map;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.LLM_SETTINGS_MISSING;

/**
 * The administrator's view of which models write the questions. The generation service keeps and
 * checks the settings; API keys never leave its environment, so they are neither read nor sent here.
 */
@Service
public class LlmSettingsService {
    @Autowired
    private AqgClient aqgClient;

    @Autowired
    private TeacherApiKeyService teacherApiKeyService;

    /** The settings; a provider the administrator gave a key for here has one, besides those in the service's environment. */
    public LlmSettingsViewDto getSettings() {
        LlmSettingsViewDto view = aqgClient.getLlmSettings();
        Map<String, Boolean> keys = new LinkedHashMap<>(view.keys() == null ? Map.of() : view.keys());
        teacherApiKeyService.siteProviders().forEach(provider -> keys.put(provider, true));
        return new LlmSettingsViewDto(view.settings(), keys, view.providers(), view.defaultModels(), view.providerNames());
    }

    public LlmSettingsViewDto saveSettings(LlmSettingsDto settings) {
        if (settings == null || settings.primary() == null)
            throw new TutorException(LLM_SETTINGS_MISSING);
        aqgClient.saveLlmSettings(settings, teacherApiKeyService.siteProviders());
        return getSettings();
    }

    public LlmModelTestDto testModel(LlmModelChoiceDto model) {
        checkModel(model);
        // the site's own key for the provider, if the administrator gave one here
        String key = teacherApiKeyService.siteKeys().get(model.provider());
        return key == null ? aqgClient.testModel(model) : aqgClient.testModel(model, key);
    }

    public ProviderModelsDto getProviderModels(String provider, boolean refresh) {
        if (provider == null || provider.isBlank())
            throw new TutorException(LLM_SETTINGS_MISSING);
        return aqgClient.getProviderModels(provider, refresh, teacherApiKeyService.siteKeys().get(provider));
    }

    public OllamaModelsDto getOllamaModels() {
        return aqgClient.getOllamaModels();
    }

    public OllamaModelsDto pullOllamaModel(LlmModelChoiceDto model) {
        checkModel(model);
        return aqgClient.pullOllamaModel(model);
    }

    private void checkModel(LlmModelChoiceDto model) {
        if (model == null || model.provider() == null || model.model() == null || model.model().isBlank())
            throw new TutorException(LLM_SETTINGS_MISSING);
    }
}
