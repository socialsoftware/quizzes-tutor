package pt.ulisboa.tecnico.socialsoftware.tutor.question.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.TopicSource;

import java.io.Serializable;

/** A piece (chunk) of a course material a topic is taught from. */
public class TopicSourceDto implements Serializable {
    private String materialId;
    private String chunkId;

    public TopicSourceDto() {
    }

    public TopicSourceDto(String materialId, String chunkId) {
        this.materialId = materialId;
        this.chunkId = chunkId;
    }

    public TopicSourceDto(TopicSource source) {
        this(source.getMaterialId(), source.getChunkId());
    }

    public String getMaterialId() { return materialId; }

    public void setMaterialId(String materialId) { this.materialId = materialId; }

    public String getChunkId() { return chunkId; }

    public void setChunkId(String chunkId) { this.chunkId = chunkId; }
}
