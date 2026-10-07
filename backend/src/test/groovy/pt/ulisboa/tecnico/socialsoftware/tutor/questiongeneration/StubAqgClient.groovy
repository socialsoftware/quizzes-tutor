package pt.ulisboa.tecnico.socialsoftware.tutor.questiongeneration

import pt.ulisboa.tecnico.socialsoftware.tutor.questiongeneration.dto.AqgGenerateRequest
import pt.ulisboa.tecnico.socialsoftware.tutor.questiongeneration.dto.AqgJobDto
import pt.ulisboa.tecnico.socialsoftware.tutor.questiongeneration.dto.AqgDiscussionRequest
import pt.ulisboa.tecnico.socialsoftware.tutor.questiongeneration.dto.AqgDiscussionSuggestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.questiongeneration.dto.AqgMaterialDto
import pt.ulisboa.tecnico.socialsoftware.tutor.questiongeneration.dto.AqgOutlineEditDto
import pt.ulisboa.tecnico.socialsoftware.tutor.questiongeneration.dto.AqgOutlineEditResultDto
import pt.ulisboa.tecnico.socialsoftware.tutor.questiongeneration.dto.AqgOutlineNodeDto
import pt.ulisboa.tecnico.socialsoftware.tutor.questiongeneration.dto.AqgParagraphDto
import pt.ulisboa.tecnico.socialsoftware.tutor.questiongeneration.dto.AqgSectionDto

/** Stands in for the question generation service: tests set the replies and inspect the calls. */
class StubAqgClient implements AqgClient {
    AqgJobDto generateReply
    AqgJobDto jobReply
    List<AqgMaterialDto> materials = []
    List<AqgSectionDto> sections = []
    String reprocessedMaterialId
    List<AqgOutlineNodeDto> outline = []
    List<AqgParagraphDto> paragraphs = []
    AqgOutlineEditResultDto editReply
    AqgOutlineEditDto lastEdit
    AqgDiscussionSuggestionDto suggestionReply
    AqgDiscussionRequest lastSuggestionRequest

    AqgGenerateRequest lastGenerateRequest
    int getJobCalls = 0

    /** The bean outlives each test (the Spring context is cached), so tests start from a clean stub. */
    void reset() {
        generateReply = null
        jobReply = null
        materials = []
        sections = []
        reprocessedMaterialId = null
        outline = []
        paragraphs = []
        editReply = null
        lastEdit = null
        suggestionReply = null
        lastSuggestionRequest = null
        lastGenerateRequest = null
        getJobCalls = 0
    }

    @Override
    AqgMaterialDto uploadMaterial(int courseId, String filename, byte[] content) {
        return new AqgMaterialDto('material-1', courseId, filename, 'PROCESSING', 0, null, null, null)
    }

    @Override
    List<AqgMaterialDto> listMaterials(int courseId) {
        return materials
    }

    @Override
    List<AqgSectionDto> getSections(String materialId) {
        return sections
    }

    @Override
    AqgMaterialDto reprocessMaterial(String materialId) {
        reprocessedMaterialId = materialId
        return new AqgMaterialDto(materialId, 1, 'a.pdf', 'PROCESSING', 0, null, null, null)
    }

    @Override
    List<AqgOutlineNodeDto> getOutline(String materialId) {
        return outline
    }

    @Override
    List<AqgParagraphDto> getSectionText(String materialId, String path) {
        return paragraphs
    }

    @Override
    AqgOutlineEditResultDto editOutline(String materialId, AqgOutlineEditDto edit) {
        lastEdit = edit
        return editReply
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
}
