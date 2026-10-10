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

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.LLM_SETTINGS_MISSING;

/**
 * The administrator's view of which models write the questions. The generation service keeps and
 * checks the settings; API keys never leave its environment, so they are neither read nor sent here.
 */
@Service
public class LlmSettingsService {
    @Autowired
    private AqgClient aqgClient;

    public LlmSettingsViewDto getSettings() {
        return aqgClient.getLlmSettings();
    }

    public LlmSettingsViewDto saveSettings(LlmSettingsDto settings) {
        if (settings == null || settings.primary() == null)
            throw new TutorException(LLM_SETTINGS_MISSING);
        return aqgClient.saveLlmSettings(settings);
    }

    public LlmModelTestDto testModel(LlmModelChoiceDto model) {
        checkModel(model);
        return aqgClient.testModel(model);
    }

    public ProviderModelsDto getProviderModels(String provider, boolean refresh) {
        if (provider == null || provider.isBlank())
            throw new TutorException(LLM_SETTINGS_MISSING);
        return aqgClient.getProviderModels(provider, refresh);
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
