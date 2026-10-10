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

    /** A draft reply to a student's doubt, for a teacher to edit; nothing is saved or sent. */
    AqgDiscussionSuggestionDto suggestReply(AqgDiscussionRequest request);

    AqgJobDto generate(AqgGenerateRequest request);

    AqgJobDto getJob(String aqgJobId);

    /** The models the service uses, and whether each provider's API key is set in its environment. */
    LlmSettingsViewDto getLlmSettings();

    LlmSettingsViewDto saveLlmSettings(LlmSettingsDto settings);

    /** One tiny call to a model, to check its name and how fast it answers. */
    LlmModelTestDto testModel(LlmModelChoiceDto model);

    OllamaModelsDto getOllamaModels();

    /** Starts downloading a model to the Ollama server; it shows in getOllamaModels when done. */
    OllamaModelsDto pullOllamaModel(LlmModelChoiceDto model);
}
