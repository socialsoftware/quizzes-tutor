package pt.ulisboa.tecnico.socialsoftware.tutor.questiongeneration

import pt.ulisboa.tecnico.socialsoftware.tutor.questiongeneration.dto.AqgGenerateRequest
import pt.ulisboa.tecnico.socialsoftware.tutor.questiongeneration.dto.AqgJobDto
import pt.ulisboa.tecnico.socialsoftware.tutor.questiongeneration.dto.AqgMaterialDto

/** Stands in for the question generation service: tests set the replies and inspect the calls. */
class StubAqgClient implements AqgClient {
    AqgJobDto generateReply
    AqgJobDto jobReply
    List<AqgMaterialDto> materials = []

    AqgGenerateRequest lastGenerateRequest
    int getJobCalls = 0

    /** The bean outlives each test (the Spring context is cached), so tests start from a clean stub. */
    void reset() {
        generateReply = null
        jobReply = null
        materials = []
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
