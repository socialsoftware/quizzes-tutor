package pt.ulisboa.tecnico.socialsoftware.tutor.questiongeneration.dto;

import java.io.Serializable;

/** A course material as teachers see it; the generation service's own field names stay behind AqgClient. */
public class GenerationMaterialDto implements Serializable {
    private String id;
    private String filename;
    private String status;
    private Integer chunkCount;
    private String parser;
    private Double parseSeconds;
    private String error;
    // How many of its pieces are under some topic; the others are left out of question generation
    private Integer placedChunks = 0;

    public GenerationMaterialDto() {
    }

    public GenerationMaterialDto(AqgMaterialDto material, Integer placedChunks) {
        this(material);
        this.placedChunks = placedChunks == null ? 0 : placedChunks;
    }

    public GenerationMaterialDto(AqgMaterialDto material) {
        this.id = material.id();
        this.filename = material.filename();
        this.status = material.status();
        this.chunkCount = material.chunkCount();
        this.parser = material.parser();
        this.parseSeconds = material.parseSeconds();
        this.error = material.error();
    }

    public String getId() { return id; }

    public void setId(String id) { this.id = id; }

    public String getFilename() { return filename; }

    public void setFilename(String filename) { this.filename = filename; }

    public String getStatus() { return status; }

    public void setStatus(String status) { this.status = status; }

    public Integer getChunkCount() { return chunkCount; }

    public void setChunkCount(Integer chunkCount) { this.chunkCount = chunkCount; }

    public String getParser() { return parser; }

    public void setParser(String parser) { this.parser = parser; }

    public Double getParseSeconds() { return parseSeconds; }

    public void setParseSeconds(Double parseSeconds) { this.parseSeconds = parseSeconds; }

    public String getError() { return error; }

    public void setError(String error) { this.error = error; }

    public Integer getPlacedChunks() { return placedChunks; }

    public void setPlacedChunks(Integer placedChunks) { this.placedChunks = placedChunks; }
}
