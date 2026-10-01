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
}
