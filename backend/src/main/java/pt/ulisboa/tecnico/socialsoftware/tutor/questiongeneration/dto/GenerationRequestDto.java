package pt.ulisboa.tecnico.socialsoftware.tutor.questiongeneration.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class GenerationRequestDto implements Serializable {
    private Integer topicId;
    private String topic;
    private String difficulty;
    private Integer count;
    private String groundingMode;
    private List<String> materialIds = new ArrayList<>();
    private String language;
    private List<String> sections = new ArrayList<>();
    private String focus;
    // True: draw from the document sections linked to the topic (and its subtopics) instead of the
    // materials and sections above
    private Boolean fromTopic;

    public GenerationRequestDto() {
    }

    public Integer getTopicId() { return topicId; }

    public void setTopicId(Integer topicId) { this.topicId = topicId; }

    public String getTopic() { return topic; }

    public void setTopic(String topic) { this.topic = topic; }

    public String getDifficulty() { return difficulty; }

    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }

    public Integer getCount() { return count; }

    public void setCount(Integer count) { this.count = count; }

    public String getGroundingMode() { return groundingMode; }

    public void setGroundingMode(String groundingMode) { this.groundingMode = groundingMode; }

    public List<String> getMaterialIds() { return materialIds; }

    public void setMaterialIds(List<String> materialIds) { this.materialIds = materialIds; }

    public String getLanguage() { return language; }

    public void setLanguage(String language) { this.language = language; }

    public List<String> getSections() { return sections; }

    public void setSections(List<String> sections) { this.sections = sections; }

    public Boolean getFromTopic() { return fromTopic; }

    public void setFromTopic(Boolean fromTopic) { this.fromTopic = fromTopic; }

    public String getFocus() { return focus; }

    public void setFocus(String focus) { this.focus = focus; }
}
