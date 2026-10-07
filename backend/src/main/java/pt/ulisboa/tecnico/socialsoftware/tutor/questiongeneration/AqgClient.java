package pt.ulisboa.tecnico.socialsoftware.tutor.questiongeneration;

import pt.ulisboa.tecnico.socialsoftware.tutor.questiongeneration.dto.AqgGenerateRequest;
import pt.ulisboa.tecnico.socialsoftware.tutor.questiongeneration.dto.AqgJobDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.questiongeneration.dto.AqgOutlineEditDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.questiongeneration.dto.AqgOutlineEditResultDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.questiongeneration.dto.AqgOutlineNodeDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.questiongeneration.dto.AqgParagraphDto;
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

    /** The headings of a material, each with the text under it. */
    List<AqgOutlineNodeDto> getOutline(String materialId);

    /** The paragraphs a section has of its own. */
    List<AqgParagraphDto> getSectionText(String materialId, String path);

    /** Changes the sections of a material; the reply says where the text of each changed section went. */
    AqgOutlineEditResultDto editOutline(String materialId, AqgOutlineEditDto edit);

    AqgJobDto generate(AqgGenerateRequest request);

    AqgJobDto getJob(String aqgJobId);
}
