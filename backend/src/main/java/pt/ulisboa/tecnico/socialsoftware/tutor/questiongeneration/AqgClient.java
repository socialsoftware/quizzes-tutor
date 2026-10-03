package pt.ulisboa.tecnico.socialsoftware.tutor.questiongeneration;

import pt.ulisboa.tecnico.socialsoftware.tutor.questiongeneration.dto.AqgGenerateRequest;
import pt.ulisboa.tecnico.socialsoftware.tutor.questiongeneration.dto.AqgJobDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.questiongeneration.dto.AqgMaterialDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.questiongeneration.dto.AqgSectionDto;

import java.util.List;

/**
 * The only door to the question generation service. It talks HTTP and JSON and never
 * touches its database, so either side can change without the other noticing.
 */
public interface AqgClient {
    AqgMaterialDto uploadMaterial(int courseId, String filename, byte[] content);

    List<AqgMaterialDto> listMaterials(int courseId);

    List<AqgSectionDto> getSections(String materialId);

    /** Rebuilds a material's chunks (and so its sections) with the service's current parsers. */
    AqgMaterialDto reprocessMaterial(String materialId);

    AqgJobDto generate(AqgGenerateRequest request);

    AqgJobDto getJob(String aqgJobId);
}
