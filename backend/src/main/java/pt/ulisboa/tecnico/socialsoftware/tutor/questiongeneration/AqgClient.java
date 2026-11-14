package pt.ulisboa.tecnico.socialsoftware.tutor.questiongeneration;

import pt.ulisboa.tecnico.socialsoftware.tutor.questiongeneration.dto.AqgChunkDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.questiongeneration.dto.AqgDiscussionRequest;
import pt.ulisboa.tecnico.socialsoftware.tutor.questiongeneration.dto.AqgDiscussionSuggestionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.questiongeneration.dto.AqgGenerateRequest;
import pt.ulisboa.tecnico.socialsoftware.tutor.questiongeneration.dto.AqgJobDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.questiongeneration.dto.AqgMaterialDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.questiongeneration.dto.LlmModelChoiceDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.questiongeneration.dto.LlmModelTestDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.questiongeneration.dto.LlmSettingsDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.questiongeneration.dto.LlmSettingsViewDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.questiongeneration.dto.OllamaModelsDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.questiongeneration.dto.ProviderModelsDto;

import java.util.List;

/**
 * The only door to the question generation service. It talks HTTP and JSON and never
 * touches its database, so either side can change without the other noticing.
 */
public interface AqgClient {
    AqgMaterialDto uploadMaterial(int courseId, String filename, byte[] content);

    List<AqgMaterialDto> listMaterials(int courseId);

    /** The pieces (chunks) of a material in reading order, with the headings above each one. */
    List<AqgChunkDto> getChunks(String materialId);

    /** Rebuilds a material's chunks with the service's current parsers; the old chunk ids are gone. */
    AqgMaterialDto reprocessMaterial(String materialId);

    /** Forgets a material, its pieces and its original file. */
    void deleteMaterial(String materialId);

    /** A draft reply to a student's doubt, for a teacher to edit; nothing is saved or sent. */
    AqgDiscussionSuggestionDto suggestReply(AqgDiscussionRequest request);

    AqgJobDto generate(AqgGenerateRequest request);

    AqgJobDto getJob(String aqgJobId);

    /** The models the service uses, and whether each provider's API key is set in its environment. */
    LlmSettingsViewDto getLlmSettings();

    /** Saves the settings; `providersWithKey` are the providers the Tutor holds a key for, which count as having one. */
    LlmSettingsViewDto saveLlmSettings(LlmSettingsDto settings, List<String> providersWithKey);

    default LlmSettingsViewDto saveLlmSettings(LlmSettingsDto settings) {
        return saveLlmSettings(settings, List.of());
    }

    /** One tiny call to a model, to check its name and how fast it answers. */
    LlmModelTestDto testModel(LlmModelChoiceDto model);

    /** The same call made with the given API key (a teacher checking their own); the key is not kept. */
    LlmModelTestDto testModel(LlmModelChoiceDto model, String apiKey);

    /** The models a provider offers, read from the provider with the service's key; `refresh` skips its cache. */
    /** The models of a provider, read with `apiKey` when one is given (the site's, for a provider the service has none for). */
    ProviderModelsDto getProviderModels(String provider, boolean refresh, String apiKey);

    default ProviderModelsDto getProviderModels(String provider, boolean refresh) {
        return getProviderModels(provider, refresh, null);
    }

    OllamaModelsDto getOllamaModels();

    /** Starts downloading a model to the Ollama server; it shows in getOllamaModels when done. */
    OllamaModelsDto pullOllamaModel(LlmModelChoiceDto model);
}
