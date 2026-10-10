package pt.ulisboa.tecnico.socialsoftware.tutor.questiongeneration

import pt.ulisboa.tecnico.socialsoftware.tutor.questiongeneration.dto.AqgChunkDto
import pt.ulisboa.tecnico.socialsoftware.tutor.questiongeneration.dto.AqgDiscussionRequest
import pt.ulisboa.tecnico.socialsoftware.tutor.questiongeneration.dto.AqgDiscussionSuggestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.questiongeneration.dto.AqgGenerateRequest
import pt.ulisboa.tecnico.socialsoftware.tutor.questiongeneration.dto.AqgJobDto
import pt.ulisboa.tecnico.socialsoftware.tutor.questiongeneration.dto.AqgMaterialDto
import pt.ulisboa.tecnico.socialsoftware.tutor.questiongeneration.dto.LlmModelChoiceDto
import pt.ulisboa.tecnico.socialsoftware.tutor.questiongeneration.dto.LlmModelTestDto
import pt.ulisboa.tecnico.socialsoftware.tutor.questiongeneration.dto.LlmSettingsDto
import pt.ulisboa.tecnico.socialsoftware.tutor.questiongeneration.dto.LlmSettingsViewDto
import pt.ulisboa.tecnico.socialsoftware.tutor.questiongeneration.dto.OllamaModelsDto

/** Stands in for the question generation service: tests set the replies and inspect the calls. */
class StubAqgClient implements AqgClient {
    AqgJobDto generateReply
    AqgJobDto jobReply
    List<AqgMaterialDto> materials = []
    // material id -> its pieces
    Map<String, List<AqgChunkDto>> chunks = [:]
    String reprocessedMaterialId
    AqgDiscussionSuggestionDto suggestionReply
    AqgDiscussionRequest lastSuggestionRequest

    AqgGenerateRequest lastGenerateRequest
    int getJobCalls = 0

    LlmSettingsDto savedLlmSettings
    LlmModelChoiceDto lastTestedModel
    LlmModelChoiceDto lastPulledModel

    /** The bean outlives each test (the Spring context is cached), so tests start from a clean stub. */
    void reset() {
        generateReply = null
        jobReply = null
        materials = []
        chunks = [:]
        reprocessedMaterialId = null
        suggestionReply = null
        lastSuggestionRequest = null
        lastGenerateRequest = null
        getJobCalls = 0
        savedLlmSettings = null
        lastTestedModel = null
        lastPulledModel = null
    }

    /** A READY material of the course with pieces under the given headings, one piece per heading. */
    void addMaterial(String materialId, int courseId, List<String> headings) {
        materials << new AqgMaterialDto(materialId, courseId, materialId + '.pdf', 'READY', headings.size(), 'pymupdf', 1.0d, null)
        chunks[materialId] = headings.withIndex().collect { heading, index ->
            new AqgChunkDto("${materialId}:${index}".toString(), index, heading, "Text of ${heading}".toString())
        }
    }

    @Override
    AqgMaterialDto uploadMaterial(int courseId, String filename, byte[] content) {
        return new AqgMaterialDto('material-1', courseId, filename, 'PROCESSING', 0, null, null, null)
    }

    @Override
    List<AqgMaterialDto> listMaterials(int courseId) {
        return materials.findAll { it.courseId() == null || it.courseId() == courseId }
    }

    @Override
    List<AqgChunkDto> getChunks(String materialId) {
        return chunks.getOrDefault(materialId, [])
    }

    @Override
    AqgMaterialDto reprocessMaterial(String materialId) {
        reprocessedMaterialId = materialId
        return new AqgMaterialDto(materialId, 1, 'a.pdf', 'PROCESSING', 0, null, null, null)
    }

    @Override
    AqgDiscussionSuggestionDto suggestReply(AqgDiscussionRequest request) {
        lastSuggestionRequest = request
        return suggestionReply
    }

    @Override
    AqgJobDto generate(AqgGenerateRequest request) {
        lastGenerateRequest = request
        return generateReply
    }

    @Override
    AqgJobDto getJob(String aqgJobId) {
        getJobCalls++
        return jobReply
    }

    @Override
    LlmSettingsViewDto getLlmSettings() {
        return new LlmSettingsViewDto(savedLlmSettings, [ollama: true, nvidia_nim: false], ['ollama', 'nvidia_nim'], [ollama: 'llama3.1:8b'])
    }

    @Override
    LlmSettingsViewDto saveLlmSettings(LlmSettingsDto settings) {
        savedLlmSettings = settings
        return getLlmSettings()
    }

    @Override
    LlmModelTestDto testModel(LlmModelChoiceDto model) {
        lastTestedModel = model
        return new LlmModelTestDto(true, 0.5d, '{"ok": true}', null)
    }

    @Override
    OllamaModelsDto getOllamaModels() {
        return new OllamaModelsDto(['llama3.1:8b'], [:], null)
    }

    @Override
    OllamaModelsDto pullOllamaModel(LlmModelChoiceDto model) {
        lastPulledModel = model
        return new OllamaModelsDto([], [(model.model()): 'downloading'], null)
    }
}
