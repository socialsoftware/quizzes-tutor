package pt.ulisboa.tecnico.socialsoftware.tutor.question.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.TopicSource;

import java.io.Serializable;

public class TopicSourceDto implements Serializable {
    private String materialId;
    private String sectionPath;

    public TopicSourceDto() {
    }

    public TopicSourceDto(String materialId, String sectionPath) {
        this.materialId = materialId;
        this.sectionPath = sectionPath;
    }

    public TopicSourceDto(TopicSource source) {
        this(source.getMaterialId(), source.getSectionPath());
    }

    public String getMaterialId() { return materialId; }

    public void setMaterialId(String materialId) { this.materialId = materialId; }

    public String getSectionPath() { return sectionPath; }

    public void setSectionPath(String sectionPath) { this.sectionPath = sectionPath; }
}
